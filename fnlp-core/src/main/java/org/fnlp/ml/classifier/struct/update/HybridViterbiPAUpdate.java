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

package org.fnlp.ml.classifier.struct.update;

import org.fnlp.ml.classifier.linear.update.AbstractPAUpdate;
import org.fnlp.ml.classifier.struct.inf.HybridViterbi;
import org.fnlp.ml.loss.struct.HybridHammingLoss;
import org.fnlp.ml.types.Instance;

/**
 * 双链序列的参数更新类，采用PA算法
 * @author Feng Ji
 *
 */
public class HybridViterbiPAUpdate extends AbstractPAUpdate {

	private int[] ysize;
	private int[][] orders;
	private int[][][] data;
	private int[][] golds;
	private int[][] preds;

	public HybridViterbiPAUpdate(HybridViterbi inf, HybridHammingLoss loss,
			float c) {
		super(loss);
		this.ysize = inf.ysize();
		this.orders = inf.orders();
	}

	/**
	 * @return 预测双链和对照双链之间不同的Clique数量
	 */
	@Override
	protected int diff(Instance inst, float[] weights, Object targets,
			Object predicts) {

		data = (int[][][]) inst.getData();
		if (targets == null)
			golds = (int[][]) inst.getTarget();
		else
			golds = (int[][]) targets;
		preds = (int[][]) predicts;

		int diff = 0;

		if (golds[0][0] != preds[0][0]) {
			diff++;
			diffUpClique(weights, orders[0], 0);
		}
		if (golds[1][0] != preds[1][0]) {
			diff++;
			diffDownClique(weights, orders[1], 0);
		}

		for (int i = 1; i < data[0].length; i++) {
			if (golds[0][i - 1] != preds[0][i - 1]
					|| golds[1][i - 1] != preds[1][i - 1]
					|| golds[0][i] != preds[0][i]) {
				diff++;
				diffUpClique(weights, orders[0], i);
			}
			if (golds[1][i - 1] != preds[1][i - 1]
					|| golds[0][i] != preds[0][i] || golds[1][i] != preds[1][i]) {
				diff++;
				diffDownClique(weights, orders[1], i);
			}
		}

		return diff;
	}

	/**
	 * 根据下层Clique，调整权重
	 * @param weights 权重
	 * @param orders 下层Clique对应模板的阶数组
	 * @param p 位置
	 */
	private void diffDownClique(float[] weights, int[] orders, int p) {
		for (int t = 0; t < orders.length; t++) {
			if (data[1][p][t] == -1)
				continue;

			int base = data[1][p][t];
			if (orders[t] == 0) {
				if (golds[1][p] != preds[1][p]) {
					int ts = base + golds[1][p];
					int ps = base + preds[1][p];
					adjust(weights, ts, ps);
				}
			}
			if (orders[t] == -1) {
				if (golds[0][p] != preds[0][p] || golds[1][p] != preds[1][p]) {
					int ts = base + golds[0][p] * ysize[1] + golds[1][p];
					int ps = base + preds[0][p] * ysize[1] + preds[1][p];
					adjust(weights, ts, ps);
				}
			}
			if (p > 0) {
				if (orders[t] == 1) {
					if (golds[1][p - 1] != preds[1][p - 1]
							|| golds[1][p] != preds[1][p]) {
						int ts = base + golds[1][p - 1] * ysize[1] + golds[1][p];
						int ps = base + preds[1][p - 1] * ysize[1] + preds[1][p];
						adjust(weights, ts, ps);
					}
				}
				if (orders[t] == 2) {
					int ts = base + (golds[1][p - 1] * ysize[0] + golds[0][p])
							* ysize[1] + golds[1][p];
					int ps = base + (preds[1][p - 1] * ysize[0] + preds[0][p])
							* ysize[1] + preds[1][p];
					adjust(weights, ts, ps);
				}
			}
		}
	}

	/**
	 * 根据上层Clique，调整权重
	 * @param weights 权重
	 * @param orders 上层Clique对应模板的阶数组
	 * @param p 位置
	 */
	private void diffUpClique(float[] weights, int[] orders, int p) {
		for (int t = 0; t < orders.length; t++) {
			if (data[0][p][t] == -1)
				continue;

			int base = data[0][p][t];
			if (orders[t] == 0) {
				if (golds[0][p] != preds[0][p]) {
					int ts = base + golds[0][p];
					int ps = base + preds[0][p];
					adjust(weights, ts, ps);
				}
			}
			if (p > 0) {
				if (orders[t] == -1) {
					if (golds[1][p - 1] != preds[1][p - 1]
							|| golds[0][p] != preds[0][p]) {
						int ts = base + golds[1][p - 1] * ysize[0] + golds[0][p];
						int ps = base + preds[1][p - 1] * ysize[0] + preds[0][p];
						adjust(weights, ts, ps);
					}
				}
				if (orders[t] == 1) {
					if (golds[0][p - 1] != preds[0][p - 1]
							|| golds[0][p] != preds[0][p]) {
						int ts = base + golds[0][p - 1] * ysize[0] + golds[0][p];
						int ps = base + preds[0][p - 1] * ysize[0] + preds[0][p];
						adjust(weights, ts, ps);
					}
				}
				if (orders[t] == 2) {
					int ts = base + (golds[0][p - 1] * ysize[1] + golds[1][p - 1])
							* ysize[0] + golds[0][p];
					int ps = base + (preds[0][p - 1] * ysize[1] + preds[1][p - 1])
							* ysize[0] + preds[0][p];
					adjust(weights, ts, ps);
				}
			}
		}
	}

}