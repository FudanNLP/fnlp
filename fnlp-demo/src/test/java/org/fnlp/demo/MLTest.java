package org.fnlp.demo;

import static org.junit.Assert.*;

import org.fnlp.demo.ml.HierClassifierUsage1;
import org.fnlp.demo.ml.HierClassifierUsage2;
import org.fnlp.demo.ml.SequenceLabeling;
import org.fnlp.demo.ml.SimpleClassifier2;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MLTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		
		SequenceLabeling.main(null);
		SimpleClassifier2.main(null);
		HierClassifierUsage1.main(null);
		HierClassifierUsage2.main(null);
		
	}

}
