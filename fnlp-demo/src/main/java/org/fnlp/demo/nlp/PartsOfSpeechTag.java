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

package org.fnlp.demo.nlp;

import org.fnlp.ml.types.Dictionary;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.POSTagger;

/**
 * 词性标注使用示例
 * @author xpqiu
 *
 */
public class PartsOfSpeechTag {
	
	static POSTagger tag;

	/**
	 * 主程序
	 * @param args
	 * @throws Exception
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {

		
		CWSTagger cws = new CWSTagger("../models/seg.m");
		tag = new POSTagger(cws,"../models/pos.m");
		
		System.out.println("得到支持的词性标签集合");
		System.out.println(tag.getSupportedTags());
		System.out.println(tag.getSupportedTags().size());
		System.out.println("\n");
		
		String str = "媒体计算研究所成立了，高级数据挖掘很难。乐phone很好！";
		String s = tag.tag(str);
		System.out.println("处理未分词的句子");
		System.out.println(s);
		
		System.out.println("使用英文标签");
		tag.SetTagType("en");		
		System.out.println(tag.getSupportedTags());
		System.out.println(tag.getSupportedTags().size());
		s = tag.tag(str);
		System.out.println(s);		
		System.out.println();
		
		CWSTagger cws2 = new CWSTagger("../models/seg.m", new Dictionary("../models/dict.txt"));
		
		//bool值指定该dict是否用于cws分词（分词和词性可以使用不同的词典）
		tag = new POSTagger(cws2, "../models/pos.m"
				, new Dictionary("../models/dict.txt"), true);//true就替换了之前的dict.txt
		tag.removeDictionary(false);//不移除分词的词典
		tag.setDictionary(new Dictionary("../models/dict.txt"), false);//设置POS词典，分词使用原来设置
		
		String str2 = "媒体计算研究所成立了，高级数据挖掘很难。乐phone很好！";
		String s2 = tag.tag(str2);
		System.out.println("处理未分词的句子，使用词典");
		System.out.println(s2);
		System.out.println();
		
		Dictionary dict = new Dictionary();
		dict.add("媒体计算","mypos1","mypos2");
		dict.add("乐phone","专有名");
		tag.setDictionary(dict, true);
		String s22 = tag.tag(str2);
		System.out.println(s22);
		System.out.println();
		
		POSTagger tag1 = new POSTagger("../models/pos.m");
		String str1 = "媒体计算 研究所 成立 了 , 高级 数据挖掘 很 难";
		String[] w = str1.split(" ");
		String[] s1 = tag1.tagSeged(w);
		System.out.println("直接处理分好词的句子:++++++++++");
		for(int i=0;i<s1.length;i++){
			System.out.print(w[i]+"/"+s1[i]+" ");
		}
		System.out.println("\n");
		
		POSTagger tag3 = new POSTagger("../models/pos.m", new Dictionary("../models/dict.txt"));
		String str3 = "媒体计算 研究所 成立 了 , 高级 数据挖掘 很 难 ";
		String[] w3 = str3.split(" ");
		String[] s3 = tag3.tagSeged(w3);
		System.out.println("直接处理分好词的句子，使用词典");
		for(int i=0;i<s3.length;i++){
			System.out.print(w3[i]+"/"+s3[i]+" ");
		}
		System.out.println("\n");
		
		//????????????????????????????
		
		System.out.println("重新构造");
		cws = new CWSTagger("../models/seg.m");
		tag = new POSTagger(cws,"../models/pos.m");
		str = "媒体计算研究所成立了, 高级数据挖掘很难";
		System.out.println(tag.tag(str));
		String[][] sa = tag.tag2Array(str);
		for(int i = 0; i < sa.length; i++) {
			for(int j = 0; j < sa[i].length; j++) {
					System.out.print(sa[i][j] + " ");
			}
			System.out.println();
		}
		
		String s4 = tag.tagFile("../example-data/data-tag.txt");
		System.out.println("\n处理文件：");
		System.out.println(s4);
	}

}