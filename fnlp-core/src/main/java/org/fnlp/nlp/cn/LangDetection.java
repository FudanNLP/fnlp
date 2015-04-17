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

import java.lang.Character.UnicodeBlock;

/**
 * 判断语言是否为中英文
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2015年4月17日 上午10:44:19
 */
public class LangDetection {

	public static String detect(String str){
		char[] ch = str.toCharArray();
		if(isChinese(ch))
			return "cn";
		else
			return "en";
	}
	public static boolean isChinese(char[] ch){
		for(int i=0;i<ch.length;i++){
			if(isChinese(ch[i]))
				return true;
		}
		return false;
	}
	
	private static boolean isChinese(char c) {
		UnicodeBlock ub = UnicodeBlock.of(c);
		if(ub==UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
			ub == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS||
			ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A||
			ub == UnicodeBlock.GENERAL_PUNCTUATION||
			ub == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION||
			ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
			return true;
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str;
		str = ".";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		
		str = "you and me";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		
		str = "()";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		
		str = "。";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		str = "我们";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		str = "我们and";
		System.out.println(LangDetection.detect(str)+":\t"+str);
		str = "《and";
		System.out.println(LangDetection.detect(str)+":\t"+str);

	}

}