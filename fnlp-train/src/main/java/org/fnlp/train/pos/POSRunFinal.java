package org.fnlp.train.pos;

import org.fnlp.train.tag.ModelOptimization;
import org.fnlp.util.MyFiles;

public class POSRunFinal {

	public static void main(String[] args) throws Exception {
		
		String datapath = "../data";
		String model = "../models/pos.m";
		String templates = "../data/template";

		//合并训练文件

		String allfile = datapath + "/FNLPDATA/all.pos";	
		String testfile = datapath + "/FNLPDATA/test.pos";	
		String trainfile = datapath + "/FNLPDATA/train.pos";


		MyFiles.combine(allfile, trainfile,testfile);
		
		POSTrain pos;
		
		
		pos = new POSTrain();
		pos.model = model;
		pos.train = allfile;
		pos.templateFile = templates;
		pos.iterNum = 100;
		pos.c = 0.01f;
		pos.train();		
		
		float thres = 1.0E-5f;		
		ModelOptimization op = new ModelOptimization(thres);
		op.optimizeTag(model);
		
		POSAddEnTag pp = new POSAddEnTag();
		pp.addEnTag(model);
		

	}

}
