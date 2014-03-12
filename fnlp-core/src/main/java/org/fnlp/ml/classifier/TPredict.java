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

package org.fnlp.ml.classifier;

/**
 * 结果接口
 * @author xpqiu
 * @version 2.0
 * @since 1.5
 */
public interface TPredict<T> {
	/**
	 * 获得预测结果
	 * @param i	  位置
	 * @return 第i个预测结果；如果不存在，为NULL
	 */
	public T getLabel(int i);
	/**
	 * 获得预测结果的得分
	 * @param i	     位置
	 * @return 第i个预测结果的得分；不存在为Double.NEGATIVE_INFINITY
	 */
	public float getScore(int i);
	/**
	 * 归一化得分
	 */
	public void normalize();
	/**
	 * 预测结果数量 
	 * @return 预测结果的数量
	 */
	public int size();
	/**
	 * 得到所有标签
	 * @return
	 */
	public T[] getLabels();
	/**
	 * 删除位置i的信息
	 * @param i
	 */
	public void remove(int i);
	
	

}