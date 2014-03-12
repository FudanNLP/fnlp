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

package org.fnlp.ml.classifier.struct.inf;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 抽象最优序列解码器
 * @author Feng Ji
 *
 */

public abstract class AbstractViterbi extends Inferencer {

	private static final long serialVersionUID = 2627448350847639460L;
	
	
	protected int[] orders;
	
	/**
	 * 标记个数
	 */
	int ysize;

	/**
	 * 模板个数
	 */
	int numTemplets;
	
	/**
	 * 模板组
	 */
	protected TempletGroup templets;

	/**
	 * 状态组合个数
	 */
	protected int numStates;
	
	/**
	 * 抽象最优解码算法实现
	 * @param inst 样本实例
	 */
	public abstract Predict getBest(Instance inst);
	/**
	 * Viterbi解码算法不支持K-Best解码
	 */
	@Override
	public  Predict getBest(Instance inst, int nbest)	{
		return getBest(inst);
	}
	public TempletGroup getTemplets() {
		return templets;
	}
	public void setTemplets(TempletGroup templets) {
		this.templets = templets;
	}
	
}