package org.fnlp.train.corpus;

import java.util.LinkedList;
import java.util.List;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.corpus.fnlp.FNLPDoc;
import org.fnlp.nlp.corpus.fnlp.FNLPSent;

public class TrainTestSplit {

	public static void main(String[] args) throws Exception {

		String datapath = "../data";
		FNLPCorpus corpus = new FNLPCorpus();
		corpus.read(datapath + "/FNLPDATA/WeiboFTB(v1.0).dat", null);
		
		System.out.println(corpus.getDocumenNum());
		System.out.println(corpus.getSentenceNum());
		System.out.println(corpus.getAllPOS());
		
		FNLPDoc doc = corpus.docs.get(0);
		List<FNLPSent> train = doc.sentences.subList(0, 3000);
		List<FNLPSent> test = doc.sentences.subList(3000,doc.sentences.size());
		
		doc.sentences =  new LinkedList<FNLPSent>();
		doc.sentences.addAll(train);
		corpus.writeOne(datapath + "/FNLPDATA/WeiboFTB(v1.0)-train.dat");
		System.out.println(corpus.getSentenceNum());
		System.out.println(corpus.getAllPOS().size());
		
		
		doc.sentences =  new LinkedList<FNLPSent>();
		doc.sentences.addAll(test);
		corpus.writeOne(datapath + "/FNLPDATA/WeiboFTB(v1.0)-test.dat");
		System.out.println(corpus.getSentenceNum());
		System.out.println(corpus.getAllPOS().size());
	}

}
