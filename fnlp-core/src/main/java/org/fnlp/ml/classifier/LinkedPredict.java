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

import java.util.LinkedList;

/**
 * KNN分类器结果记录
 * 
 * @author lcao
 *
 */

public class LinkedPredict<T> implements TPredict<T> {

	/**
	 * 类标签
	 */
	private LinkedList<T> labels;
	/**
	 * 分数
	 */
	private LinkedList<Float> scores;
	/**
	 * 证据
	 */
	private LinkedList<T> evidences;
	private int k;

	public LinkedPredict(int k){
		this.k = k;
		labels = new LinkedList<T>();
		scores = new LinkedList<Float>();
		evidences = new LinkedList<T>();
	}

	public LinkedPredict(){
		this.k = Integer.MAX_VALUE;
		labels = new LinkedList<T>();
		scores = new LinkedList<Float>();
		evidences = new LinkedList<T>();
	}


	/**
	 * 简单可视输出
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<labels.size();i++){
			sb.append(labels.get(i));
			sb.append(" ");
			sb.append(scores.get(i));
			sb.append("\n");			
		}
		return sb.toString();

	}


	/**
	 * 增加新的标签和得分，并根据得分调整排序	 * 
	 * @param label 标签
	 * @param score 得分
	 * @return 插入位置
	 */
	public int add(T label,float score) {
		int j = 0;

		for(; j < labels.size(); j++)
			if(scores.get(j) < score){
				labels.add(j, label);
				scores.add(j,score);
				break;
			}

		if(j == labels.size() && labels.size() < k){
			labels.add(j, label);
			scores.add(j,score);
		}

		if(labels.size() > k){
			labels.removeLast();
			scores.removeLast();
		}
		return j;
	}


	public void add(T t, float score, T source) {
		int j = add(t,score);
		if(j>=k)
			return;
		evidences.add(j,source);
		if(evidences.size()>k)
			evidences.removeLast();


	}

	@Override
	public T getLabel(int i) {
		if(labels.size()==0)
			return null;
		return labels.get(i);
	}

	@Override
	public float getScore(int i) {

		return scores.get(i);
	}

	@Override
	public void normalize() {
		float base = 1;
		if(scores.get(0)!=0.0f)
			base = scores.get(0)/2;
		float sum = 0;

		for(int i=0;i<scores.size();i++){
			float s  = (float) Math.exp(scores.get(i)/base);
			scores.set(i, s);
			sum +=s;
		}
		for(int i=0;i<scores.size();i++){
			float s = scores.get(i)/sum;
			scores.set(i, s);		
		}

	}

	@Override
	public int size() {
		return labels.size();
	}

	/**
	 * 合并重复标签，并重新排序
	 * @param useScore true 用得分; false 计数
	 * @return
	 */
	public LinkedPredict<T> mergeDuplicate(boolean useScore) {
		LinkedPredict<T> pred = new LinkedPredict<T>();
		for(int i = 0; i < labels.size(); i++){
			T l = labels.get(i);
			float score;
			if(useScore)
				score = scores.get(i);
			else 
				score=1;
			pred.addoradjust(l, score);
		}
		return pred;
	}

	/**
	 * 合并重复标签，有问题（未排序）
	 */
	public void mergeDuplicate() {
		for(int i = 0; i < labels.size(); i++)
			for(int j = i + 1; j < labels.size(); j++){
				T tagi = labels.get(i);
				T tagj = labels.get(j);
				if(tagi.equals(tagj)){
					scores.set(i, scores.get(i) + scores.get(j));
					labels.remove(j);
					scores.remove(j);
					j--;
				}
			}

	}

	/**
	 * 合并
	 * @param pred
	 * @param w
	 */
	public void addorjust(TPredict<T> pred, float w) {

		for(int i=0;i<pred.size();i++){
			T l = pred.getLabel(i);
			float s = pred.getScore(i);
			addoradjust(l,s*w);
		}

	}
	/**
	 * 
	 * @param label
	 * @param f
	 */
	public void addoradjust(T label, float f) {

		int j = 0;
		for(; j < labels.size(); j++){
			T tagj = labels.get(j);
			if(tagj.equals(label)){
				break;
			}
		}
		if(j<labels.size()){
			float ts = scores.get(j);
			labels.remove(j);
			scores.remove(j);
			add(label,ts+f);
		}else{
			add(label,f);
		}

	}

	@Override
	public T[] getLabels() {
		if(labels==null)
			return null;
		else
			return (T[]) labels.toArray();
	}

	@Override
	public void remove(int i) {
		labels.remove(i);
		scores.remove(i);
		if(evidences.size()>i)
			evidences.remove(i);

	}
	/**
	 * 确保结果个数小于等于n
	 * @param n
	 */
	public void assertSize(int n) {
		while(labels.size()>n){
			labels.removeLast();
			scores.removeLast();
			if(evidences.size()>n)
				evidences.removeLast();
		}

	}




}