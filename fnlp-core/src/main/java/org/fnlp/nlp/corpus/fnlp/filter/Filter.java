/**
*  This file is part of FNLP (formerly FudanNLP).
*  
*  FNLP is free software: you can redistribute it and/or modify
*  it under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  FNLP is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
*  
*  Copyright 2009-2014 www.fnlp.org. All rights reserved. 
*/

package org.fnlp.nlp.corpus.fnlp.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.corpus.fnlp.FNLPDoc;
import org.fnlp.nlp.corpus.fnlp.FNLPSent;
import org.fnlp.util.MyCollection;

public class Filter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FNLPCorpus corpus = new FNLPCorpus();
		corpus.read("./data/FNLPDATA/ctb7.dat",null);
		String pattern  = "北京";
		Pattern p = Pattern.compile(pattern);
		ArrayList<FNLPSent> sents = new ArrayList<FNLPSent> ();
		
		
		Iterator<FNLPDoc> dit = corpus.docs.iterator();
		while(dit.hasNext()){
			FNLPDoc doc = dit.next();
			Iterator<FNLPSent> sit = doc.sentences.iterator();
			while(sit.hasNext()){
				FNLPSent sent = sit.next();
				String s = sent.getSentenceString();
				Matcher m = p.matcher(s);
				if(m.find()){
					System.out.println(s);
					sents.add(sent);
				}
			}
		}
		
		String path = "./tmp";
		MyCollection.write(sents, path +"/wordpos.txt");
		System.out.println("Done");

	}

}