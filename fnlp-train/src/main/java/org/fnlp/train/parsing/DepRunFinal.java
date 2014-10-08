package org.fnlp.train.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.fnlp.ml.eval.SeqEval;
import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.parser.dep.train.JointParerTester;
import org.fnlp.nlp.parser.dep.train.JointParerTrainer;
import org.fnlp.train.seg.SegTrain;
import org.fnlp.train.tag.ModelOptimization;
import org.fnlp.util.MyFiles;

public class DepRunFinal {

	public static void main(String[] args) throws Exception {		

		String datapath = "../data";
		String model = "../models/dep.m";
		//合并训练文件

		String allfile = datapath + "/FNLPDATA/all.dep";	
		MyFiles.delete(allfile);
		String testfile = datapath + "/FNLPDATA/test.dep";	
		String trainfile = datapath + "/FNLPDATA/train.dep";


		MyFiles.combine(allfile, trainfile,testfile);


		JointParerTrainer trainer = new JointParerTrainer(model);
		int maxite = 100;
		float c = 0.01f;
		trainer.train(allfile, maxite, c);
		
		float thres = 0.00001f;
		ModelOptimization op = new ModelOptimization(thres);
		op.optimizeDep(model);
		
	}

}
