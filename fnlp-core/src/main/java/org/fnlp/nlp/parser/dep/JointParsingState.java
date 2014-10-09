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

import java.util.ArrayList;
import java.util.List;

import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.ISparseVector;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.dep.ParsingState.Action;

import gnu.trove.list.array.TIntArrayList;

/**
 * 句法分析过程中的状态，及在此状态上的一系列操作
 * 
 * 句法分析由状态的转换完成，转换操作涉及在当前状态提取特征，动作执行。 动作的预测在Parser 中完成
 * 
 * @author xpqiu
 */
public class JointParsingState{

	
	private static final String END = "E*";
	private static final String START = "S*";
	
	private static final String CH_L_LEX = "/LL/";
	private static final String CH_R_LEX = "/RL/";
	private static final String CH_R_POS = "/RP/";	
	private static final String CH_L_POS = "/LP/";
	private static final String CH_R_DEP = "/RD/";	
	private static final String CH_L_DEP= "/LD/";
	private static final String NULL = "N*";

	
	
	private static final String LEX = "/L/";
	private static final String POS = "/P/";
	private int ysize = 0;

	/**
	 * 动作类型
	 * @author xpqiu
	 *
	 */
	public enum Action {
		SHIFT, LEFT, RIGHT, NONE
	}

	protected Sentence sent;
	protected List<DependencyTree> trees;
	protected int leftFocus;

	// 非SHIFT动作中概率较大的动作的概率
	protected float[] probsOfBuild;

	// 非SHIFT动作中概率较大的动作
	protected Action[] actionsOfBuild;

	// 是否执行过非SHIFT动作
	protected boolean isUpdated = false;

	protected boolean isFinal = false;
	private String[] depClassOfBuild;

	/**
	 * 构造函数
	 * 
	 * 由句子实例初始化状态
	 * 
	 * @param instance
	 *            句子实例
	 * @param factory2 
	 */

	public JointParsingState(Sentence instance) {
		trees = new ArrayList<DependencyTree>();
		for (int i = 0; i < instance.length(); i++) {
			String word = instance.getWordAt(i);
			String pos = instance.getTagAt(i);
			DependencyTree tree = new DependencyTree(i, word, pos);
			trees.add(tree);
		}
		this.sent = instance;
		if(trees.size()==0)
			return;

		probsOfBuild = new float[trees.size() - 1];
		actionsOfBuild = new Action[trees.size() - 1];
		depClassOfBuild = new String[trees.size()-1];
	}
	
	

