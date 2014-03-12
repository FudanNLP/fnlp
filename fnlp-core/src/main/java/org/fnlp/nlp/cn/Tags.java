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

package org.fnlp.nlp.cn;

import java.util.regex.Pattern;


/**
 * 中文词性操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Tags {
	

	
	
	
	static Pattern nounsPattern  = Pattern.compile("名词|人名|地名|机构名|专有名");
	
	public static boolean isNoun(String pos) {
		return (nounsPattern.matcher(pos).find());
	}

	
	static Pattern stopwordPattern  = Pattern.compile(".*代词|标点|介词|从属连词|语气词|叹词|结构助词|拟声词|方位词");
	/**
	 * 判断词性是否为无意义词。
	 * @param pos 词性
	 * @return true,false
	 */
	public static boolean isStopword(String pos) {
		return (stopwordPattern.matcher(pos).find());
	}
}