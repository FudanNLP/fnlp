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

import java.util.List;

/**
 * 自定义字符串操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyStrings {

	public static String normalizeRE(String string) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<string.length();i++){
			char c = string.charAt(i);
		}
		//([{\^-$|}])?*+
		return sb.toString();
	}

	/**
	 * 将数组元素用空格隔开，返回字符串
	 * @param s 二维数组
	 * @param delim1  列分隔符
	 * @param delim2  行分隔符
	 * @return
	 */
	public static String toString(String[][] s,String delim1, String delim2) {
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<s.length;i++){
			for(int j=0;j<s[i].length;j++){
				sb.append(s[i][j]);
				if(j<s[i].length-1)
					sb.append(delim1);
			}
			if(i<s.length-1)
				sb.append(delim2);
		}
		return sb.toString();
	}

	/**
	 * 将数组转换成字符串
	 * @param s 数组
	 * @param delim 分隔符
	 * @return
	 */
	public static String toString(List s,String delim) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<s.size();i++){
			sb.append(s.get(i));
			if(i<s.size()-1)
				sb.append(delim);
		}
		return sb.toString();

	}
	/**
	 * 将数组转换成字符串
	 * @param s 数组
	 * @param delim 分隔符
	 * @return
	 */
	public static String toString(Object[] s,String delim) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<s.length;i++){
			sb.append(s[i]);
			if(i<s.length-1)
				sb.append(delim);
		}
		return sb.toString();

	}

	public static String toString(int[] s, String delim) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<s.length;i++){
			sb.append(s[i]);
			if(i<s.length-1)
				sb.append(delim);
		}
		return sb.toString();
	}
	/**
	 * 
	 * @param s
	 * @param delim
	 * @return
	 */
	public static String toString(float[] s, String delim) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<s.length;i++){
			sb.append(s[i]);
			if(i<s.length-1)
				sb.append(delim);
		}
		return sb.toString();

	}

	/**
	 * 字符在字符串中出现的次数
	 * 
	 * @param str
	 * @param a
	 * @return
	 */
	public static int countOccurTimes(String str, String a) {
		int pos = -2;
		int n = 0;

		while (pos != -1) {
			if (pos == -2) {
				pos = -1;
			}
			pos = str.indexOf(a, pos + 1);
			if (pos != -1) {
				n++;
			}
		}
		return n;
	}

	public static String toString(float[][] s) {
		StringBuilder sb = new StringBuilder();

		for(int i=0;i<s.length;i++){
			for(int j=0;j<s[i].length;j++){
				sb.append(s[i][j]);
				if(j<s[i].length-1)
					sb.append(" ");
			}
			if(i<s.length-1)
				sb.append("\n");
		}
		return sb.toString();
	}
	/**
	 * 统计文本中某个字符串出现的次数
	 * @param str
	 * @param target
	 * @return
	 * 下午5:33:24
	 */
	public static int count(String str,String target){
		int count=0; 
		int index=0; 
		while(true){ 
			index=str.indexOf(target,index+1); 
			if(index>0){ 
				count++; 
			}else{ 
				break; 
			} 
		} 
		return count; 
	}



}