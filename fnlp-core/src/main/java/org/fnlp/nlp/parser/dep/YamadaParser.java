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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.util.exception.UnsupportedDataTypeException;

/**
 * 依赖句法分析器类
 * 
 * 输入单个分完词的句子(包含词性)，使用Yamada分析算法完成依存结构分析。
 * 
 * @author cshen
 * @version Feb 16, 2009
 */
public class YamadaParser extends Inferencer {

	private static final long serialVersionUID = 7114734594734593632L;
	
	// 对于左焦点词的每个词性，保存一张特征名到特征ID的对应表
	LabelAlphabet postagAlphabet;
	// 对于左焦点词的每个词性，有一个分类模型
	public Linear[] models;
	public AlphabetFactory factory;
	
	
	/**
	 * 缺省词性,
	 * 如果词性在训练语料中没有使用过，用缺省词性代替，比如”名词“
	 * 默认为null，不进行替换。遇到新词性时抛出异常
	 */
	protected String defaultPOS = "名词";
	
	/**
	 * 设置缺省词性
	 * @param pos
	 * @throws UnsupportedDataTypeException 
	 */
	
	public void setDefaultPOS(String pos) throws UnsupportedDataTypeException{
		int lpos = postagAlphabet.lookupIndex(pos);
		if(lpos==-1){			
				throw new UnsupportedDataTypeException("不支持词性："+pos);
		}
		defaultPOS = pos;
	}

	/**
	 * 构造函数
	 * 
	 * @param modelfile
	 *            模型目录
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public YamadaParser(String modelfile) throws IOException, ClassNotFoundException {
		loadModel(modelfile);
		factory.setStopIncrement(true);
		postagAlphabet = factory.buildLabelAlphabet("postag");
	}
	
	private Predict<DependencyTree> _getBestParse(Sentence sent){
		float score = 0;

		// 分析中的状态
		ParsingState state = new ParsingState(sent,factory);

		postagAlphabet = factory.buildLabelAlphabet("postag");

		while (!state.isFinalState()) {

			float[][] estimates;
			try {
				estimates = estimateActions(state);
			} catch (UnsupportedDataTypeException e) {
				return null;
			}

			if ((int) estimates[0][0] == 1)
				state.next(ParsingState.Action.LEFT);
			else if ((int) estimates[0][0] == 2)
				state.next(ParsingState.Action.RIGHT);
			else if ((int) estimates[0][1] == 1)
				state.next(ParsingState.Action.LEFT, estimates[1][1]);
			else
				state.next(ParsingState.Action.RIGHT, estimates[1][1]);

			if (estimates[0][0] != 0)
				score += Math.log10(estimates[1][0]);
			else
				score += Math.log10(estimates[1][1]);

		}
		
		score = (float) Math.exp(score);
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
	 * @throws UnsupportedDataTypeException 
	 */
	private float[][] estimateActions(ParsingState state) throws UnsupportedDataTypeException {
		// 当前状态的特征
		HashSparseVector features = state.getFeatures();
		Instance inst = new Instance(features.indices());

		String pos = state.getLeftPos();
		int lpos = postagAlphabet.lookupIndex(pos);
		if(lpos==-1)
			throw new UnsupportedDataTypeException("不支持词性："+pos);
		LabelAlphabet actionList = factory.buildLabelAlphabet(pos);
		
		Predict<Integer> ret = models[lpos].classify(inst, actionList.size());
		Object[] guess = ret.labels;
		
		float[][] result = new float[2][actionList.size()];
		float total = 0;
		for (int i = 0; i < guess.length; i++) {
			if(guess[i]==null) //bug：可能为空，待修改。 xpqiu
				break;
			String action = actionList.lookupString((Integer)guess[i]);
			result[0][i] = 0;
			if (action.matches("L"))
				result[0][i] = 1;
			else if (action.matches("R"))
				result[0][i] = 2;
			result[1][i] = (float) Math.exp(ret.getScore(i));
			total += result[1][i];
		}
		for (int i = 0; i < guess.length; i++) {
			result[1][i] = result[1][i] / total;
		}
		
		return result;
	}

	/**
	 * 分析单个句子
	 * 
	 * @param carrier   句子实例
	 * @param n 
	 * @return 整个句子的得分
	 */
	public Predict<int[]> getBest(Instance carrier, int n) {
		throw new UnsupportedOperationException("Cannot find k-best trees in "
				+ this.getClass().getName());
	}

	/**
	 * 加载模型
	 * 
	 * 以序列化方式加载模型
	 * 
	 * @param modelfile
	 *            模型路径
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void loadModel(String modelfile) throws IOException,
			ClassNotFoundException {
		ObjectInputStream instream = new ObjectInputStream(new GZIPInputStream(
				new FileInputStream(modelfile)));
		factory = (AlphabetFactory) instream.readObject();
		models = (Linear[]) instream.readObject();
		instream.close();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();
		features.setStopIncrement(true);
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
	
	

	public int[] parse(Instance inst)	{
		return (int[]) getBest(inst).getLabel(0);
	}
	
//	public int[] parse(String[][] strings) {
//		return parse(new Sentence(strings));
//	}
	
	public int[] parse(String[] words, String[] pos)	{
		return parse(new Sentence(words, pos));
	}
	
	public DependencyTree getBestParse(Sentence sent)	{
		return _getBestParse(sent).getLabel(0);
	}
	
	public DependencyTree getBestParse(String[] words)	{
		return getBestParse(words, null);
	}
	
	public DependencyTree getBestParse(String[] words, String[] tags)	{
		return getBestParse(new Sentence(words, tags));
	}
	public static void main(String args[]){
		try {
			YamadaParser yp =  new YamadaParser("./tmp/modelConll.mz");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 得到支持的词性标签集合
	 * @return 词性标签集合
	 */
	public Set<String> getSupportedTags(){
		Set<String> tagset = postagAlphabet.toSet();
		return tagset;
	}
	
	
}