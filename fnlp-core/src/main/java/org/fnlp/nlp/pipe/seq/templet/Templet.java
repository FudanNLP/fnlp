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

package org.fnlp.nlp.pipe.seq.templet;

import java.io.Serializable;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
/**
 * 模板接口
 * @author xpqiu
 *
 */
public interface Templet extends Serializable{
	
	/**
	 * 返回该模板的阶
	 * @return 阶
	 */
	public int getOrder();
	
	/**
	 * 在给定实例的指定位置上抽取特征
	 * @param instance 给定实例
	 * @param pos 指定位置
	 * @param numLabels 标签数量
	 * @throws Exception 
	 */
	public int generateAt( Instance instance,
							IFeatureAlphabet features,
							int pos,
							int ... numLabels ) throws Exception;

	public int[] getVars();
	
}