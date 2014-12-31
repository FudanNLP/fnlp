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

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class TestSEG {
	
	public static void main(String[] args) throws Exception {
		CWSTagger tag = new CWSTagger("./models/seg.m");
		ArrayList<String> str = MyCollection.loadList("./testcase/seg.txt",null);
		for(String s:str){			
			String t = tag.tag(s);
//			t = tag.tag(t);
			System.out.println(t);
		}
		tag.setEnFilter(false);
		for(String s:str){
			String t = tag.tag(s);
			System.out.println(t);
		}
		
		ArrayList<String> str1 = MyCollection.loadList("data/FNLPDATA/seg/bad case.txt",null);
		for(String s:str1){		
			s = s.trim();
			String s1 = s.replaceAll(" ", "");
			String t = tag.tag(s1);
			System.out.println("处理： "+t);
			if(!t.equals(s))
				System.err.println("正确： "+s);	
		}
		
	}
	

}