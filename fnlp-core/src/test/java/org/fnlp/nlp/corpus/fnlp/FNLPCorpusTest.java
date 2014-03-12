package org.fnlp.nlp.corpus.fnlp;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class FNLPCorpusTest {

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
	public void test() {
		fail("Not yet implemented");
	}
	@Test
	public void testReadCWS() throws IOException {
		FNLPCorpus corpus = new FNLPCorpus();
		corpus.readCWS("./data/FNLPDATA/seg",".txt","UTF8");		
		corpus.writeOne("./tmp/seg.dat");
//		corpus.count("./tmp", true);
		System.out.println("Done!");
	}
	

}
