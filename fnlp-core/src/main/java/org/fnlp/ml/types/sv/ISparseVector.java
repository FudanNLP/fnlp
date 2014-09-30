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

package org.fnlp.ml.types.sv;

import java.io.Serializable;


/**
 * 稀疏向量，并实现各种向量运算
 *
 */
public interface ISparseVector extends Serializable {

	/**
	 * 点积
	 * @param vector
	 * @return
	 */
	public float dotProduct(float[] vector);
	
	/**
	 * 
	 * @param sv
	 * @return
	 */
	public float dotProduct(HashSparseVector sv);
	
	/**
	 * 增加元素
	 * @param vector
	 * @return
	 */
	public void put(int i);
	/**
	 * 增加多个元素
	 * @param vector
	 * @return
	 */
	public void put(int[] idx);
	/**
	 * L2模
	 * @return
	 */
	public float l2Norm2();

}