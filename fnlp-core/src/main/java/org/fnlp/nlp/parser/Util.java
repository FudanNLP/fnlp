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

package org.fnlp.nlp.parser;


/**
 * 工具模块
 * 
 * 提供一些字符串操作的静态函数
 * @author cshen
 * @version  Feb 17, 2009
 */
public class Util {

	// Assumes input is a String[] containing integers as strings.
	public static int[] stringsToInts(String[] stringreps) {
		int[] nums = new int[stringreps.length];
		for (int i = 0; i < stringreps.length; i++)
			nums[i] = Integer.parseInt(stringreps[i]);
		return nums;
	}
	
	public static float[] stringsToDoubles(String[] stringreps) {
		float[] vals = new float[stringreps.length];
		for (int i = 0; i < vals.length; i++)
			vals[i] = Float.parseFloat(stringreps[i]);
		return vals;
	}
	
	public static String[] intsToStrings(int[] intreps) {
		String[] stringreps = new String[intreps.length];
		for (int i = 0; i < intreps.length; i++)
			stringreps[i] = new Integer(intreps[i]).toString();
		return stringreps;
	}

	public static String join(String[] a, char sep) {
		StringBuffer sb = new StringBuffer(a[0]);
		for (int i = 1; i < a.length; i++)
			sb.append(sep).append(a[i]);
		return sb.toString();
	}

	public static String join(int[] a, char sep) {
		StringBuffer sb = new StringBuffer(a[0]);
		for (int i = 1; i < a.length; i++)
			sb.append(sep).append(a[i]);
		return sb.toString();
	}

}