	/**
	 * 得到当前状态的特征
	 * 
	 * @return 特征表
	 * @throws Exception
	 */
	public ArrayList<String> getFeatures() {
		if (isFinalState())
			return null;
		ArrayList<String> featurelist = new ArrayList<String>();

		int rightFocus = leftFocus + 1;

//		ISparseVector vec = new HashSparseVector();
		//所有的联合feature
		featurelist.add(combinedFeature("+0+1", POS, new int[]{0, 1}));
		featurelist.add(combinedFeature("-1+0+1", POS, new int[]{-1, 0, 1}));
		featurelist.add(combinedFeature("+0+1+2", POS, new int[]{0, 1, 2}));
		featurelist.add(combinedFeature("+1+2+3", POS, new int[]{1, 2, 3}));
		featurelist.add(combinedFeature("-2+3+4", POS, new int[]{2, 3, 4}));
		featurelist.add(combinedFeature("+0+1", LEX, new int[]{0, 1}));
		featurelist.add(combinedFeature("-1+0+1", LEX, new int[]{-1, 0, 1}));
		featurelist.add(combinedFeature("+0+1+2", LEX, new int[]{0, 1, 2}));

		// 设定上下文窗口大小
		int l = 2;
		int r = 4;
		for (int i = 0; i <= l; i++) {
			// 特征前缀
			String posFeature = "-" + String.valueOf(i) + POS;
			String lexFeature = "-" + String.valueOf(i) + LEX;

			String lcLexFeature = "-" + String.valueOf(i)
					+ CH_L_LEX;
			String lcPosFeature = "-" + String.valueOf(i)
					+ CH_L_POS;
			String rcLexFeature = "-" + String.valueOf(i)
					+ CH_R_LEX;
			String rcPosFeature = "-" + String.valueOf(i)
					+ CH_R_POS;
			String lcDepFeature = "-" + String.valueOf(i)
					+ CH_L_DEP;			
			String rcDepFeature = "-" + String.valueOf(i)
					+ CH_R_DEP;

			if (leftFocus - i < 0) {
				featurelist.add(lexFeature + START + String.valueOf(i - leftFocus));
				featurelist.add(posFeature + START + String.valueOf(i - leftFocus));
			} else {
				featurelist.add(lexFeature + sent.words[trees.get(leftFocus - i).id]);
				featurelist.add(posFeature + sent.tags[trees.get(leftFocus - i).id]);

				if (trees.get(leftFocus - i).leftChilds.size() != 0) {
					for (int j = 0; j < trees.get(leftFocus - i).leftChilds
							.size(); j++) {
						int leftChildIndex = trees.get(leftFocus - i).leftChilds
								.get(j).id;
						featurelist.add(lcLexFeature
								+ sent.words[leftChildIndex]);
						featurelist.add(lcPosFeature
								+ sent.tags[leftChildIndex]);
						featurelist.add(lcDepFeature
								+ sent.getDepClass(leftChildIndex));
					}
				}else{
					featurelist.add(lcLexFeature + NULL);
					featurelist.add(lcPosFeature + NULL);
				}

				if (trees.get(leftFocus - i).rightChilds.size() != 0) {
					for (int j = 0; j < trees.get(leftFocus - i).rightChilds
							.size(); j++) {
						int rightChildIndex = trees.get(leftFocus - i).rightChilds
								.get(j).id;
						featurelist.add(rcLexFeature
								+ sent.words[rightChildIndex]);
						featurelist.add(rcPosFeature
								+ sent.tags[rightChildIndex]);
						featurelist.add(rcDepFeature
								+ sent.getDepClass(rightChildIndex));
					}
				}else{
					featurelist.add(rcLexFeature + NULL);
					featurelist.add(rcPosFeature + NULL);
				}
			}
		}

		for (int i = 0; i <= r; i++) {
			String posFeature = "+" + String.valueOf(i) + POS;
			String lexFeature = "+" + String.valueOf(i) + LEX;

			String lcLexFeature = "+" + String.valueOf(i)
					+ CH_L_LEX;
			String rcLexFeature = "+" + String.valueOf(i)
					+ CH_R_LEX;
			String lcPosFeature = "+" + String.valueOf(i)
					+ CH_L_POS;			
			String rcPosFeature = "+" + String.valueOf(i)
					+ CH_R_POS;
			String lcDepFeature = "+" + String.valueOf(i)
					+ CH_L_DEP;			
			String rcDepFeature = "+" + String.valueOf(i)
					+ CH_R_DEP;

			if (rightFocus + i >= trees.size()) {
				featurelist.add(lexFeature+ END+ String.valueOf(rightFocus + i- trees.size() + 3));
				featurelist.add(posFeature+ END+ String.valueOf(rightFocus + i- trees.size() + 3));
			} else {
				featurelist.add(lexFeature+ sent.words[trees.get(rightFocus + i).id]);
				featurelist.add(posFeature+ sent.tags[trees.get(rightFocus + i).id]);

				if (trees.get(rightFocus + i).leftChilds.size() != 0) {
					for (int j = 0; j < trees.get(rightFocus + i).leftChilds
							.size(); j++) {
						int leftChildIndex = trees.get(rightFocus + i).leftChilds
								.get(j).id;
						featurelist.add(lcLexFeature+ sent.words[leftChildIndex]);
						featurelist.add(lcPosFeature+ sent.tags[leftChildIndex]);
						featurelist.add(lcDepFeature+ sent.getDepClass(leftChildIndex));
					}
				}else{
					featurelist.add(lcLexFeature + NULL);
					featurelist.add(lcPosFeature + NULL);
				}

				if (trees.get(rightFocus + i).rightChilds.size() != 0) {
					for (int j = 0; j < trees.get(rightFocus + i).rightChilds
							.size(); j++) {
						int rightChildIndex = trees.get(rightFocus + i).rightChilds
								.get(j).id;
						featurelist.add(rcLexFeature+ sent.words[rightChildIndex]);
						featurelist.add(rcPosFeature+ sent.tags[rightChildIndex]);
						featurelist.add(rcDepFeature+ sent.getDepClass(rightChildIndex));
					}
				}else{
					featurelist.add(rcLexFeature + NULL);
					featurelist.add(rcPosFeature + NULL);
				}
			}
		}
		
		
		return featurelist;
	}
	
