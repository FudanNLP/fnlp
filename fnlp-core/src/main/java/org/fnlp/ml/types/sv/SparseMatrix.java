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

import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.hash.TLongFloatHashMap;

import java.util.Random;


/**
 * 类似于matlab的多维稀疏矩阵
 * 列向量优先
 * @author xpqiu
 * @version 1.0
 * SparseMatrix
 * package edu.fudan.ml.types
 */
public class SparseMatrix {

	/**
	 * 每一维的大小
	 */
	private int[] dim;
	
	/**
	 * 内部为向量表示
	 */
	public TLongFloatHashMap vector = null;
	
	public SparseMatrix(){
		vector = new TLongFloatHashMap(100,0.8f);
	}
	
	public SparseMatrix(int[] dim){
		this();
		this.dim = dim;
	}
	
	public void set(int[] indices, float val){
		long idx = getIdx(indices);
		set(idx,val);
	}

	public void set(long index, float value)	{

		vector.put(index, value);

	}
	
	public float elementAt(int[] indices)	{
		long idx = getIdx(indices);
		return vector.get(idx);
	}

	public float elementAt(long index)	{
		return vector.get(index);
	}
	
	public int[] size()	{
		return dim;
	}
	public SparseMatrix mutiplyMatrix(SparseMatrix a){
		int m = this.size()[0];
		int n = a.size()[1];
		int dim[]={m,n};
		SparseMatrix matrix = new SparseMatrix(dim);
		 TLongFloatIterator it = this.vector.iterator();
		 TLongFloatIterator ita = a.vector.iterator();
		for (int i = this.vector.size(); i-- > 0;) 
		{
			it.advance();
			ita = a.vector.iterator();
			for(int j = a.vector.size(); j-- > 0;)
			{
				ita.advance();
				if(this.getIndices(it.key())[1]==a.getIndices(ita.key())[0])
				{
					int []indices = {this.getIndices(it.key())[0],a.getIndices(ita.key())[1]};
					matrix.set(indices, matrix.elementAt(indices)+it.value()*ita.value());
				}					
			}
		}
		return matrix;
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
	
	public SparseMatrix clone(){
		SparseMatrix mat = new SparseMatrix();
		mat.dim = this.dim;
		mat.vector = new TLongFloatHashMap(vector);
		return mat;
	}
	
	/**
	 * 生成大小为int[]dim的随机矩阵
	 * @param dim
	 * @return 矩阵
	 */
	public static SparseMatrix random(int[] dim) {
		Random r = new Random();
		SparseMatrix matrix = new SparseMatrix(dim);
		for(int i=0;i<dim[0];i++)
			for(int j=0;j<dim[1];j++)
			{
				int []indices={i,j};
				matrix.set(indices, r.nextFloat());
			}
		return matrix;
	}

	/**
	 * @param mat
	 * Sep 6, 2009
	 */
	public void minus(SparseMatrix mat) {
		TLongFloatIterator it = mat.vector.iterator();
		for (int i = mat.vector.size(); i-- > 0;) 
		{
			it.advance();
			vector.put(it.key(),vector.get(it.key()) - it.value());
		}
		
	}
	
	public void add(SparseMatrix mat) {
		TLongFloatIterator it = mat.vector.iterator();
		for (int i = mat.vector.size(); i-- > 0;) 
		{
			it.advance();
			vector.put(it.key(),vector.get(it.key()) + it.value());
		}
		
	}
	
	public float l1Norm()	{
		float norm = 0;
		TLongFloatIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			norm += Math.abs(it.value());
		}
		return norm;
	}
	
	public float l2Norm()	{
		float norm = 0;
		TLongFloatIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			norm += it.value()*it.value();
		}
		return (float) Math.sqrt(norm);
	}
	
	public float infinityNorm()	{
		float norm = 0;
		TLongFloatIterator it = vector.iterator();
		for (int i = vector.size(); i-- > 0;) {
			it.advance();
			if (Math.abs(it.value()) > norm)
				norm = Math.abs(it.value());
		}
		it=null;
		return norm;
	}

	/**
	 * @return
	 * Sep 6, 2009
	 */
	public SparseMatrix trans() {
		int []newdim = {dim[1],dim[0]};
		SparseMatrix newmat = new SparseMatrix(newdim);
		TLongFloatIterator itW = vector.iterator();
		for (int i = vector.size(); i-- > 0;) 
		{
			itW.advance();
			int x = getIndices(itW.key())[0];
			int y = getIndices(itW.key())[1];
			int []TranWIndices = {y,x};
			newmat.set(TranWIndices,itW.value());
		}
		return newmat;
	}
	
}