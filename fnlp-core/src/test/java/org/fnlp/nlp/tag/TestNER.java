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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.cn.tag.NERTagger;
import org.fnlp.util.MyCollection;

/**
 * 实体名识别使用示例
 * @author xpqiu
 *
 */
public class TestNER {	


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		NERTagger tag = new NERTagger("./models/seg.m","./models/pos.m");
		ArrayList<String> str = MyCollection.loadList("./testcase/test case ner.txt",null);
		
		
		for(String s:str){
			System.out.println(s);
			HashMap<String, String> t = tag.tag(s);
			System.out.println(t);
		}
		
		
		testFile(tag,"../FudanNLP/example-data/text/1.txt");
		
	}
	public static void testFile(NERTagger tag, String file) throws Exception{
		BufferedReader bin;
		StringBuilder res = new StringBuilder();
		String str1=null;
		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			while(str!=null){
				str = ChineseTrans.toFullWidth(str);
				res.append(str);				
				res.append("\n");
				str = lbin.readLine();
			}
			lbin.close();
			str1 = res.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		long beginTime = System.currentTimeMillis();
		HashMap<String, String> map = new HashMap<String, String>(); 
		tag.tag(str1,map);
		float totalTime = (System.currentTimeMillis() - beginTime)/ 1000.0f;
		System.out.println("总时间(秒):" + totalTime);
		System.out.println("速度(字/秒):" + str1.length()/totalTime);
		System.out.println(map);
	}

}