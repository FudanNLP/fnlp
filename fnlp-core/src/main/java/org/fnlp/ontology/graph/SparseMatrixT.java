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

package org.fnlp.ontology.graph;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TLongObjectHashMap;


/**
 * 类似于matlab的多维稀疏矩阵
 * 列向量优先
 * @author xpqiu
 * @version 1.0
 * SparseMatrix
 * package edu.fudan.ml.types
 */
public class SparseMatrixT<T> {

	/**
	 * 每一维的大小
	 */
	private int[] dim;
	
	/**
	 * 内部为向量表示
	 */
	public TLongObjectHashMap<T> vector = null;
	
	public SparseMatrixT(){
		vector = new TLongObjectHashMap<T>(100,0.8f);
	}
	
	public SparseMatrixT(int[] dim){
		this();
		this.dim = dim;
	}
	
	public void set(int[] indices, T val){
		long idx = getIdx(indices);
		set(idx,val);
	}

	public void set(long index, T value)	{

		vector.put(index, value);

	}
	
	public T elementAt(int[] indices)	{
		long idx = getIdx(indices);
		return vector.get(idx);
	}

	public T elementAt(long index)	{
		return vector.get(index);
	}
	
	public int[] size()	{
		return dim;
	}
	
	/**
	 * 将多维索引转换为列排序索引 
	 * @param indices
	 * @return
	 * Jul 29, 2009
	 */
	public long getIdx(int[] indices){
		long idx=0;
		int i=indices.length-1;

		for(int j=0;i>0&&j<indices.length-1;i--,j++)
			idx += indices[i]*dim[j];
		idx += indices[0];
		return idx;
	}
	/**
	 * long型索引转换为int[]索引
	 * @param idx
	 * @return 索引
	 */
	public int[] getIndices(long idx)
	{
		int xIndices = (int)idx%this.size()[0];
		int yIndices = (int)(idx-xIndices)/this.size()[0];
		int []Indices = {xIndices,yIndices};
		return Indices;
	}
	
	public SparseMatrixT<T> clone(){
		SparseMatrixT<T> mat = new SparseMatrixT<T>();
		mat.dim = this.dim;
		mat.vector = new TLongObjectHashMap<T>(vector);
		return mat;
	}
	
	public SparseMatrixT<T> resize(int[] dim){
		SparseMatrixT<T> mat = new SparseMatrixT<T>(dim);
		TLongObjectIterator<T> it = vector.iterator();
		while(it.hasNext()){
			it.advance();
			long key = it.key();
			T val = it.value();
			int[] idx = getIndices(key);
			mat.set(idx, val);
		}
		return mat;
	}
	

	
}