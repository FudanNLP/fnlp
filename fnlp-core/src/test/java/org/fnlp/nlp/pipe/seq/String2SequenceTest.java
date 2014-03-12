package org.fnlp.nlp.pipe.seq;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.util.MyStrings;

public class String2SequenceTest {

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
	public void testGenSequence() {
		String input = "我000们ss001 在 这里 哈哈ssss哈s。";
		String[][] s = String2Sequence.genSequence(input);
		System.out.println(MyStrings.toString(s, ",", "\n"));
	}

}
