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

package org.fnlp.util;

import java.util.Arrays;

import org.fnlp.ml.types.sv.HashSparseVector;

import gnu.trove.map.hash.TIntFloatHashMap;

/**
 * 自定义数组操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 * @deprecated 合并到MyArrays里
 */
public class MyHashSparseArrays {

	/**
	 * 对数组的绝对值由大到小排序，返回调整后元素对于的原始下标
	 * 
	 * @param data
	 *            待排序数组
	 * @return 原始下标
	 */
	public static int[] sort(TIntFloatHashMap data) {
		

		return MyCollection.sort(data);
	}

	/**
	 * 得到总能量值大于thres的元素对应的下标
	 * 
	 * @param data 稀疏向量
	 * @param thres
	 * @return 元素下标 int[][] 第一列表示大于阈值的元素 第二列表示小于阈值的元素
	 */
	public static int[][] getTop(TIntFloatHashMap data, float thres) {
		int[] idx = sort(data);
		int i;
		float total = 0;
		float[] cp = new float[idx.length];
		for (i = idx.length; i-- > 0;) {
			cp[i] = (float) Math.pow(data.get(idx[i]), 2);
			total += cp[i];
		}

		float ratio = 0;
		for (i = 0; i < idx.length; i++) {
			ratio += cp[i] / total;
			if (ratio > thres)
				break;
		}
		int[][] a = new int[2][];
		a[0] = Arrays.copyOfRange(idx, 0, i);
		a[1] = Arrays.copyOfRange(idx, i, idx.length);
		return a;
	}

	/**
	 * 对部分下标的元素赋零
	 * 
	 * @param data
	 *            数组
	 * @param idx
	 *            赋值下标
	 */
	public static void setZero(TIntFloatHashMap data, int[] idx) {
		for(int i = 0; i < idx.length; i++)	{
			if (data.containsKey(idx[i]))	{
				data.remove(idx[i]);
			}
		}
	}
	/**
	 * 移除能量值小于一定阈值的项
	 * @return 
	 * 
	 */
	public static int[] trim(TIntFloatHashMap data, float v) {
		int[][] idx = getTop(data, v);
		setZero(data, idx[1]);
		return idx[0];
	}
	
	/**
	 * 移除能量值小于一定阈值的项
	 * 
	 */
	public static void trim(HashSparseVector c, float v) {
		trim(c.data,v);		
	}


}