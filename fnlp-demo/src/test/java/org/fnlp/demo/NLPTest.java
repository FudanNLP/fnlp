package org.fnlp.demo;

import static org.junit.Assert.*;

import org.fnlp.demo.ml.HierClassifierUsage1;
import org.fnlp.demo.ml.HierClassifierUsage2;
import org.fnlp.demo.ml.SequenceLabeling;
import org.fnlp.demo.ml.SimpleClassifier2;
import org.fnlp.demo.nlp.AnaphoraResolution;
import org.fnlp.demo.nlp.ChineseWordSegmentation;
import org.fnlp.demo.nlp.DepParser;
import org.fnlp.demo.nlp.KeyWordExtraction;
import org.fnlp.demo.nlp.NamedEntityRecognition;
import org.fnlp.demo.nlp.PartsOfSpeechTag;
import org.fnlp.demo.nlp.TimeExpressionRecognition;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class NLPTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		ChineseWordSegmentation.main(null);
		PartsOfSpeechTag.main(null);
		DepParser.main(null);
		KeyWordExtraction.main(null);
		NamedEntityRecognition.main(null);
		TimeExpressionRecognition.main(null);
//		AnaphoraResolution.main(null);
		
	}

}
