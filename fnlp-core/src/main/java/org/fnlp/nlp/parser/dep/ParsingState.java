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

import java.util.*;

import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.parser.Sentence;

/**
 * 句法分析过程中的状态，及在此状态上的一系列操作
 * 
 * 句法分析由状态的转换完成，转换操作涉及在当前状态提取特征，动作执行。 动作的预测在Parser 中完成
 * 
 * @author cshen
 * @version Feb 16, 2009
 * @see YamadaParser
 */
public class ParsingState {
	/**
	 * 动作类型
	 */
	public enum Action {
		SHIFT, LEFT, RIGHT
	}

	protected static final int DefaultLabelNum = 25;

	protected Sentence instance;
	protected List<DependencyTree> trees;
	protected int leftFocus;

	// 非SHIFT动作中概率较大的动作的概率
	protected float[] probsOfBuild;

	// 非SHIFT动作中概率较大的动作
	protected Action[] actionsOfBuild;

	// 是否执行过非SHIFT动作
	protected boolean isUpdated = false;

	protected boolean isFinal = false;
	protected AlphabetFactory factory;

	/**
	 * 构造函数
	 * 
	 * 由句子实例初始化状态
	 * 
	 * @param instance
	 *            句子实例
	 */
	public ParsingState(Sentence instance,AlphabetFactory factory2) {
		factory = factory2;
		trees = new ArrayList<DependencyTree>();
		for (int i = 0; i < instance.length(); i++) {
			String word = instance.getWordAt(i);
			String pos = instance.getTagAt(i);
			DependencyTree tree = new DependencyTree(i, word, pos);
			trees.add(tree);
		}
		this.instance = instance;

		probsOfBuild = new float[trees.size() - 1];
		actionsOfBuild = new Action[trees.size() - 1];
	}

	public boolean isFinalState() {
		return trees.size() == 1 || isFinal;
	}

	public int[] getFocusIndices() {
		assert (!isFinalState());

		int[] indices = new int[2];
		indices[0] = trees.get(leftFocus).id;
		indices[1] = trees.get(leftFocus + 1).id;
		return indices;
	}
	
	public String getLeftPos()	{
		return trees.get(leftFocus).pos;
	}

