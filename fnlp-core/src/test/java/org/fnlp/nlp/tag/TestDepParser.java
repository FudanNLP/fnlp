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
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyStrings;
/**
 * 依存句法分析使用示例
 * @author xpqiu
 *
 */
public class TestDepParser {

	private static JointParser parser;
	private static POSTagger tag;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		parser = new JointParser("../models/dep.m");
		tag = new POSTagger("../models/seg.m","../models/pos.m");

		test();

	}

	/**
	 * 只输入句子，不带词性
	 * @throws Exception 
	 */
	private static void test() throws Exception {		

		
//		ArrayList<String> cases = MyCollection.loadList("./testcase/test case parser.txt",null);
//		for(String w:cases){
//			String[][] s = tag.tag2Array(w);
//			String tree = parser.parse2String(s[0],s[1],true);
//			System.out.println(tree);
//		}
		
	}

}