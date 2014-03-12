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
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 一阶线性最优序列解码器
 * （修改成可并行版本 2011.9.15）
 * @author Feng Ji
 * 
 */
public class LinearViterbi extends AbstractViterbi {

	private static final long serialVersionUID = -8237762672065700553L;

	

	public LinearViterbi(TempletGroup templets, int ysize) {
		this.ysize = ysize;
		this.setTemplets(templets);
		this.orders = templets.getOrders();
	}
	
	public LinearViterbi(int[] orders, int ysize) {
		this.ysize = ysize;
		this.orders = orders;
	}
	
	public int ysize() {
		return ysize;
	}
	
	public int[] orders()	{
		return orders;
	}

	/**
	 * 构造函数
	 * @param viterbi 一阶线性解码器
	 */
	public LinearViterbi(AbstractViterbi viterbi) {
		this(viterbi.getTemplets(), viterbi.ysize);
		this.weights = viterbi.getWeights();
	}


	@Override
	public Predict<int[]> getBest(Instance carrier) {

		Node[][] node = initialLattice(carrier);

		doForwardViterbi(node, carrier);

		Predict<int[]> res = getPath(node);

		return res;
	}

	/**
	 * 构造并初始化网格
	 * @param carrier 样本实例
	 * @return 推理网格
	 */
	protected Node[][] initialLattice(Instance carrier) {
		int[][] data = (int[][]) carrier.getData();

		int length = carrier.length();

		Node[][] lattice = new Node[length][];
		for (int l = 0; l < length; l++) {
			lattice[l] = new Node[ysize];
			for (int c = 0; c < ysize; c++) {
				lattice[l][c] = new Node(ysize);
				for (int i = 0; i < orders.length; i++) {
					if (data[l][i] == -1 || data[l][i]>=weights.length) //TODO: xpqiu 2013.2.1
						continue;
					if (orders[i] == 0) {
						lattice[l][c].score += weights[data[l][i] + c];
					} else if (orders[i] == 1) {
						int offset = c;
						for (int p = 0; p < ysize; p++) {
							//weights对应trans(c,p)的按行展开
							lattice[l][c].trans[p] += weights[data[l][i]
									+ offset];
							offset += ysize;
						}
					}
				}
			}
		}

		return lattice;
	}

	/**
	 * 前向Viterbi算法
	 * @param lattice 网格
	 * @param carrier 样本实例
	 */
	protected void doForwardViterbi(Node[][] lattice, Instance carrier) {
		for (int l = 1; l < lattice.length; l++) {
			for (int c = 0; c < lattice[l].length; c++) {
				if (lattice[l][c] == null)
					continue;

				float bestScore = Float.NEGATIVE_INFINITY;
				int bestPath = -1;
				for (int p = 0; p < lattice[l - 1].length; p++) {
					if (lattice[l - 1][p] == null)
						continue;
					
					float score = lattice[l - 1][p].score
							+ lattice[l][c].trans[p];
					if (score > bestScore) {
						bestScore = score;
						bestPath = p;
					}
				}
				bestScore += lattice[l][c].score;
				lattice[l][c].addScore(bestScore, bestPath);
			}
		}
	}

	/**
	 * 回溯获得最优路径
	 * @param lattice 网格
	 * @return 最优路径及其得分
	 */
	protected Predict<int[]> getPath(Node[][] lattice) {

		Predict<int[]> res = new Predict<int[]>();
		if (lattice.length == 0)
			return res;

		float max = Float.NEGATIVE_INFINITY;
		int cur = 0;
		for (int c = 0; c < ysize(); c++) {
			if (lattice[lattice.length-1][c] == null)
				continue;
			
			if (lattice[lattice.length - 1][c].score > max) {
				max = lattice[lattice.length - 1][c].score;
				cur = c;
			}
		}

		int[] path = new int[lattice.length];
		path[lattice.length - 1] = cur;
		for (int l = lattice.length - 1; l > 0; l--) {
			cur = lattice[l][cur].prev;
			path[l - 1] = cur;
		}
		res.add(path,max);

		return res;
	}

	final class Node {

		float base = 0;
		float score = 0;
		int prev = -1;
		float[] trans = null;

		public Node(int n) {
			base = 0;
			score = 0;
			prev = -1;
			trans = new float[n];
		}

		public void addScore(float score, int path) {
			this.score = score;
			this.prev = path;
		}

		public void clear() {
			base = 0;
			score = 0;
			prev = -1;
			Arrays.fill(trans, 0);
		}

	}

}