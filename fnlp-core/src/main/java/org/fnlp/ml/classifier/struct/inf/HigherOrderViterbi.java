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

package org.fnlp.ml.classifier.struct.inf;

import java.util.Arrays;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
import org.fnlp.util.MyArrays;

/**
 * 任意阶Viterbi算法
 * 
 * @author xpqiu
 * 
 */
public class HigherOrderViterbi extends AbstractViterbi {

	private static final long serialVersionUID = 6023318778006156804L;

	

	/**
	 * 构造函数
	 * 
	 * @param numLabels
	 *            标记
	 * @param templets
	 *            模板
	 */
	public HigherOrderViterbi(TempletGroup templets, int numLabels) {
		this.ysize = numLabels;
		this.templets = templets;
		this.templets.calc(numLabels);
		this.numTemplets = templets.size();
		numStates = templets.numStates;
	}

	/**
	 * 标记给定实例
	 * 
	 * @param instance
	 */
	public Predict<int[]> getBest(Instance instance, int nbest) {
		int[][] data;
		/**
		 * 节点矩阵
		 */
		Node[][] lattice;

		data = (int[][]) instance.getData();
		// target = (int[]) instance.getTarget();
		lattice = new Node[data.length][getTemplets().numStates];
		for (int ip = 0; ip < data.length; ip++)
			for (int s = 0; s < getTemplets().numStates; s++)
				lattice[ip][s] = new Node(nbest);

		for (int ip = 0; ip < data.length; ip++) {
			// 对于每一个n阶的可能组合
			for (int s = 0; s < numStates; s++) {
				// 计算所有特征的权重和
				for (int t = 0; t < numTemplets; t++) {
					if (data[ip][t] == -1)
						continue;
					lattice[ip][s].weight += weights[data[ip][t]
							+ getTemplets().offset[t][s]];
				}
			}
		}
		for (int s = 0; s < ysize; s++) {
			lattice[0][s].best[0] = lattice[0][s].weight;
		}
		float[] best = new float[nbest];
		Integer[] prev = new Integer[nbest];
		for (int ip = 1; ip < data.length; ip++) {
			for (int s = 0; s < numStates; s += ysize) {
				Arrays.fill(best, Float.NEGATIVE_INFINITY);
				for (int k = 0; k < ysize; k++) {
					int sp = (k * getTemplets().numStates + s) / ysize;
					for (int ibest = 0; ibest < nbest; ibest++) {
						float b = lattice[ip - 1][sp].best[ibest];
						MyArrays.addBest(best, prev, b, sp * nbest + ibest);
					}
				}
				for (int r = s; r < s + ysize; r++) {
					for (int n = 0; n < nbest; n++) {
						lattice[ip][r].best[n] = best[n]
								+ lattice[ip][r].weight;
						lattice[ip][r].prev[n] = prev[n];
					}
				}
			}
		}

		Predict<int[]> res = getPath(lattice, nbest);

		return res;
	}
	
	public Predict<int[]> getBest(Instance instance)	{
		return getBest(instance, 1);
	}

	private Predict<int[]> getPath(Node[][] lattice, int nbest) {
		float best;
		Node lastNode = new Node(nbest);
		int last = lattice.length - 1;
		for (int s = 0; s < getTemplets().numStates; s++) {
			for (int ibest = 0; ibest < nbest; ibest++) {
				best = lattice[last][s].best[ibest];
				lastNode.addBest(best, s * nbest + ibest);
			}
		}

		Predict<int[]> res = new Predict<int[]>(nbest);
		for (int k = 0; k < nbest; k++) {
			int[] path = new int[lattice.length];
			int p = last;
			int s = lastNode.prev[k];
			float score = lastNode.best[k];

			for (int d = s / nbest, i = 0; i < getTemplets().maxOrder && p >= 0; i++, p--) {
				path[p] = d % ysize;
				d = d / ysize;
			}
			while (p >= 0) {
				path[p] = s / nbest / getTemplets().base[getTemplets().maxOrder];
				s = lattice[p + getTemplets().maxOrder][s / nbest].prev[s % nbest];
				--p;
			}
			res.add(path,score);
		}
		return res;
	}

	public final class Node {

		int n;
		float weight = 0.0f;
		float[] best;
		int[] prev;

		public Node(int n) {
			this.n = n;
			best = new float[n];
			prev = new int[n];

		}

		/**
		 * 记录之前的label和得分，保留前n个
		 * 
		 * @param score
		 * @param p
		 */
		public int addBest(float score, int p) {
			int i;
			for (i = 0; i < n; i++) {
				if (score > best[i])
					break;
			}
			if (i >= n)
				return -1;
			for (int k = n - 2; k >= i; k--) {
				best[k + 1] = best[k];
				prev[k + 1] = prev[k];
			}
			best[i] = score;
			prev[i] = p;
			return i;
		}

		public String toString() {
			return String.format("%f %f %d", weight, best[0], prev[0]);
		}
	}

}