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

package org.fnlp.ml.classifier.linear;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.types.InstanceSet;

/**
 * 抽象参数训练类
 * @author Feng Ji
 *
 */
public abstract class AbstractTrainer {

	/**
	 * 抽象参数训练方法
	 * @param trainset 训练数据集
	 * @param devset 评估性能的数据集，可以为NULL
	 * @return 分类器 
	 */
	public abstract AbstractClassifier train(InstanceSet trainset, InstanceSet devset);
	
	/**
	 * 参数训练方法
	 * @param trainset 训练数据集
	 * @return 分类器 
	 */
	public  AbstractClassifier train(InstanceSet trainset){
		return train(trainset,null);
	}
	
	/**
	 * 评估性能方法
	 * @param devset 评估性能的数据集
	 */
	protected abstract void evaluate(InstanceSet devset);
	
}