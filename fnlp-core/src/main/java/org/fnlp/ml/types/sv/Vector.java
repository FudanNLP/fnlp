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


/**
 * 一般向量，只是封装为统一接口
 * @author Xipeng
 *
 */
public class Vector implements ISparseVector {
	
	private static final long serialVersionUID = -7805496876863128028L;
	
	float[] data;
	
	public Vector(int size){
		data = new float[size];
	}
	
	public Vector(float[] data){
		this.data = data;
	}

	@Override
	public float dotProduct(float[] vector) {
		System.out.println("未实现");
		return 0;
	}

	@Override
	public float dotProduct(HashSparseVector sv) {
		return sv.dotProduct(data);
	}

	@Override
	public void put(int i) {
		System.out.println("未实现");

	}

	@Override
	public void put(int[] idx) {
		System.out.println("未实现");

	}

	@Override
	public float l2Norm2() {
		// TODO Auto-generated method stub
		return 0;
	}

}