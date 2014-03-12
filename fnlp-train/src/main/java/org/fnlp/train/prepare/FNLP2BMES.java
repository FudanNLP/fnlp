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

public class FNLP2BMES {
	static ChineseTrans ct = new ChineseTrans();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FNLPCorpus corpus = new FNLPCorpus();
		corpus.read("./tmpdata/FNLPDATA",".dat");	
		String file = "./tmpdata/FNLPDATA/data-cws.txt";

		w2BMES(corpus, file);		
		System.out.println(new Date().toString());
		System.out.println("Done!");

	}
	
	static void w2BMES_Word(FNLPCorpus corpus, String file)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file ), "UTF-8"));
		Iterator<FNLPDoc> it1 = corpus.docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				for(String w:sent.words){
				
				String s = Tags.genSequence4Tags(new String[]{w});
				bout.write(s);
				String w1 = ChineseTrans.toFullWidth(w);
				if(!w1.equals(w)){
					String ss = Tags.genSequence4Tags(new String[]{w1});
					bout.write(ss);
				}
				
				}
			}

		}
		bout.close();
	}

	static void w2BMES(FNLPCorpus corpus, String file)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file ), "UTF-8"));
		Iterator<FNLPDoc> it1 = corpus.docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				String s = Tags.genSequence4Tags(sent.words);
				bout.write(s);
				
				for(int i=0;i<sent.words.length;i++){
					String w1 = ChineseTrans.toFullWidth(sent.words[i]);
					if(!w1.equals(sent.words[i])){
						String ss = Tags.genSequence4Tags(new String[]{w1});
						bout.write(ss);
					}
				}				
				
				
			}

		}
		bout.close();
	}

}
