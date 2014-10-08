package org.fnlp.nlp.corpus;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StopWordsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testIsStopWordStringIntInt() {
		StopWords sw = new StopWords();
		sw.read("../models/stopwords/StopWords.txt");
		assertTrue(!sw.isStopWord("现在我",2,4));
		assertTrue(sw.isStopWord("我0",2,4));
		assertTrue(sw.isStopWord("我#",2,4));	
		assertTrue(sw.isStopWord(" ",2,4));	
	}

	@Test
	public void testIsStopWordString() {
		StopWords sw = new StopWords();
		sw.read("../models/stopwords/StopWords.txt");
		assertTrue(!sw.isStopWord("现在我"));
	}

}
