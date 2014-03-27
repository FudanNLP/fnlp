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

package org.fnlp.nlp.ner.time;

import org.fnlp.nlp.cn.ner.stringPreHandlingModule;

/**
 * 大写数字转化模块的演示demo
 * 
 * @author 曹零07300720158
 *
 */
public class Demo_NumberTranslator {
	public static void main(String[] args){
		String target = "七千零五十一万零三百零五";
		String s  = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		
		target = "一千六加一五八零";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		
		target = "周三十三点";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
		
		target = "三十三点";
		s = stringPreHandlingModule.numberTranslator(target);
		System.out.println(s);
	}
}