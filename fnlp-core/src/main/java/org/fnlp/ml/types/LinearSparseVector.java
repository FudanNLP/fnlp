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

package org.fnlp.ml.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 通过二分查找实现的稀疏向量，并实现各种向量运算
 * @version 1.0
 * @since 1.0
 * 
 */
public class LinearSparseVector implements Serializable {

	private static final long serialVersionUID = 1467092492463327579L;
	
	protected float[] data = new float[0];
	protected int[] index = new int[0];
	protected int length;
	private int increSize = 8;

	public LinearSparseVector()	{
	}
	
	public LinearSparseVector(int init)	{
		length = 0;
		data = new float[init];
		index = new int[init];
		Arrays.fill(index, Integer.MAX_VALUE);
	}

	/**
	 * 将一般数组转换成稀疏表示
	 * 
	 * @param w 数组
	 */
	public LinearSparseVector(float[] w) {
		for (int i = 0; i < w.length; i++) {
			if (Math.abs((w[i]-0f))<Float.MIN_VALUE) {
				put(i, w[i]);
			}
		}
	}
	/**
	 * 将一般数组转换成稀疏表示，并增加常数项
	 * @param w 数组
	 * @param b 是否设置常数项
	 */
	public LinearSparseVector(float[] w, boolean b) {
		for (int i = 0; i < w.length; i++) {
			if (Math.abs((w[i]-0f))<Float.MIN_VALUE) {
				put(i, w[i]);
			}
		}
		if(b)
			put(w.length,1.0f);
	}

	public LinearSparseVector(LinearSparseVector sv) {
		index = Arrays.copyOf(sv.index, sv.length);
		data = Arrays.copyOf(sv.data, sv.length);
		length = sv.length;
	}



	/**
	 * 向量减法: x-y
	 * 
	 * @param sv
	 */
	public void minus(LinearSparseVector sv) {
		for (int r = 0; r < sv.length; r++) {
			int p = Arrays.binarySearch(index, sv.index[r]);
			if (p >= 0) {
				data[p] = (float) data[p] - (float) sv.data[r];
			} else {
				put(sv.index[r], -(float) sv.data[r]);
			}
		}
	}

	/**
	 * 对应位置加上值: x[i] = x[i]+c
	 * 
	 * @param id
	 * @param c
	 */
	public void add(int id, float c) {
		int p = Arrays.binarySearch(index, id);
		if (p >= 0) {
			data[p] = ((float) data[p]) + c;
		} else {
			put(id, c);
		}
	}

	/**
	 * 向量加法：x+y
	 * 
	 * @param sv
	 */
	public void plus(LinearSparseVector sv) {
		plus(sv, 1);
	}

	/**
	 * 计算x+y*w
	 * 
	 * @param sv
	 * @param w
	 */
	public void plus(LinearSparseVector sv, float w) {
		if (sv == null)
			return;
		for (int i = 0; i < sv.length; i++) {
			float val = (float) sv.data[i] * w;
			add(sv.index[i], val);
		}
	}

	public float elementAt(int id) {
		float ret = 0;
		int p = Arrays.binarySearch(index, id);
		if (p >= 0)
			ret = (float) data[p];
		return ret;
	}

	public int[] indices() {
		return Arrays.copyOfRange(index, 0, length);
	}

	/**
	 * 向量点积: x*y
	 * 
	 * @param sv
	 * @return 结果
	 */
	public float dotProduct(LinearSparseVector sv) {
		return dotProduct(sv, 0);
	}

	/**
	 * 向量点积: x*(y+c)
	 * 
	 * @param sv
	 * @return 结果
	 */
	public float dotProduct(LinearSparseVector sv, float c) {
		float product = 0;

		for (int i = 0; i < sv.length; i++) {
			int p = Arrays.binarySearch(index, sv.index[i]);
			if (p >= 0) {
				float val = (float) sv.data[i] + c;
				val *= (float) data[p];
				product += val;
			}
		}

		return product;
	}

	
	/**
	 * A*(B+c)
	 * @param sv
	 * @param li
	 * @param n
	 * @return 结果
	 */
	public float dotProduct(LinearSparseVector sv, int li, int n) {
		float product = 0;
		int z = n * li;
		for (int i = 0; i < length; i++) {
			int p = Arrays.binarySearch(sv.index, index[i] + z);
			if (p >= 0) {
				product += (float) data[i] + (float) sv.data[p];
			}
		}
		return product;
	}

	public void scaleMultiply(float c) {
		if (c == 0)
			clear();
		for (int i = 0; i < length; i++) {
			data[i] = (float) data[i] * c;
		}
	}

	public void scaleDivide(float c) {
		if (c == 0)
			throw new ArithmeticException();
		for (int i = 0; i < length; i++) {
			data[i] = (float) data[i] / c;
		}
	}

