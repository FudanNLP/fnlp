package org.fnlp.train.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.fnlp.ml.eval.SeqEval;
import org.fnlp.train.pos.POSTrain;
import org.fnlp.train.tag.ModelOptimization;

public class POSRun {
	
	static String datapath = "../data";
	static String dicfile = datapath + "/FNLPDATA/train.dict";
	static String outputPath = "../data/FNLPDATA/pos-eval.txt";
	static String model = "../models/pos.m";
	static String trainfile = "../data/FNLPDATA/train.pos";
	static String testfile = "../data/FNLPDATA/test.pos";
	static String output = "../data/FNLPDATA/res.pos.3col";
	static String templates = "../data/template";
	static PrintWriter bw;

	public static void main(String[] args) throws Exception {		
		bw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputPath,true), "utf8"));		
		

		POSTrain pos;
		
		
		pos = new POSTrain();
		pos.model = model;
		pos.train = trainfile;
		pos.templateFile = templates;
		pos.iterNum = 50;
		pos.c = 0.01f;
		pos.train();		
		

		eval(pos);		
		
		/////////////////////////////////////////
		
		bw.println("优化");
		float thres = 1.0E-5f;
		bw.println(thres);
		
		ModelOptimization op = new ModelOptimization(thres);
		op.optimizeTag(model);
		
		eval(pos);
		/////////////////////////////////////////
		
		bw.println("优化");
		thres = 1.0E-4f;
		bw.println(thres);
		op = new ModelOptimization(thres);
		op.optimizeTag(model);
		
		eval(pos);
		
		bw.close();

	}

	private static void eval(POSTrain pos)
			throws UnsupportedEncodingException, FileNotFoundException,
			Exception, IOException {
		SeqEval ne;		
		
		File modelF = new File(model);
		bw.println("模型大小:"+modelF.length()/1000000.0);
		
		pos.testfile = testfile ;
		pos.output = output;
		pos.hasLabel = true;
		pos.cl = null;
		pos.test();
		
		ne = new SeqEval();
		ne.NoSegLabel = true;
		ne.readOOV(dicfile);
		ne.read(output);
		String res = ne.calcByType2();
		bw.println(res);
		
	}
	
	

}
