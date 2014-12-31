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

package org.fnlp.ml.classifier.linear.inf;

import java.io.Serializable;

import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.types.Instance;

/**
 * 推理类
 * @author xpqiu
 *
 */
public abstract class Inferencer implements Serializable	{

	private static final long serialVersionUID = -7254946709189008567L;
	
	protected float[] weights;
	
	protected boolean isUseTarget;
		
	/**
	 * 得到前n个最可能的预测值
	 * @param inst 
	 * @return
	 * Sep 9, 2009
	 */
	public abstract TPredict getBest(Instance inst);
	
	public abstract TPredict getBest(Instance inst, int n);
	
	public float[] getWeights()	{
		return weights;
	}
	
	public void setWeights(float[] weights)	{
		this.weights = weights;
	}

	public void isUseTarget(boolean b) {
		isUseTarget = b;
	}
}