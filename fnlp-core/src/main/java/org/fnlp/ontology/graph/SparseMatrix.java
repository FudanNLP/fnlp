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
 * 两维稀疏矩阵
 * 列向量优先
 * @author xpqiu
 * @version 1.0
 * SparseMatrix
 * package edu.fudan.ml.types
 */
public class SparseMatrix<T> {

	/**
	 * 每一维的大小
	 */
	private  int dim1;
	private int dim2;
	
	/**
	 * 内部为向量表示
	 */
	public TLongObjectHashMap<T> vector = null;
	
	public SparseMatrix(){
		vector = new TLongObjectHashMap<T>(100,0.8f);
	}
	
	public SparseMatrix(int dim){
		this();
		this.dim1 = dim;
		this.dim2 = dim;
	}
	
	public void set(int x, int y, T val){
		long idx = getIdx(x,y);
		if(idx==-1)
			return;
		set(idx,val);
	}

	public void set(long index, T value)	{

		vector.put(index, value);

	}
	
	public T get(int[] indices)	{
		long idx = getIdx(indices[0],indices[1]);
		return vector.get(idx);
	}
	
	public T get(int id1, int id2) {
		long idx = getIdx(id1,id2);
		return vector.get(idx);		
	}

	public T elementAt(long index)	{
		return vector.get(index);
	}
	
	public int[] size()	{
		return new int[]{dim1,dim2};
	}
	
	public int NZN()	{
		return vector.size();
	}
	
	public long[] getKeyIdx()	{
		return vector.keys();
	}
	
	/**
	 * 将多维索引转换为列排序索引 
	 * @return
	 * Jul 29, 2009
	 */
	public long getIdx(int x,int y){
		long idx=-1;
		if(x>=0&&x<dim1&&y>=0&&y<dim2)
			idx = dim1*x+y;
		return idx;
	}
	/**
	 * long型索引转换为int[]索引
	 * @param idx
	 * @return 索引
	 */
	public int[] getIndices(long idx)
	{
		int xIndices = (int)idx%dim1;
		int yIndices = (int)(idx-xIndices)/dim1;
		int []Indices = {xIndices,yIndices};
		return Indices;
	}
	
	public SparseMatrix<T> resize(int dim){
		SparseMatrix<T> mat = new SparseMatrix<T>(dim);
		TLongObjectIterator<T> it = vector.iterator();
		while(it.hasNext()){
			it.advance();
			long key = it.key();
			T val = it.value();
			int[] idx = getIndices(key);
			mat.set(idx[0],idx[1], val);
		}
		return mat;
	}

	
	

	
}