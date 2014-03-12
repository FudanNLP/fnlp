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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.fnlp.ml.types.sv.HashSparseVector;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

/**
 * 实现数组排序、直方图的功能
 * 
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyArrays {
	/**
	 * 记录之前的label和得分，保留前n个
	 * 
	 * @param score
	 * @param pred
	 * @return 插入位置
	 */
	public static int addBest(float[] scores, Object[] predList, float score, Object pred) {
		int n = scores.length;
		int i;
		for (i = 0; i < n; i++) {
			if (score > scores[i])
				break;
		}
		if (i >= n)
			return -1;
		for (int k = n - 2; k >= i; k--) {
			scores[k + 1] = scores[k];
			predList[k + 1] = predList[k];
		}
		scores[i] = score;
		predList[i] = pred;
		return i;
	}
	
	/**
	 * 
	 * @param freqmap
	 * @return
	 */
	public static TreeMap<Integer, Integer> countFrequency(TIntIntHashMap freqmap) {
		TreeMap<Integer, Integer> map = new TreeMap<Integer,Integer>();
		TIntIntIterator it = freqmap.iterator();
		while(it.hasNext()){
			it.advance();
			int freq = it.value();
			if(map.containsKey(freq)){
				map.put(freq, map.get(freq)+1);
			}else
				map.put(freq, 1);
		}
		return map;		
	}

	/**
	 * 
	 * @param count
	 * @param nbin
	 * @return 直方图
	 */
	public static float[][] histogram(float[] count, int nbin) {
		float maxCount = Float.NEGATIVE_INFINITY;
		float minCount = Float.MAX_VALUE;
		for (int i = 0; i < count.length; i++) {
			if (maxCount < count[i]) {
				maxCount = count[i];
			}
			if (minCount > count[i]) {
				minCount = count[i];
			}
		}
		float[][] hist = new float[2][nbin];
		float interv = (maxCount - minCount) / nbin;
		for (int i = 0; i < count.length; i++) {
			int idx = (int) Math.floor((count[i] - minCount) / interv);
			if (idx == nbin)
				idx--;
			hist[0][idx]++;
		}
		for (int i = 0; i < nbin; i++) {
			hist[1][i] = minCount + i * interv;
		}
		return hist;
	}

	/**
	 * 归一化
	 * 
	 * @param c
	 */
	public static void normalize(float[] c) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < c.length; i++) {
			if (min > c[i])
				min = c[i];
			if (max < c[i])
				max = c[i];
		}
		float val = max - min;
		if (val == 0)
			return;
		for (int i = 0; i < c.length; i++) {
			c[i] = (c[i] - min) / val;
		}
	}
	
	/**
	 * 概率归一化
	 * 
	 * @param c 数组元素必须大于等于0
	 */
	public static void normalize2Prop(float[] c) {
		float sum = sum(c);
		
		if (sum == 0)
			return;
		for (int i = 0; i < c.length; i++) {
			c[i] = c[i] / sum;
		}
	}

	/**
	 * 对数组的绝对值由大到小排序，返回调整后元素对于的原始下标
	 * 
	 * @param c
	 *            待排序数组
	 * @return 原始下标
	 */
	public static int[] sort(float[] c) {

		HashMap<Integer, Float> map = new HashMap<Integer, Float>();

		for (int i = 0; i < c.length; i++) {
			if (c[i] != 0.0) {
				map.put(i, Math.abs(c[i]));
			}
		}
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
	 * @param c
	 * @param thres
	 * @param r
	 *            true表示返回最大的，false表示返回剩余的
	 * @return 元素下标
	 */
	public static int[] getTop(float[] c, float thres, boolean r) {
		int[] idx = sort(c);
		int i;
		float total = 0;
		float[] cp = new float[idx.length];
		for (i = 0; i < idx.length; i++) {
			cp[i] = (float) Math.pow(c[idx[i]], 2);
			total += cp[i];
		}

		float ratio = 0;
		for (i = 0; i < idx.length; i++) {
			ratio += cp[i] / total;
			if (ratio > thres)
				break;
		}
		int[] a;
		if (r)
			a = Arrays.copyOfRange(idx, 0, i);
		else
			a = Arrays.copyOfRange(idx, i, idx.length);
		return a;
	}

	/**
	 * 对部分下标的元素赋值
	 * 
	 * @param c
	 *            数组
	 * @param idx
	 *            赋值下标
	 * @param v
	 *            值
	 */
	public static void set(float[] c, int[] idx, float v) {
		for (int i = 0; i < idx.length; i++) {
			c[idx[i]] = v;
		}
	}
	
	/**
	 * 移除能量值小于一定阈值的项
	 * @param c 数组
	 * @param v 阈值
	 */
	public static void trim(float[] c, float v) {
		int[] idx = getTop(c, v, false);
		set(c, idx, 0.0f);
	}

	/**
	 * 求和 
	 * @param c
	 * @return 所有元素的和
	 */
	public static int sum(int[] c) {
		int s = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] != 0)
				s+=c[i];
		}
		return s;
	}
	/**
	 * 累加
	 * @param c
	 * @return 所有元素的和
	 */
	public static int[] accumulate(int[] c) {
		int[] s = new int[c.length];
		s[0] =c[0];
		for (int i = 1; i < c.length; i++) {			
				s[i]+=s[i-1]+c[i];
		}
		return s;
	}
	/**
	 * 计算方差
	 * @param c
	 * @return
	 */
	public static float viarance(float[] c) {
		float aver = average(c);
		float via = 0.0f;
		for(int i=0;i<c.length;i++){
			float diff = c[i]-aver;
			via+=diff*diff;
		}
		return via/c.length;
	}
	/**
	 * 计算熵
	 * @param c 概率数组
	 * @return
	 */
	public static float entropy(float[] c) {
		
		float e = 0.0f;
		for(int i=0;i<c.length;i++){
			if(c[i]!=0.0&&c[i]!=1){
				e -= c[i]*Math.log(c[i]);
			}
			
		}
		return e;
	}
	
	/**
	 * 计算熵，先将频率转换为概率
	 * @param c 频率数组
	 * @return
	 */
	public static float entropy(int[] c) {
		//total 频率总数
		float total = sum(c);
		if(total==0f)
			return 0f;
		float[] prop = new float[c.length];
		for(int i=0;i<c.length;i++){
			prop[i] = c[i]/(total);	
		}
		return entropy(prop);
	}
	
	/**
	 * 求和 
	 * @param c float
	 * @return 所有元素的和
	 */
	public static float average(float[] c) {
		float s = sum(c);
		return s/c.length;
	}
	/**
	 * 求和 
	 * @param c float
	 * @return 所有元素的和
	 */
	public static float sum(float[] c) {
		float s = 0;
		for (int i = 0; i < c.length; i++) {
			s+=c[i];
		}
		return s;
	}
	
	/**
	 * 统计非零个数
	 * 
	 * @param c
	 * @return 非零元素数量
	 */
	public static int countNoneZero(float[] c) {
		int count = 0;
		for (int i = 0; i < c.length; i++) {
			if (c[i] != 0.0)
				count++;
		}
		return count;
	}

	/**
	 * 统计非零元素
	 * 
	 * @param c
	 * @return 非零元素标记
	 */
	public static boolean[] getNoneZeroIdx(float[] c) {
		boolean[] b = new boolean[c.length];
		for (int i = 0; i < c.length; i++) {
			if (c[i] != 0.0)
				b[i] = true;
		}
		return b;
	}

	public static int[] string2int(String[] c) {
		int[] d = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			d[i] = Integer.parseInt(c[i]);
		}
		return d;
	}
	public static String[] int2string(int[] c) {
		String[] d = new String[c.length];
		for (int i = 0; i < c.length; i++) {
			d[i] = String.valueOf(c[i]);
		}
		return d;
	}

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