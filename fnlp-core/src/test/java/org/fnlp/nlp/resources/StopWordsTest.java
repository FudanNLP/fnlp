package org.fnlp.nlp.resources;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.nlp.corpus.StopWords;

public class StopWordsTest {

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
	public void testRead() {
		fail("Not yet implemented");
	}

	@Test
	public void testPhraseDel() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsStopWord() {
		StopWords sw = new StopWords();
		sw.read("./models/stopwords/StopWords.txt");
		assertTrue(sw.isStopWord("现在我"));
		assertTrue(sw.isStopWord("我0"));
		assertTrue(sw.isStopWord("我#"));	
		assertTrue(sw.isStopWord(" "));	
	}

}
