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


import java.util.ArrayList;

import org.fnlp.ml.types.Dictionary;
import org.fnlp.nlp.cn.tag.CWSTagger;


/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class ChineseWordSegmentation {
	/**
	 * 主程序
	 * @param args 
	 * @throws Exception
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		CWSTagger tag = new CWSTagger("../models/seg.m");
		System.out.println("不使用词典的分词：");
		String str = " 媒体计算研究所成立了, 高级数据挖掘(data mining)很难。 乐phone热卖！";
		String s = tag.tag(str);
		System.out.println(s);
		
		//设置英文预处理
		tag.setEnFilter(true);
		s = tag.tag(str);
		System.out.println(s);
//		tag.setEnFilter(false);
		
		System.out.println("\n设置临时词典：");
		ArrayList<String> al = new ArrayList<String>();
		al.add("数据挖掘");
		al.add("媒体计算研究所");
		al.add("乐phone");
		Dictionary dict = new Dictionary(false);
		dict.addSegDict(al);
		tag.setDictionary(dict);
		s = tag.tag(str);
		System.out.println(s);
		
		
		CWSTagger tag2 = new CWSTagger("../models/seg.m", new Dictionary("../models/dict.txt"));
		System.out.println("\n使用词典的分词：");
		String str2 = "媒体计算研究所成立了, 高级数据挖掘很难。 乐phone热卖！";
		String s2 = tag2.tag(str2);
		System.out.println(s2);
		
		//使用不严格的词典
		CWSTagger tag3 = new CWSTagger("../models/seg.m", new Dictionary("../models/dict_ambiguity.txt",true));
		//尽量满足词典，比如词典中有“成立”“成立了”和“了”, 会使用Viterbi决定更合理的输出
		System.out.println("\n使用不严格的词典的分词：");
		String str3 = "媒体计算研究所成立了, 高级数据挖掘很难";
		String s3 = tag3.tag(str3);
		System.out.println(s3);
		str3 = "我送给力学系的同学一个玩具 (送给给力力学力学系都在词典中)";
		s3 = tag3.tag(str3);
		System.out.println(s3);
		
		System.out.println("\n处理文件：");
		String s4 = tag.tagFile("../example-data/data-tag.txt");
		System.out.println(s4);
		
		String s5 = tag2.tagFile("../example-data/data-tag.txt");
		System.out.println(s5);
		
	}

}