/**
*  This file is part of FNLP (formerly FudanNLP).
*  
*  FNLP is free software: you can redistribute it and/or modify
*  it under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  FNLP is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
*  
*  Copyright 2009-2014 www.fnlp.org. All rights reserved. 
*/

package org.fnlp.nlp.parser.dep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.Target;
import org.fnlp.util.exception.LoadModelException;
import org.fnlp.util.exception.UnsupportedDataTypeException;

import gnu.trove.list.array.TIntArrayList;

/**
 * 依赖句法分析器类，同时标注依赖关系类型
 * 
 * 输入单个分完词的句子(包含词性)，使用Yamada分析算法完成依存结构分析。
 * 
 * @author 
 */
public class JointParser implements Serializable{

	private static final long serialVersionUID = 7114734594734593632L;
	private int ysize;
	private AlphabetFactory factory;
	private Linear models;
	private IFeatureAlphabet fa;
	private LabelAlphabet la;



	/**
	 * 构造函数
	 * 
	 * @param modelfile
	 *            模型目录
	 * @throws LoadModelException 
	 */
	public JointParser(String modelfile) throws LoadModelException  {		
		models = Linear.loadFrom(modelfile);
		factory = models.getAlphabetFactory();
		fa = factory.DefaultFeatureAlphabet();
		la  = factory.DefaultLabelAlphabet();
		ysize = la.size();
		factory.setStopIncrement(true);
	}

	public static int[] addFeature(IFeatureAlphabet fa, ArrayList<String> str,  int ysize) {
		TIntArrayList indices = new TIntArrayList();
		String constant = "////";
		str.add(constant);
		for(String s: str){
			int i = fa.lookupIndex(s,ysize);
			if(i!=-1)
				indices.add(i);
		}
		return indices.toArray();
	}

	private void doNext(String action,JointParsingState state){
		char act = action.charAt(0);
		String relation = action.substring(1);
		switch(act){
		case 'L':
			state.next(JointParsingState.Action.LEFT, relation);break;		
		case 'R':
			state.next(JointParsingState.Action.RIGHT, relation);break;
		default:
			System.out.println("状态动作错误");
		}

	}

	private void doNext(String action,float est1,JointParsingState state){
		char act = action.charAt(0);
		String relation = action.substring(1);
		switch(act){
		case 'L':
			state.next(JointParsingState.Action.LEFT, est1,relation);break;
		case 'R':
			state.next(JointParsingState.Action.RIGHT, est1,relation);break;
		default:
			System.out.println("状态动作错误");
		}

	}


	private Predict<DependencyTree> _getBestParse(Sentence sent){
		float score = 0.0f;

		// 分析中的状态
		JointParsingState state = new JointParsingState(sent);
		while (!state.isFinalState()) {

			Predict<String> estimates;
			estimates = estimateActions(state);

			String action = estimates.getLabel(0);
			if (!action.equals("S")){
				doNext(action ,state);
				score +=estimates.getScore(0);
			}
			else{
				action = estimates.getLabel(1);
				float s = estimates.getScore(1);
				doNext(action,s,state);
				score +=estimates.getScore(1);
			}
		}
		Predict<DependencyTree> res = new Predict<DependencyTree>();
		res.add(state.trees.get(0),score);
		return res;
	}

