package org.fnlp.nlp.similarity;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.data.reader.StringReader;
import org.fnlp.nlp.similarity.train.WordCluster;

public class WordClusterTest {

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
	public void testStartClustering() {
		WordCluster wc = new WordCluster();
		wc.slotsize =6;
		String[] strs = new String[]{"猪肉","狗肉","狗头","鸡头","猪头","鸡肉"};
		StringReader r = new StringReader(strs );
		wc.read(r);

		try {
			Cluster root = wc.startClustering();
			DrawTree.printTree(root,"./tmp/t.png");
			wc.saveModel("./tmp/t.m");
			wc.saveTxt("./tmp/t.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
