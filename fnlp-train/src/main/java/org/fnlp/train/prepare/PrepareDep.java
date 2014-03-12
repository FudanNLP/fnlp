package org.fnlp.train.prepare;

import java.util.Date;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.parser.dep.train.JointParerTrainer;

public class PrepareDep {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		FNLPCorpus corpus = new FNLPCorpus();
		//读FNLP数据
		corpus.read("./data/FNLPDATA/ctb7.dat", null);
		//读自有数据
		corpus.readOurCorpus("./data/ourdata",".txt","UTF8");
		
		String path = "./data/FNLPDATA/all.pos";
		corpus.writeOne(path);
		
		String param = "-iter 25 -c 0.1 "+path+" ./models/dep.m";
		JointParerTrainer.main(param.split(" +"));
//		Malt2ParerTester.main(param.split(" +"));
		
		System.out.println(new Date().toString());
		System.out.println("Done!");
	}

}
