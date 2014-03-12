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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.fnlp.ml.types.LinearSparseVector;
/**
 * 自定义数组操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyLinearSparseArrays {

	/**
	 * 对数组的绝对值由大到小排序，返回调整后元素对于的原始下标
	 * 
	 * @param sv
	 *            待排序数组
	 * @return 原始下标
	 */
	public static int[] sort(LinearSparseVector sv) {
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();

		Iterator<Integer> it = sv.iterator();
		while (it.hasNext()) {
			int id = it.next();
			float val = sv.get(id);
			map.put(id, Math.abs(val));
		}
		it = null;

		ArrayList<Map.Entry<Integer, Float>> list = new ArrayList<Map.Entry<Integer, Float>>(
				map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
			@Override
			public int compare(Entry<Integer, Float> o1,
					Entry<Integer, Float> o2) {

				if (o2.getValue() > o1.getValue()) {
					return 1;
				} else if (o1.getValue() > o2.getValue()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		int[] idx = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			idx[i] = list.get(i).getKey();
		}
		return idx;

	}

	/**
	 * 得到总能量值大于thres的元素对应的下标
	 * 
	 * @param sv 稀疏向量
	 * @param thres
	 * @return 元素下标 int[][] 第一列表示大于阈值的元素 第二列表示小于阈值的元素
	 */
	public static int[][] getTop(LinearSparseVector sv, float thres) {
		int[] idx = sort(sv);
		int i;
		float total = 0;
		float[] cp = new float[idx.length];
		for (i = idx.length; i-- > 0;) {
			cp[i] = (float) Math.pow(sv.get(idx[i]), 2);
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
	 * @param sv
	 *            数组
	 * @param idx
	 *            赋值下标
	 */
	public static void setZero(LinearSparseVector sv, int[] idx) {
		for(int i = 0; i < idx.length; i++)	{
			if (sv.containsKey(idx[i]))	{
				sv.remove(idx[i]);
			}
		}
	}
	/**
	 * 移除能量值小于一定阈值的项
	 * @return 
	 * 
	 */
	public static int[] trim(LinearSparseVector c, float v) {
		int[][] idx = getTop(c, v);
		setZero(c, idx[1]);
		return idx[0];

	}

}