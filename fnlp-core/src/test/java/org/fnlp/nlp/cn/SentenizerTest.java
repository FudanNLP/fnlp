package org.fnlp.nlp.cn;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.util.MyStrings;

public class SentenizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testSplit() {
		String sent = "  回顾王适娴职业生涯成长历程，2008年只参加了两站国内进行的公开赛？呵呵";
		String[] subsents = Sentenizer.split(sent);
		System.out.println(MyStrings.toString(subsents,"\n"));
	}

}
