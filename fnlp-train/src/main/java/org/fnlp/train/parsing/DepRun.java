package org.fnlp.train.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.fnlp.ml.eval.SeqEval;
import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.parser.dep.train.JointParerTester;
import org.fnlp.nlp.parser.dep.train.JointParerTrainer;
import org.fnlp.train.seg.SegTrain;
import org.fnlp.train.tag.ModelOptimization;
import org.fnlp.util.exception.LoadModelException;

public class DepRun {

	static String datapath = "../data";
	static String outputPath = "../data/FNLPDATA/dep-eval.txt";
	static String model = "../models/dep.m";
	static String trainfile = "../data/FNLPDATA/train.dep";
	static String testfile = "../data/FNLPDATA/test.dep";
	static String output = "../data/FNLPDATA/res.dep.3col";
	static PrintWriter bw;

	public static void main(String[] args) throws Exception {		
		bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputPath,true), "utf8"));		
		

		JointParerTrainer trainer = new JointParerTrainer(model);
		int maxite = 50;
		float c = 0.01f;
		trainer.train(trainfile, maxite, c);

		eval();

		bw.println("优化");
		float thres = 1.0E-5f;
		bw.println(thres);

		ModelOptimization op = new ModelOptimization(thres);
		op.optimizeDep(model);

		eval();
		/////////////////////////////////////////

		bw.println("优化");
		thres = 1.0E-4f;
		bw.println(thres);
		op = new ModelOptimization(thres);
		op.optimizeDep(model);

		eval();

		bw.close();
	}

	private static void eval() throws Exception{

		File modelF = new File(model);
		bw.println("模型大小:"+modelF.length()/1000000.0);
		JointParerTester tester  = new JointParerTester(model);
		tester.test(testfile, output, "UTF8");
	}

}
