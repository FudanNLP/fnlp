package org.fnlp.train.prepare;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.corpus.Tags;
import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.corpus.fnlp.FNLPDoc;
import org.fnlp.nlp.corpus.fnlp.FNLPSent;
import org.fnlp.nlp.pipe.seq.String2Sequence;

public class FNLP2POS {
	static ChineseTrans ct = new ChineseTrans();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		String path = "./tmpdata/FNLPDATA";

		String toPath = "./tmpdata/FNLPDATA/data-pos.txt";

		trans(path, toPath);		
		System.out.println(new Date().toString());
		System.out.println("Done!");

	}
	static void trans(String path, String toPath) throws IOException,
	UnsupportedEncodingException, FileNotFoundException {
		FNLPCorpus corpus = new FNLPCorpus();
		corpus.read(path,".dat");		
		trans(corpus,toPath);
	}
	/**
	 * 每个单词为一句话
	 * @param corpus
	 * @param toPath
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	static void trans_w(FNLPCorpus corpus, String toPath) throws IOException,
	UnsupportedEncodingException, FileNotFoundException {

		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(toPath ), "UTF-8"));
		Iterator<FNLPDoc> it1 = corpus.docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				if(!sent.hasTag())
					continue;
				for(int i=0;i<sent.size();i++){
					String w = ct.normalize(sent.words[i]);
					bout.write(w);
					bout.write("\t");
					bout.write(sent.tags[i]);
					bout.write("\n");
					bout.write("\n");
					
					String w1 = ChineseTrans.toFullWidth(w);
					if(!w1.equals(w)){
						bout.write(w1);
						bout.write("\t");
						bout.write(sent.tags[i]);
						bout.write("\n");
						bout.write("\n");
					}
					
				}
				bout.write("\n");
			}

		}
		bout.close();
	}

	static void trans(FNLPCorpus corpus, String toPath) throws IOException,
	UnsupportedEncodingException, FileNotFoundException {

		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(toPath ), "UTF-8"));
		Iterator<FNLPDoc> it1 = corpus.docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				if(!sent.hasTag())
					continue;
				for(int i=0;i<sent.size();i++){
					String w = ct.normalize(sent.words[i]);
					bout.write(w);
					bout.write("\t");
					bout.write(sent.tags[i]);
					bout.write("\n");
				}
				bout.write("\n");
			}

		}
		bout.close();
	}

}
