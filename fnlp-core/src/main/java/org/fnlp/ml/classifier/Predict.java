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

import java.util.Arrays;

/**
 * 用来输出带得分的预测结果
 * @author xpqiu
 *
 */
public class Predict<T> implements TPredict<T> {
	/**
	 * 标签数组
	 */
	public T[] labels;
	/**
	 * 得分数组
	 */
	public float[] scores;
	/**
	 * 保存个数
	 */
	protected int n;
	/**
	 * 缺省只保存一个最大值
	 */
	public Predict(){
		this(1);
	}
	/**
	 * 保存前n个最大值
	 * @param n
	 */
	public Predict(int n){
		this.n = n;
		labels = (T[]) new Object[n];
		scores = new float[n];
		Arrays.fill(scores, Float.NEGATIVE_INFINITY);
	}

	public float getShiftScore(){
		for(int i=0; i<n; i++){
			String s = labels[i].toString();
			if(s.equals("S")){
				return scores[i];
			}
		}
		System.err.println("not right");
		return 0;
	}
	

	public int size() {		
		return labels.length;
	}
	

	
	/**
	 * 返回得分最高的标签
	 * @return
	 */
	public T getLabel() {
		return labels[0];
	}
	/**
	 * 返回得分第i高的标签
	 */
	public T getLabel(int i) {
		return labels[i];
	}
	
	public float getScore(int i) {
		return scores[i];
	}
	
	
	/**
	 * 设置位置i上的标签和得分
	 * @param i
	 * @param label2
	 * @param d
	 */
	public void set(int i, T label2, float d) {
		labels[i] = label2;
		scores[i] = d;		
	}
	
	/**
	 * 增加新的标签和得分，并根据得分调整排序	 * 
	 * @param label 标签
	 * @param score 得分
	 * @return 插入位置
	 */
	public int add(T label,float score) {
		int i = 0;
		int ret = i;
		if (n != 0) {
			for (i = 0; i < n; i++) {
				if (score > scores[i])
					break;
			}
			if (i != n || n < scores.length) {
				for (int k = n - 2; k >= i; k--) {
					scores[k + 1] = scores[k];
					labels[k + 1] = labels[k];
				}
				ret = i;
			}else	 if (n < scores.length)	{

			}else	{
				ret = -1;
			}
		}
		if(ret!=-1){
			scores[i] = score;
			labels[i] = label;
		}
		if (n < scores.length)
			n++;
		return ret;
	}
	
	/**
	 * 将得分归一化到[0,1]区间
	 */
	public void normalize() {
		float base = 1;
//		if(scores[0]>=0.0f)
//			base = scores[0]/2;
		float sum = 0;

		for(int i=0;i<scores.length;i++){
			float s  = (float) Math.exp(scores[i]/base);
			scores[i] = s;
			sum +=s;
		}
		for(int i=0;i<scores.length;i++){
			float s = scores[i]/sum;
			scores[i] = s;
			
		}
		
	}
	/**
	 * 简单可视输出
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<labels.length;i++){
			sb.append(labels[i]);
			sb.append(" ");
			sb.append(scores[i]);
			sb.append("\n");			
		}
		return sb.toString();
		
	}
	/**
	 * 取得所有返回结果
	 * @return
	 */
	public T[] getLabels() {
		return labels;
	}
	@Override
	public void remove(int i) {
		// TODO Auto-generated method stub
		System.err.println("没有实现");
	}
	
}