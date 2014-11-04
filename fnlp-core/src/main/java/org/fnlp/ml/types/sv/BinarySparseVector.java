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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
/**
 * 0/1取值的稀疏向量
 * @author xpqiu
 */
public class BinarySparseVector implements ISparseVector {


	private static final long serialVersionUID = 3666569734894722449L;
	TIntArrayList data;

	public BinarySparseVector(){
		data = new TIntArrayList();
	}

	public BinarySparseVector(int len) {
		data = new TIntArrayList(len);
	}

	@Override
	public float dotProduct(float[] vector) {
		TIntIterator it = data.iterator();
		float sum = 0f;
		while(it.hasNext()){
			int i = it.next();
			if(i<0||i>=vector.length)
				continue;
			sum += vector[i];
		}
		return sum;
	}

	@Override
	public void put(int i) {
		data.add(i);

	}
	@Override
	public void put(int[] idx) {
		for(int i=0;i<idx.length;i++){
			if(idx[i]!=-1)
			data.add(idx[i]);
		}
		
	}
	

	@Override
	public float dotProduct(HashSparseVector sv) {
		float v =0f;
		TIntIterator it = data.iterator();			
		while(it.hasNext()){
			int i = it.next();
			v += sv.get(i);
		}
		return v;
	}

	private int size() {
		return data.size();
	}
	
	public float l2Norm2() {
		return	data.size();
	}
	

}