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

package org.fnlp.nlp.cn.tag;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.fnlp.ml.types.Dictionary;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class CWSTaggerTest {
	static CWSTagger tag;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tag = new CWSTagger("../models/seg.m");	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testTagString() {
		String str = "江苏省兴化市沈伦镇樊荣村委会";
		String s = tag.tag(str);
		assertTrue(s.equals("江苏省 兴化市 沈伦镇 樊荣 村委会"));
		
		ArrayList<String> al = new ArrayList<String>();
		al.add("兴化市");
		al.add("沈伦镇");
		al.add("樊荣村委会");
		Dictionary dict = new Dictionary(false);
		dict.addSegDict(al);
		tag.setDictionary(dict);
		s = tag.tag(str);
		assertTrue(s.equals("江苏省 兴化市 沈伦镇 樊荣村委会"));
	}
	@Test
	public void testTagString1() {
		String str = "以及在电视 电影反各类艺术图形的生成和处理过程中 微机图形功能都是很重要的 随着高分辨率图形显示设备的增加和普及 对空间物体进行三维彩色图形表示的要求日趋增高 尤其是在工业设计 建筑设计 地形图绘制";
		
		String s = tag.tag(str);
		System.out.println(s);
//		assertTrue(s.equals("江苏省 兴化市 沈伦镇 樊荣 村委会"));
		
		ArrayList<String> al = new ArrayList<String>();
		al.add("图形显示");
		Dictionary dict = new Dictionary(false);
		dict.addSegDict(al);
		tag.setDictionary(dict);
		s = tag.tag(str);
		System.out.println(s);
		assertTrue(s.indexOf("图形显示")!=-1);
	}

}