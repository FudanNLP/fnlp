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
import org.fnlp.ml.classifier.struct.inf.AbstractViterbi;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 双链最优解码器
 * 
 * @author Feng Ji
 * 
 */

public class HybridViterbi extends AbstractViterbi {

	private static final long serialVersionUID = 5485421022552472597L;

	private int[] ysize;
	private int length;
	private int[][] orders;

	public HybridViterbi(TempletGroup[] templets, int ssize, int tsize) {
		this.ysize = new int[] { ssize, tsize };
		this.orders = new int[templets.length][];
		for (int i = 0; i < templets.length; i++) {
			this.orders[i] = templets[i].getOrders();
		}
	}

	public int[] ysize() {
		return ysize;
	}

	public int[][] orders() {
		return orders;
	}

	@Override
	public Predict<int[][]> getBest(Instance inst) {
		Node[][][] lattice = initialLattice(inst);
		doForwardViterbi(lattice);
		return getForwardPath(lattice);
	}

	/**
	 * 已知分段结果的情况下，最优双链解码方法
	 * 
	 * @param inst
	 *            样本实例
	 * @return 双链标注结果
	 */
	public Predict<int[][]> getBestWithSegs(Instance inst) {
		Node[][][] lattice = initialLatticeWithSegs(inst);
		doForwardViterbi(lattice);
		return getForwardPath(lattice);
	}

	/**
	 * 已知分段结果的情况下，构造并初始化网格，不经过的节点设置为NULL
	 * 
	 * @param inst
	 *            样本实例
	 * @return 双链网格
	 */
	private Node[][][] initialLatticeWithSegs(Instance inst) {
		int[][][] data = (int[][][]) inst.getData();
		int[] tags = (int[]) inst.getTempData();
		length = inst.length();

		Node[][][] lattice = new Node[2][length][];
		for (int i = 0; i < length; i++) {
			lattice[0][i] = new Node[ysize[0]];
			lattice[0][i][tags[i]] = new Node(ysize[0], ysize[1]);
			initialClique(lattice[0][i], data[0][i], orders[0], ysize[0],
					ysize[1]);

			lattice[1][i] = new Node[ysize[1]];
			for (int j = 0; j < ysize[1]; j++) {
				lattice[1][i][j] = new Node(ysize[1], ysize[0]);
			}
			initialClique(lattice[1][i], data[1][i], orders[1], ysize[1],
					ysize[0]);
		}

		return lattice;
	}

	/**
	 * 构造并初始化网格
	 * 
	 * @param inst
	 *            样本实例
	 * @return 双链网格
	 */
	private Node[][][] initialLattice(Instance inst) {
		int[][][] data = (int[][][]) inst.getData();
		length = inst.length();

		Node[][][] lattice = new Node[2][length][];
		for (int i = 0; i < length; i++) {
			lattice[0][i] = new Node[ysize[0]];
			for (int j = 0; j < ysize[0]; j++) {
				lattice[0][i][j] = new Node(ysize[0], ysize[1]);
			}
			initialClique(lattice[0][i], data[0][i], orders[0], ysize[0],
					ysize[1]);

			lattice[1][i] = new Node[ysize[1]];
			for (int j = 0; j < ysize[1]; j++) {
				lattice[1][i][j] = new Node(ysize[1], ysize[0]);
			}
			initialClique(lattice[1][i], data[1][i], orders[1], ysize[1],
					ysize[0]);
		}

		return lattice;
	}

