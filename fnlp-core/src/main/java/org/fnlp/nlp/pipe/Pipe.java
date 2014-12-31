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

package org.fnlp.nlp.pipe;

import java.io.Serializable;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;

/**
 * 数据类型转换管道，通过一系列的组合将数据从原始方式转为需要的数据类型
 * Pipe只能每次连续流水处理一个样本，不能按阶段多遍执行
 * 要分阶段多遍执行参见 {@link org.fnlp.ml.types.InstanceSet#loadThruStagePipes(org.fnlp.data.reader.Reader)}
 * @author xpqiu
 *
 */
public abstract class Pipe implements Serializable{
	
	/**
	 * 用来判断是否使用类别，以便在无类别使用时删掉
	 */
	boolean useTarget = false;
	
	/**
	 * 基本的数据类型转换处理操作，继承类需重新定义实现
	 * @param inst 样本
	 * @throws Exception
	 */
	public abstract void addThruPipe(Instance inst) throws Exception;
	
	/**
	 * 通过pipe直接处理实例
	 * @param instSet
	 * @throws Exception
	 */
	public void process(InstanceSet instSet) throws Exception {
		for(int i=0; i < instSet.size(); i++){
			Instance inst = instSet.getInstance(i);
			addThruPipe(inst);
		}
	}
}