	/**
	 * 动作预测
	 * 
	 * 根据当前状态得到的特征，和训练好的模型，预测当前状态应采取的策略，用在测试中
	 * 
	 * @param featureAlphabet
	 *            特征名到特征ID的对应表，特征抽取时使用特征名，模型中使用特征ID，
	 * @param model
	 *            分类模型
	 * @param features
	 *            当前状态的特征
	 * @return 动作及其概率 ［［动作1，概率1］，［动作2，概率2］，［动作3，概率3］］ 动作： 1->LEFT; 2->RIGHT;
	 *         0->SHIFT
	 */
	private Predict<String> estimateActions(JointParsingState state) {
		// 当前状态的特征
		ArrayList<String> features = state.getFeatures();
		Instance inst = new Instance(addFeature(fa, features, ysize));

		Predict<Integer> ret = models.classify(inst,ysize);
		ret.normalize();
		Predict<String> result =new Predict<String>(2);
		float total = 0;
		for (int i = 0; i < 2; i++) {
			Integer guess = ret.getLabel(i);
			if(guess==null) //bug：可能为空，待修改。 xpqiu
				break;
			String action = la.lookupString(guess);
			result.add(action,ret.getScore(i));	
		}


		return result;
	}

	public Target jointParse(Instance inst)  {
		Sentence sent = (Sentence) inst;

		Predict<DependencyTree> res = _getBestParse(sent);
		DependencyTree dt = res.getLabel(0);

		Target target = new Target(sent.length());

		target=target.ValueOf(dt);

		return target;
	}

	public int[] parse(Instance inst)	{
		return (int[]) getBest(inst).getLabel(0);
	}

//	public int[] parse(String[][] strings) {
//		return parse(new Sentence(strings));
//	}

	public int[] parse(String[] words, String[] pos)	{
		return parse(new Sentence(words, pos));
	}
	
	public Predict<int[]> getBest(Instance inst) {
		Sentence sent = (Sentence) inst;

		Predict<DependencyTree> res = _getBestParse(sent);
		float score = res.getScore(0);
		DependencyTree dt = res.getLabel(0);

		Predict<int[]> ret = new Predict<int[]>();
		int[] preds = new int[sent.length()];
		Arrays.fill(preds, -1);
		DependencyTree.toArrays(dt, preds);
		ret.add(preds,score);
		return ret;
	}

	public DependencyTree parse2T(Sentence sent) {
		Predict<DependencyTree> res = _getBestParse(sent);
		DependencyTree dt = res.getLabel(0);
		return dt;
	}
	/**
	 * 得到依存句法树
	 * @param words 词数组
	 * @param pos 词性数组
	 */
	public DependencyTree parse2T(String[] words, String[] pos){
		return parse2T(new Sentence(words, pos));
	}

	public Target parse2R(Instance inst) 	{
		return  jointParse(inst);
	}

	public Target parse2R(String[] words, String[] pos) 	{
		return parse2R(new Sentence(words, pos));
	}

	public String parse2String(String[] words, String[] pos,boolean b) {
		Target target = parse2R(words,pos);
		int[] heads = target.getHeads();
		String[] rel = target.getRelations();
		StringBuffer sb = new StringBuffer();
		if(b){
			for(int j = 0; j < words.length; j++){
				sb.append(words[j]);
				if(j<words.length-1)
					sb.append(" ");
			}
			sb.append("\n");
			for(int j = 0; j < pos.length; j++){
				sb.append(pos[j]);
				if(j<pos.length-1)
					sb.append(" ");
			}
			sb.append("\n");

		}
		for(int j = 0; j < heads.length; j++){
			sb.append(heads[j]);
			if(j<heads.length-1)
				sb.append(" ");
		}
		sb.append("\n");
		for(int j = 0; j < rel.length; j++){
			if(rel[j]==null)
				sb.append("核心词");
			else
				sb.append(rel[j]);
			if(j<heads.length-1)
				sb.append(" ");
		}
		return sb.toString();
	}

	
	/**
	 * 得到支持的依存关系类型集合
	 * @return 词性标签集合
	 */
	public Set<String> getSupportedTypes(){
		Set<String> typeset = new HashSet<String>();
		Set<String> set = factory.DefaultLabelAlphabet().toSet();
		Iterator<String> itt = set.iterator();
		while(itt.hasNext()){
			String type = itt.next();
			if(type.length() ==1 )
				continue;
			typeset.add(type.substring(1));
		}
		return typeset;
	}


}