	/**
	 * 得到当前状态的特征
	 * 
	 * @return 特征表，其中key是有用的，value没有用
	 */
	public HashSparseVector getFeatures() {
		if (isFinalState())
			return null;
		
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		int rightFocus = leftFocus + 1;

		HashSparseVector vec = new HashSparseVector();

		// 设定上下文窗口大小
		int l = 2;
		int r = 2;
		for (int i = 0; i <= l; i++) {
			// 词性特征前缀
			String posFeature = "-" + new Integer(i).toString() + "/pos/";
			String lexFeature = "-" + new Integer(i).toString() + "/lex/";

			String lcLexFeature = "-" + new Integer(i).toString()
					+ "/ch-L-lex/";
			String lcPosFeature = "-" + new Integer(i).toString()
					+ "/ch-L-pos/";
			String rcLexFeature = "-" + new Integer(i).toString()
					+ "/ch-R-lex/";
			String rcPosFeature = "-" + new Integer(i).toString()
					+ "/ch-R-pos/";

			if (leftFocus - i < 0) {
				addFeature(features, vec,
						lexFeature + "START" + String.valueOf(i - leftFocus));
				addFeature(features, vec,
						posFeature + "START" + String.valueOf(i - leftFocus));
			} else {
				addFeature(
						features,
						vec,
						lexFeature
								+ instance.words[trees.get(leftFocus - i).id]);
				addFeature(features, vec, posFeature
						+ instance.tags[trees.get(leftFocus - i).id]);

				if (trees.get(leftFocus - i).leftChilds.size() != 0) {
					for (int j = 0; j < trees.get(leftFocus - i).leftChilds
							.size(); j++) {
						int leftChildIndex = trees.get(leftFocus - i).leftChilds
								.get(j).id;
						addFeature(features, vec, lcLexFeature
								+ instance.words[leftChildIndex]);
						addFeature(features, vec, lcPosFeature
								+ instance.tags[leftChildIndex]);
					}
				}

				if (trees.get(leftFocus - i).rightChilds.size() != 0) {
					for (int j = 0; j < trees.get(leftFocus - i).rightChilds
							.size(); j++) {
						int rightChildIndex = trees.get(leftFocus - i).rightChilds
								.get(j).id;
						addFeature(features, vec, rcLexFeature
								+ instance.words[rightChildIndex]);
						addFeature(features, vec, rcPosFeature
								+ instance.tags[rightChildIndex]);
					}
				}
			}
		}

		for (int i = 0; i <= r; i++) {
			String posFeature = "+" + new Integer(i).toString() + "/pos/";
			String lexFeature = "+" + new Integer(i).toString() + "/lex/";

			String lcLexFeature = "+" + new Integer(i).toString()
					+ "/ch-L-lex/";
			String lcPosFeature = "+" + new Integer(i).toString()
					+ "/ch-L-pos/";
			String rcLexFeature = "+" + new Integer(i).toString()
					+ "/ch-R-lex/";
			String rcPosFeature = "+" + new Integer(i).toString()
					+ "/ch-R-pos/";

			if (rightFocus + i >= trees.size()) {
				addFeature(
						features,
						vec,
						lexFeature
								+ "END"
								+ String.valueOf(rightFocus + i
										- trees.size() + 3));
				addFeature(
						features,
						vec,
						posFeature
								+ "END"
								+ String.valueOf(rightFocus + i
										- trees.size() + 3));
			} else {
				addFeature(
						features,
						vec,
						lexFeature
								+ instance.words[trees.get(rightFocus + i).id]);
				addFeature(features, vec, posFeature
						+ instance.tags[trees.get(rightFocus + i).id]);

				if (trees.get(rightFocus + i).leftChilds.size() != 0) {
					for (int j = 0; j < trees.get(rightFocus + i).leftChilds
							.size(); j++) {
						int leftChildIndex = trees.get(rightFocus + i).leftChilds
								.get(j).id;
						addFeature(features, vec, lcLexFeature
								+ instance.words[leftChildIndex]);
						addFeature(features, vec, lcPosFeature
								+ instance.tags[leftChildIndex]);
					}
				}

				if (trees.get(rightFocus + i).rightChilds.size() != 0) {
					for (int j = 0; j < trees.get(rightFocus + i).rightChilds
							.size(); j++) {
						int rightChildIndex = trees.get(rightFocus + i).rightChilds
								.get(j).id;
						addFeature(features, vec, rcLexFeature
								+ instance.words[rightChildIndex]);
						addFeature(features, vec, rcPosFeature
								+ instance.tags[rightChildIndex]);
					}
				}
			}
		}

		return vec;
	}

	protected void addFeature(IFeatureAlphabet features, HashSparseVector vec,
			String str) {
		int idx = features.lookupIndex(str, DefaultLabelNum);
		if (idx != -1) {
			vec.put(idx, 1);
		}
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
	public void next(Action action, float prob) {
		probsOfBuild[leftFocus] = prob;
		actionsOfBuild[leftFocus] = action;
		leftFocus++;

		if (leftFocus >= trees.size() - 1) {
			if (!isUpdated) {
				int maxIndex = 0;
				float maxValue = 0;
				for (int i = 0; i < probsOfBuild.length; i++)
					if (probsOfBuild[i] > maxValue) {
						maxValue = probsOfBuild[i];
						maxIndex = i;
					}
				leftFocus = maxIndex;
				next(actionsOfBuild[leftFocus]);
			}

			back();
		}
	}

	/**
	 * 将序列第一二个词设为焦点词
	 */
	protected void back() {
		isUpdated = false;
		leftFocus = 0;

		probsOfBuild = new float[trees.size() - 1];
		actionsOfBuild = new Action[trees.size() - 1];
	}

	/**
	 * 状态转换, 执行动作
	 * 
	 * @param action
	 *            要执行的动作
	 */
	public void next(Action action) {
		// assert (action.equalsIgnoreCase("left")
		// || action.equalsIgnoreCase("right") || action
		// .equalsIgnoreCase("shift"));
		assert (!isFinalState());

		// 左焦点词在句子中的位置
		int lNode = trees.get(leftFocus).id;
		int rNode = trees.get(leftFocus + 1).id;

		switch (action) {
		case LEFT:
			// add for counting two types of errors
			// if (instance.heads[rNode] == lNode)
			// break;

			// end

			trees.get(leftFocus).addRightChild(trees.get(leftFocus + 1));
			trees.remove(leftFocus + 1);
			isUpdated = true;

			break;
		case RIGHT:

			// add for counting two types of errors
			// if (instance.heads[lNode] == rNode)
			// break;

			// end

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

}