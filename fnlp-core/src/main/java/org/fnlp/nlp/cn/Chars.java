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

import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * 中文字符操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Chars {
	
	/**
	 * 字符类型
	 * 汉字 字母 数字 标点 :空格;
	 * @author xpqiu
	 *
	 */
	public  enum CharType {  
		/**
		 * 汉字  
		 */
		C, 
		/**
		 * 字母
		 */
		L,
		/**
		 * 数字
		 */
		D, 
		/**
		 * 标点
		 */
		P, 
		/**
		 * 空格
		 */
		B
		}  
	
	/**
	 * 字符串类型
	 */
	public enum StringType{
		/**
		 * 纯数字
		 */
		D,
		/**
		 * 纯字母
		 */
		L, 
		/**
		 * 纯汉字
		 */
		C, 
		/**
		 * 混合字符串
		 */
		M,
		/**
		 * 空格
		 */
		B,
		/**
		 * 其他，例如标点等
		 */
		O
	}
	
	/**
	 * 半角或全角数字英文
	 * 
	 * @param str
	 * @return 0,1
	 * @see Chars#isChar(char)
	 */
	public static boolean isChar(String str) {		
		for (int i = 0; i < str.length(); i++) {
			char c  = str.charAt(i);
			if (isChar(c))
				return false;
		}
		return true;
	}
	
	
	/**
	 * 是否包含半角或全角数字英文
	 * 
	 * @param str
	 * @return 0,1
	 * @see Chars#isChar(char)
	 */
	public static boolean containChar(String str) {		
		for (int i = 0; i < str.length(); i++) {
			char c  = str.charAt(i);
			if (isChar(c))
				return true;
		}
		return false;
	}

	/**
	 * 半角或全角数字英文
	 * @param c
	 * @return
	 */
	public static boolean isChar(char c) {
		return Character.isDigit(c)||Character.isLowerCase(c)||Character.isUpperCase(c);
	}

	/**
	 * 是否为中文字符
	 * @param c
	 * @return
	 */
	public static boolean isChineseChar(char c) {
		if (c > 65280 && c < 65375)
			return true;
		else
			return false;
	}

	/**
	 * 全角转半角
	 * 
	 * @param input
	 *            全角或半角字符串
	 * @return 半角字符串
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	/**
	 * 将字符类型转换为字符串类型
	 * 须和Chars#getStringType(String)保持一致
	 * @param c
	 * @return
	 * @see Chars#getStringType(String)
	 */
	public static StringType char2StringType(CharType c) {
		switch(c){
		case D: return StringType.D;
		case C: return StringType.C;
		case L: return StringType.L;
		case B: return StringType.B;
		default: return StringType.O;
		}
	}
	/**
	 * 得到字符串类型
	 * @param str
	 * @return
	 * @see Chars#char2StringType(CharType)
	 */
	public static StringType getStringType(String str) {
		TreeSet<CharType> set = getTypeSet(str);
		if(set.size()==1){
			CharType c = set.first();
			return char2StringType(c);
		}
		return StringType.M;		
	}
	/**
	 * 得到字符串中所有出现的字符类型集合
	 * @param str
	 * @return
	 */
	public static TreeSet<CharType> getTypeSet(String str) {
		CharType[] tag = getType(str);
		TreeSet<CharType> set = new TreeSet<CharType>();
		for(int i=0;i<tag.length;i++){
			set.add(tag[i]);			
		}	
		return set;
	}
	
	private static boolean isCharType(CharType[] ct, CharType CT) {
		for(int i = 0; i < ct.length; i++) {
			if(ct[i] != CT)
				return false;
		}
		return true;
	}

	public static boolean isLetterOrDigitOrPunc(char ch) {
		int i = Character.getType(ch);
		return Character.isLowerCase(ch) || Character.isUpperCase(ch)
		 || Character.isDigit(ch)
				|| (i>=20&&i<=30); 
		//TODO:可以修改为Java7中的isLetterOrDigit
		
		
	}
	
	static Pattern PattLDP = Pattern.compile("(\\w|\\pP|\\pS|\\s)+");
	
	public static boolean isLetterOrDigitOrPunc(String str) {
		return PattLDP.matcher(str).matches();  
		//TODO:可以修改为Java7中的isLetterOrDigit
	}
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isPunc(String str) {

			return PattP.matcher(str).matches();  
			//TODO:可以修改为Java7中的isLetterOrDigit
	}
	

	/**
	 * 判断字符中每个字符的类型
	 * 
	 * @param str 字符串
	 * @see Chars#getType(char)
	 */
	public static CharType[] getType(String str) {
		CharType[] tag = new CharType[str.length()];
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			tag[i] = getType(c);
		}
		return tag;
	}
	
	static Pattern PattP = Pattern.compile("\\pP|\\pS");
	/**
	 * 判断字符类型
	 * @param c 字符
	 * @return
	 */
	public static CharType getType(char c) {
		CharType tag;
		int type = Character.getType(c);
		if (Character.isLowerCase(c)||Character.isUpperCase(c)){
			tag = CharType.L;
		} else if (c == 12288 || c == 32) {//Character.isWhitespace(c) || Character.isSpaceChar(c)
			tag = CharType.B;
		} else if (Character.isDigit(c)) {
			tag = CharType.D;
//		}else if ("一二三四五六七八九十零〇○".indexOf(c) != -1) {
//			tag[i] = CharType.NUM;
		} else if (type>=20&&type<=30){
//			punP.matcher(String.valueOf(c)).matches()){//"/—-()。!,\"'（）！，””<>《》：:#@￥$%^…&*！、.%".
			tag = CharType.P;
			
		} else {
			tag =CharType.C;
		}
		return tag;
	}


	/**
	 * *
	 * 
	 * @param str
	 * @return 字符类型序列
	 */
	public static String getTypeString(String str) {
		CharType[] tag = getType(str);
		String s = type2String(tag);		
		return s;
	}
	
	
	
	public static String type2String(CharType[] tag) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<tag.length;i++){
			sb.append(tag[i]);
			if(i<tag.length-1)
				sb.append(' ');
		}
		return sb.toString();
	}
	/**
	 * 判断字符串是否全为空格
	 * @param w
	 * @return
	 */
	public static boolean isWhiteSpace(String w) {
		for(int i=0;i<w.length();i++){
			char c = w.charAt(i);
			if(!Character.isWhitespace(c)){
				return false;
			}
		}
		return true;
	}

}