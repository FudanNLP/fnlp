package org.fnlp.train.seg;

import org.fnlp.train.tag.ModelOptimization;
import org.fnlp.util.MyFiles;

public class SegRunFinal {


	public static void main(String[] args) throws Exception {		


		String datapath = "../data";
		String model = "../models/seg.m";
		String templates = "../data/template-seg";

		//合并训练文件

		String allfile = datapath + "/FNLPDATA/all.seg";	
		String testfile = datapath + "/FNLPDATA/test.seg";	
		String trainfile = datapath + "/FNLPDATA/train.seg";


		
		
		  
		MyFiles.combine(allfile, trainfile,testfile);
		SegTrain seg;


		seg = new SegTrain();
		seg.model = model;
		seg.train = allfile;
		seg.templateFile = templates;
		seg.iterNum = 100;
		seg.c = 0.01f;
		seg.train();		

		/////////////////////////////////////////
		float thres = 0.001f;

		ModelOptimization op = new ModelOptimization(thres);
		op.optimizeTag(model);


	}


}
