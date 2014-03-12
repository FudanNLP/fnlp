package org.fnlp.nlp.cn.rl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gnu.trove.set.hash.THashSet;

public class RLSegTest {
	RLSeg rlseg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		CWSTagger tag = new CWSTagger("./models/seg.m");	

		rlseg = new RLSeg(tag,"./tmpdata/FNLPDATA/all.dict");
	}

	@After
	public void tearDown() throws Exception {
		rlseg.close();
	}

	@Test
	public void testGetNewWords() throws IOException {
		THashSet<String> newset = new THashSet<String>();
		THashSet<String> set;
//		set = rlseg.getNewWords("考几");
//		set = rlseg.getNewWords("抛诸脑后");
		set = rlseg.getNewWords("买iphone");
		System.out.println(set);
		
	}

}