	private void initialClique(Node[] node, int[] data, int[] order, int nsize,
			int msize) {
		int scalar = msize * nsize;
		for (int k = 0; k < node.length; k++) {
			if (node[k] == null)
				continue;

			for (int t = 0; t < order.length; t++) {
				if (data[t] == -1)
					continue;

				int base = data[t];
				if (order[t] == 0) {
					for (int j = 0; j < msize; j++) {
						node[k].score[j] += weights[base + k];
					}
				}
				if (order[t] == -1) {
					int offset = k;
					for (int j = 0; j < msize; j++) {
						node[k].score[j] += weights[base + offset];
						offset += nsize;
					}
				}
				if (order[t] == 1) {
					int offset = k;
					for (int i = 0; i < nsize; i++) {
						for (int j = 0; j < msize; j++) {
							node[k].trans[i][j] += weights[base + offset];
						}
						offset += nsize;
					}
				}
				if (order[t] == 2) {
					for (int i = 0; i < nsize; i++) {
						int offset = i * scalar + k;
						for (int j = 0; j < msize; j++) {
							node[k].trans[i][j] += weights[base + offset];
							offset += nsize;
						}
					}
				}
			}
		}
	}

	private void doForwardViterbi(Node[][][] lattice) {

		for (int j = 0; j < ysize[1]; j++) {
			for (int i = 0; i < ysize[0]; i++) {
				if (lattice[0][0][i] == null)
					continue;

				float score = lattice[1][0][j].score[i]
						+ lattice[0][0][i].score[0];
				lattice[1][0][j].addScore(score, i, -1);
			}
		}

		for (int p = 1; p < length; p++) {
			for (int k = 0; k < ysize[0]; k++) {
				if (lattice[0][p][k] == null)
					continue;

				for (int j = 0; j < ysize[1]; j++) {
					float  bestScore = Float.NEGATIVE_INFINITY;
					int bestPath = -1;
					for (int i = 0; i < ysize[0]; i++) {
						if (lattice[0][p - 1][i] == null)
							continue;

						float score = lattice[1][p - 1][j].score[i];
						score += lattice[0][p][k].trans[i][j];
						if (score > bestScore) {
							bestScore = score;
							bestPath = i;
						}
					}
					bestScore += lattice[0][p][k].score[j];
					lattice[0][p][k].addScore(bestScore, j, bestPath);
				}
			}

			for (int k = 0; k < ysize[1]; k++) {
				for (int j = 0; j < ysize[0]; j++) {
					if (lattice[0][p][j] == null)
						continue;

					float bestScore = Float.NEGATIVE_INFINITY;
					int bestPath = -1;
					for (int i = 0; i < ysize[1]; i++) {
						float score = lattice[0][p][j].score[i];
						score += lattice[1][p][k].trans[i][j];
						if (score > bestScore) {
							bestScore = score;
							bestPath = i;
						}
					}
					bestScore += lattice[1][p][k].score[j];
					lattice[1][p][k].addScore(bestScore, j, bestPath);
				}
			}
		}

	}

	private Predict<int[][]> getForwardPath(Node[][][] lattice) {
		Predict<int[][]> res = new Predict<int[][]>();

		float  maxScore = Float.NEGATIVE_INFINITY;
		int u = -1;
		int d = -1;
		for (int j = 0; j < ysize[1]; j++) {
			for (int i = 0; i < ysize[0]; i++) {
				if (lattice[1][length - 1][j].prev[i] != -1 || length == 1) {
					float score = lattice[1][length - 1][j].score[i];
					if (score > maxScore) {
						maxScore = score;
						u = i;
						d = j;
					}
				}
			}
		}

		int[][] path = new int[2][length];
		path[0][length - 1] = u;
		path[1][length - 1] = d;
		for (int i = length - 2; i >= 0; i--) {
			d = lattice[1][i + 1][d].prev[u];
			path[1][i] = d;
			u = lattice[0][i + 1][u].prev[d];
			path[0][i] = u;
		}

		res.add(path,maxScore);

		return res;
	}

	final class Node {

		float[][] trans = null;
		float[] score = null;
		int[] prev = null;

		public Node(int m, int n) {
			trans = new float[m][];
			for (int i = 0; i < m; i++) {
				trans[i] = new float[n];
			}
			score = new float[n];
			prev = new int[n];
			Arrays.fill(prev, -1);
		}

		public void addScore(float score, int j, int i) {
			this.score[j] = score;
			this.prev[j] = i;
		}
	}

}