	public float l1Norm() {
		float norm = 0;
		for (int i = 0; i < length; i++) {
			norm += Math.abs((float) data[i]);
		}
		return norm;
	}

	public float l2Norm2() {
		float norm = 0;
		for (int i = 0; i < length; i++) {
			float val =data[i];
			norm += val * val;
		}
		return norm;
	}

	public float l2Norm() {
		float norm = 0;
		for (int i = 0; i < length; i++) {
			float val = data[i];
			norm += val * val;
		}
		return (float) Math.sqrt(norm);
	}

	public float infinityNorm() {
		float norm = 0;
		for (int i = 0; i < length; i++) {
			float val = Math.abs((float) data[i]);
			if (val > norm)
				norm = val;
		}
		return norm;
	}

	public LinearSparseVector replicate(ArrayList<Integer> list, int dim) {
		LinearSparseVector sv = new LinearSparseVector();
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < list.size(); j++) {
				sv.put(index[i] + dim * list.get(j), (float) data[i]);
			}
		}
		return sv;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(index[i]);
			sb.append(':');
			sb.append(data[i]);
			sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * 计算两个向量距离
	 * 
	 * @param sv
	 * @return 距离值
	 */
	public float euclideanDistance(LinearSparseVector sv) {
		float dist = 0.0f;
		int r = 0;
		for (int i = 0; i < sv.length; i++) {
			if (sv.index[i] == index[r]) {
				float cur = (float) data[r] - (float) sv.data[i];
				dist += cur * cur;
				r++;
			} else {
				float cur = (float) sv.data[i];
				dist += cur * cur;
			}
		}
		for (; r < length; r++) {
			float cur = (float) data[r];
			dist += cur * cur;
		}
		return dist;
	}

	public void clear() {
		length = 0;
		Arrays.fill(index, Integer.MAX_VALUE);
	}

	public void normalize() {
		float norm = l2Norm();
		if (norm > 0)
			scaleMultiply(1 / norm);
	}

	public void normalize2() {
		float sum = 0;
		for (int i = 0; i < length; i++) {
			float value = (float) Math.exp(data[i]);
			data[i] = value;
			sum += value;
		}
		scaleDivide(sum);
	}

	public float dotProduct(float[] weights) {
		
		if (index[length - 1] >= weights.length)
			throw new IllegalArgumentException();

		float product = 0;
		for (int i = 0; i < length; i++) {
			product += (float) data[i] * weights[index[i]];
		}
		return product;
	}
	
	public float get(int idx) {
		int cur = Arrays.binarySearch(index, 0,length,idx);
		if (cur >= 0)
			return data[cur];		
		return -1f;
	}

	public void put(int idx, float value) {
		int cur = Arrays.binarySearch(index, 0,length,idx);
		
		if (cur < 0)	{
			if (length == data.length)
				grow();
			
			int p = -cur-1;
			System.arraycopy(data, p, data, p+1, length-p);
			System.arraycopy(index, p, index, p+1, length-p);
			data[p] = value;
			index[p] = idx;
			length++;
		}else	{
			data[cur] = value;
		}
	}
	/**
	 * 去掉第idx维特征
	 * @param idx
	 * @return
	 */
	public float remove(int idx)	{
		float ret = -1f;
		int p = Arrays.binarySearch(index,0,length, idx);
		if (p >= 0)	{
			System.arraycopy(data, p+1, data, p, length-p-1);
			System.arraycopy(index, p+1, index, p, length-p-1);
			length--;
		}else{
			System.err.println("error");
		}
		return ret;
	}

	protected void grow() {
		int nSize = data.length+increSize;
		float[] nData = new float[nSize];
		Arrays.fill(nData, Float.NaN);
		System.arraycopy(data, 0, nData, 0, length);
		
		int[] nIndex = new int[nSize];
		Arrays.fill(nIndex, Integer.MAX_VALUE);
		System.arraycopy(index, 0, nIndex, 0, length);
		
		data = null; index = null;
		data = nData;
		index = nIndex;
	}
	
	public int capacity()	{
		return data.length;
	}
	
	public void compact()	{
		float[] nData = new float[length];
		System.arraycopy(data, 0, nData, 0, length);
		
		int[] nIndex = new int[length];
		System.arraycopy(index, 0, nIndex, 0, length);
		
		data = null; index = null;
		data = nData;
		index = nIndex;
	}
	/**
	 * 稀疏元素个数
	 * @return
	 */
	public int size()	{
		return length;
	}
	
	public boolean containsKey(int idx)	{
		int cur = Arrays.binarySearch(index, 0,length,idx);
		if (cur < 0)
			return false;
		else
			return true;
	}
	
	public Iterator<Integer> iterator()	{
		return new IndexIterator();
	}
	
	protected class IndexIterator implements Iterator<Integer>	{
		int cur = 0;

		public boolean hasNext() {
			return (cur < length);
		}

		public Integer next() {
			return index[cur++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}