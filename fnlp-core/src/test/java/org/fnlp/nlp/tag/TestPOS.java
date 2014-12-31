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

package org.fnlp.nlp.tag;

import java.util.ArrayList;

import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.util.MyCollection;

/**
 * 词性标注使用示例
 * @author xpqiu
 *
 */
public class TestPOS {
	
	static POSTagger tag;


	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		tag = new POSTagger("models/seg.m","models/pos.m");
		
		ArrayList<String> str = MyCollection.loadList("./testcase/test case pos.txt",null);
		str.add("周杰伦 生 于 台湾\n我们");
		str.add("分析和比较");
		
		for(String s:str){
			String t = tag.tag(s);
			System.out.println(t);
		}
		
		str.clear();
		str.add("周杰伦 生 于 台湾\n我们");
		
		for(String s:str){
			String t = tag.tagSeged2StringALL(s.split(" "));
			System.out.println(t);
		}
		
	}

}