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

import org.fnlp.nlp.cn.ner.TimeNormalizer;
import org.fnlp.nlp.cn.ner.TimeUnit;

/**
 * TimeNormalizer的演示demo
 * 
 * @author 曹零07300720158
 *
 */
public class Demo_TimeNormalizer {
	public static void main(String[] args){
		String target = "08年北京申办奥运会，8月8号开幕式，九月十八号闭幕式。" +
				"1年后的7月21号发生了件大事。" +
				"今天我本想去世博会，但是人太多了，直到晚上9点人还是那么多。" +
				"考虑到明天和后天人还是那么多，决定下周日再去。";
		TimeNormalizer normalizer;
//		normalizer= new TimeNormalizer();	
//				
//		try {
//			normalizer.text2binModel("./model/TimeExp-Rules.txt","./model/TimeExp.gz");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		normalizer = new TimeNormalizer("./model/TimeExp.gz");
		normalizer.parse(target);
		TimeUnit[] unit = normalizer.getTimeUnit();
		for(int i = 0; i < unit.length; i++){
			System.out.println(unit[i]);
		}
	}
}