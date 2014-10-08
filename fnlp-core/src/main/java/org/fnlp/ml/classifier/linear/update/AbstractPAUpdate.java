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

package org.fnlp.ml.classifier.linear.update;

import org.fnlp.ml.loss.Loss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.HashSparseVector;

/**
 * 抽象参数更新类，采用PA算法
 * \mathbf{w_{t+1}} = \w_t + {\alpha^*(\Phi(x,y)- \Phi(x,\hat{y}))}.
 * \alpha =\frac{1- \mathbf{w_t}^T \left(\Phi(x,y) - \Phi(x,\hat{y})\right)}{||\Phi(x,y) - \Phi(x,\hat{y})||^2}.
 *
 */
public abstract class AbstractPAUpdate implements Update {

	/**
	 * \mathbf{w_t}^T \left(\Phi(x,y) - \Phi(x,\hat{y})\right)
	 */
	protected float diffw;
	/**
	 * \Phi(x,y)- \Phi(x,\hat{y})
	 */
	protected HashSparseVector diffv;
	protected Loss loss;
	/**
	 * 是否使用样本的权重进行加权
	 */
	public boolean useInstWeight;

	public AbstractPAUpdate(Loss loss) {
		diffw = 0;
		diffv = new HashSparseVector();
		this.loss = loss;
	}

	@Override
	public float update(Instance inst, float[] weights, int k, float[] extraweight, Object predict, float c) {
		return update(inst, weights, k, extraweight, inst.getTarget(), predict, c);
	}
	

	@Override
	public float update(Instance inst, float[] weights, int k, float[] extraweight, Object target,
			Object predict, float c) {

		int lost = diff(inst, weights, target, predict);
		if(lost==0)
			return 0f;
		float lamda = diffv.l2Norm2();

		if (diffw <= lost) {
			float alpha = (lost - diffw) / lamda;
			if(useInstWeight)
				alpha = alpha*inst.getWeight();
			if(alpha>c){
				alpha = c;
			}
			
			int[] idx = diffv.indices();
			 
			for (int i = 0; i < idx.length; i++) {			
				float t = diffv.get(idx[i]) * alpha;
				weights[idx[i]] += t;
				extraweight[idx[i]] += t *k;
			}
			for (int i = 0; i < idx.length; i++) {				
				
			}
		}
		
		diffv.clear();
		diffw = 0;
		
		return loss.calc(target, predict);
	}

	/**
	 * 计算预测类别和对照类别之间的距离
	 * @param inst 样本实例
	 * @param weights 权重
	 * @param target 对照类别
	 * @param predict 预测类别
	 * @return 预测类别和对照类别之间的距离
	 */
	protected abstract int diff(Instance inst, float[] weights, Object target,
			Object predict);

	protected void adjust(float[] weights, int ts, int ps) {
		assert (ts != -1 && ps != -1);
		diffv.put(ts, 1.0f);
		diffv.put(ps, -1.0f);
		diffw += weights[ts] - weights[ps];
	}
}