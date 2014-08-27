package org.fnlp.train.tag;

import java.io.IOException;
import java.util.HashSet;

import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.util.MyCollection;

public class Clean2word {

	public static void main(String[] args) throws IOException {

		HashSet<String> wset = new HashSet<String>();
		String file = "../data/tmp.txt";
		HashSet<String> set = MyCollection.loadSet(file , false);
		for(String w: set){
			if(w.length()>=3||w.length()<=1)
				continue;
			if(Chars.isLetterOrDigitOrPunc(w))
				continue;

			wset.add(w);
		}

		MyCollection.write(wset, "../data/word.txt");
		System.out.print("Done");

	}

}
