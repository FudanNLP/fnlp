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

}
