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

import org.fnlp.ml.types.Instance;

public interface Update {
	
	/**
	 * 
	 * @param inst 样本实例
	 * @param weights 权重
	 * @param k 目前遍历的样本数
	 * @param extraweight 平均化感知器需要减去的权重
	 * @param predictLabel 预测类别
	 * @param c 步长阈值
	 * @return 预测类别和真实类别之间的损失
	 */
	public float update(Instance inst, float[] weights, int k, float[] extraweight, Object predictLabel,
			 float c);
	
	/**
	 * 
	 * @param inst 样本实例
	 * @param weights 权重
	 * @param k 目前遍历的样本数
	 * @param extraweight 平均化感知器需要减去的权重
	 * @param predictLabel 预测类别
	 * @param goldenLabel 真实类别
	 * @param c 步长阈值
	 * @return 预测类别和真实类别之间的损失
	 */
	public float update(Instance inst, float[] weights, int k, float[] extraweight, Object predictLabel,
			Object goldenLabel, float c);

}