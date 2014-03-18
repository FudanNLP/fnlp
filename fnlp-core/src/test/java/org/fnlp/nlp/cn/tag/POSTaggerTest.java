package org.fnlp.nlp.cn.tag;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

public class POSTaggerTest {
	static String s1;
	static POSTagger pos;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		s1 = MyFiles.loadString("../example-data/data-tag.txt");
		pos = new POSTagger("../models/seg.m", "../models/pos.m");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetDictionary() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveDictionary() {
		fail("Not yet implemented");
	}

	@Test
	public void testTag2Array() {
		fail("Not yet implemented");
	}

	@Test
	public void testTag2DoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testTagString() {
		String o1 = pos.tag(s1);
		System.out.println(o1);
	}

	@Test
	public void testFormat() {
		fail("Not yet implemented");
	}

	@Test
	public void testTagSeged() {
		fail("Not yet implemented");
	}

	@Test
	public void testTagSeged2String() {
		fail("Not yet implemented");
	}

	@Test
	public void testTagSeged2StringALL() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSupportedTags() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetTagType() {
		fail("Not yet implemented");
	}

}