	/**
	 * 
	 * @param sign 
	 * 		该类feature在字符串中的标志，如"-0+0"
	 * @param posOrLex
	 * 		该feature是取pos还是lex
	 * @param locations
	 * 		选取的这些联合feature的位置，以leftFocus为准的偏移量
	 * @return 
	 * 		联合feature的字符串形式
	 */
	private String combinedFeature(String sign, String posOrLex, int[] locations){
		StringBuilder cf = new StringBuilder();
		cf.append(sign);
		cf.append(posOrLex);
		for(int loc:locations){
			int focus = leftFocus + loc;
			if(isCrossBorder(focus)){
				cf.append(NULL);
			}
			else{
				cf.append(getPosOrLex(posOrLex, focus));
			}
			cf.append("/");
		}
		return cf.toString();
	}
	
	private String getPosOrLex(String posOrLex, int focus){
		if(posOrLex.equals(LEX)){
			return trees.get(focus).word;
		}
		else if(posOrLex.equals(POS)){
			return trees.get(focus).pos;
		}
		return null;
	}
	
	private boolean isCrossBorder(int focus){
		if(focus >= 0 && focus < trees.size()){
			return false;	
		}
		return true;
	}

	public boolean isFinalState() {
		return trees.size()==0||trees.size() == 1 || isFinal;
	}

	/**
	 * 状态转换，动作为SHIFT
	 * 
	 * 动作为SHIFT，但保存第二大可能的动作，当一列动作都是SHIFT时，执行概率最大的第二大动作
	 * 
	 * @param action
	 *            第二大可能的动作
	 * @param prob
	 *            第二大可能的动作的概率
	 */
	public void next(Action action, float prob,String depClass) {
		probsOfBuild[leftFocus] = prob;
		actionsOfBuild[leftFocus] = action;
		depClassOfBuild[leftFocus] = depClass;
		leftFocus++;

		if (leftFocus >= trees.size() - 1) {
			if (!isUpdated) {
				int maxIndex = 0;
				float maxValue = Float.NEGATIVE_INFINITY;
				for (int i = 0; i < probsOfBuild.length; i++)
					if (probsOfBuild[i] > maxValue) {
						maxValue = probsOfBuild[i];
						maxIndex = i;
					}
				leftFocus = maxIndex;
				next(actionsOfBuild[leftFocus],depClassOfBuild[leftFocus]);
			}

			back();
		}
	}

	/**
	 * 状态转换, 执行动作
	 * 
	 * @param action
	 *            要执行的动作
	 */
	public void next(Action action,String depClass) {

		assert (!isFinalState());

		// 左焦点词在句子中的位置
		int lNode = trees.get(leftFocus).id;
		int rNode = trees.get(leftFocus + 1).id;

		switch (action) {
		case LEFT:
			trees.get(leftFocus + 1).setDepClass(depClass);  			
			trees.get(leftFocus).addRightChild(trees.get(leftFocus + 1));
			trees.remove(leftFocus + 1);
			isUpdated = true;

			break;
		case RIGHT:
			trees.get(leftFocus).setDepClass(depClass);			
			trees.get(leftFocus + 1).addLeftChild(trees.get(leftFocus));
			trees.remove(leftFocus);
			isUpdated = true;
			break;
		default:
			leftFocus++;
		}

		if (leftFocus >= trees.size() - 1) {
			if (!isUpdated) {
				isFinal = true;
			}
			back();
		}
	}
	public int[] getFocusIndices() {
		assert (!isFinalState());

		int[] indices = new int[2];
		indices[0] = trees.get(leftFocus).id;
		indices[1] = trees.get(leftFocus + 1).id;
		return indices;
	}
	/**
	 * 将序列第一二个词设为焦点词
	 */
	protected void back() {
		isUpdated = false;
		leftFocus = 0;

		probsOfBuild = new float[trees.size() - 1];
		actionsOfBuild = new Action[trees.size() - 1];
		depClassOfBuild = new String[trees.size() - 1];
	}

}