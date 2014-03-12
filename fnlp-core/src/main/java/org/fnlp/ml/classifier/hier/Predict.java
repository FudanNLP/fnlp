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

package org.fnlp.ml.classifier.hier;

import org.fnlp.ml.classifier.TPredict;

import gnu.trove.list.linked.TFloatLinkedList;
import gnu.trove.list.linked.TIntLinkedList;

/**
 * 用来保存标签和相应的得分
 * 标签可能为中间计算结果 
 * 
 * @author xpqiu
 * 
 */
public class Predict  implements TPredict<Integer> {
	/**
	 * 记录前n个结果，默认为-1，不限制个数
	 */
	int n=-1;
	/**
	 * 标签得分值
	 */
	public TFloatLinkedList scores;
	/**
	 * 标签
	 */
	public TIntLinkedList labels;

	public Object other;

	public Predict() {
		this(1);
	}

	public Predict(int n) {
		this.n = n;
		scores = new TFloatLinkedList();
		labels = new TIntLinkedList();
	}






	/**
	 * 返回插入的位置
	 * @param score 得分
	 * @param label 标签
	 * @return 插入位置
	 */
	public int add(int label,float score) {
		int i = 0;
		int max;
		if(n==-1)
			max = scores.size();
		else
			max = n>scores.size()?scores.size():n;

			for (i = 0; i < max; i++) {
				if (score > scores.get(i))
					break;
			}
			//TODO: 没有删除多余的信息
			if(n!=-1&&i>=n)
				return -1;
			if(i<scores.size()){
				scores.insert(i,score);
				labels.insert(i,label);
			}else{
				scores.add(score);
				labels.add(label);
			}
			return i;
	}

	/**
	 * 获得预测结果
	 * 
	 * @param i
	 *            位置
	 * @return 第i个预测结果；如果不存在，为-1
	 */
	public Integer getLabel(int i) {
		if (i < 0 || i >= labels.size())
			return -1;
		return labels.get(i);
	}

	/**
	 * 获得预测结果的得分
	 * 
	 * @param i   位置
	 * @return 第i个预测结果的得分；不存在为Double.NEGATIVE_INFINITY
	 */
	public float getScore(int i) {
		if (i < 0 || i >=scores.size())
			return Float.NEGATIVE_INFINITY;
		return scores.get(i);
	}


	/**
	 * 预测结果数量
	 * 
	 * @return 预测结果的数量
	 */
	public int size() {
		return n;
	}

	public void normalize(){

		float base = scores.get(0)/2;
		float sum = 0;

		for(int i=0;i<scores.size();i++){
			float s  = (float) Math.exp(scores.get(i)/base);
			scores.set(i, s);
			sum +=s;
		}
		for(int i=0;i<scores.size();i++){
			float s = scores.get(i)/sum;
			//			if(s <0.001f)
			//				s=0;
			scores.set(i, s);

		}
	}

	@Override
	public Integer[] getLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(int i) {
		// TODO Auto-generated method stub
		
	}



}