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

import java.io.Serializable;

import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;

/**
 * 分类器抽象类
 * @author xpqiu
 * @version 1.0
 * Classifier
 * package edu.fudan.ml.classifier
 */
public abstract class AbstractClassifier implements Serializable{

	private static final long serialVersionUID = -175929257288466023L;
	
	protected AlphabetFactory factory;
	
	
	public AlphabetFactory getAlphabetFactory() {
		return factory;
	}

	/**
	 * 返回分类内部结果，标签为内部表示索引，需要还原处理
	 * @param instance
	 * @return
	 */
	public TPredict classify(Instance instance){
		return classify(instance,1);
	}
	/**
	 * 返回前n个分类内部结果，标签为内部表示索引，需要还原处理
	 * @param instance
	 * @param n 返回结果个数
	 * @return
	 */
	public abstract TPredict classify(Instance instance,int n);

	/**
	 * 对多个样本进行分类，返回前n个分类内部结果
	 * 标签为内部表示索引，需要还原处理
	 * @param instances
	 * @param n 返回结果个数
	 * @return
	 */
	public TPredict[] classify(InstanceSet instances, int n) {
		TPredict[] ress = new TPredict[instances.size()];
		for(int i=0;i<instances.size();i++){
			ress[i] = classify(instances.get(i), n);
		}
		return ress;
	}
	
	
	/**
	 * 对单个样本进行分类，返回得分最高的标签
	 * @param instance
	 * @param t 返回标签类型
	 * @return
	 */
	public TPredict classify(Instance instance,LabelParser.Type t){
		return classify(instance, t,1);
	}
	/**
	 * 对单个样本进行分类，返回得分最高前n的标签
	 * @param instance
	 * @param type 返回标签类型
	 * @param n 返回结果个数
	 * @return
	 */
	public abstract TPredict classify(Instance instance,LabelParser.Type type, int n);
	
	/**
	 * 对多个样本进行分类，返回得分最高前n的标签
	 * @param instances 样本集合
	 * @param type 返回标签类型
	 * @param n 返回结果个数
	 * @return
	 */
	public TPredict[] classify(InstanceSet instances,LabelParser.Type type, int n) {
		TPredict[] res= new Predict[instances.size()];
		for(int i=0;i<instances.size();i++){
			res[i]=  classify(instances.getInstance(i),type,n);			
		}
		return res;
	}

	/**
	 * 对单个样本进行分类，返回得分最高的标签
	 * 原始标签类型必须为字符串标签
	 * @param instance 待分类样本
	 * @return
	 */
	public String getStringLabel(Instance instance) {
		TPredict pred = classify(instance,Type.STRING);
		return (String) pred.getLabel(0);		
	}
	
	/**
	 * 对单个样本进行分类，返回得分最高的前n个标签，
	 * 原始标签类型必须为字符串标签
	 * @param instance 待分类样本
	 * @param n 返回结果个数
	 * @return
	 */
	public String[] getStringLabel(Instance instance,int n) {
		TPredict pred = classify(instance,Type.STRING);
		return (String[]) pred.getLabels();		
	}
	
	
	/**
	 * 对多个样本进行分类，返回得分最高的标签，缺省为字符串标签
	 * @param set
	 * @return
	 */
	public String[] getLabel(InstanceSet set) {
		String[] pred= new String[set.size()];
		for(int i=0;i<set.size();i++){
			pred[i]=  getStringLabel(set.getInstance(i));			
		}
		return pred;
	}
	

}