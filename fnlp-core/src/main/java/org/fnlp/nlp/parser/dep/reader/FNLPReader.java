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

package org.fnlp.nlp.parser.dep.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.corpus.fnlp.FNLPDoc;
import org.fnlp.nlp.corpus.fnlp.FNLPSent;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.Target;

public class FNLPReader extends Reader {

	BufferedReader reader = null;
	Sentence next = null;
	List<String[]> carrier = new ArrayList<String[]>();
	private FNLPCorpus corpus;
	private int  size;
	private int curSentNo;
	private int curDocNo;
	private FNLPDoc curDoc;

	public FNLPReader(String filepath) throws IOException {
		corpus = new FNLPCorpus();
		corpus.read(filepath, null);
		size = corpus.getDocumenNum();
		curDocNo = 0;
		curSentNo = 0;
		
	}


	public boolean hasNext() {
		
		if(curDoc ==null){
			curDoc = corpus.getDoc(curDocNo++);
		}
		if(curDoc==null){
			corpus =null;
			return false;
		}
		
		FNLPSent sent = curDoc.getSent(curSentNo++);
		if(sent == null){
			curDoc = null;
			curSentNo = 0;
			return hasNext();
		}
		
		Target target = new Target(sent.heads,sent.relations);
		next = new Sentence(sent.words, sent.tags, target);
		
		return true;
	}

	public Instance next() {
		Sentence cur = next;
		
		return cur;
	}
	public static void main(String args[]) throws IOException{
		FNLPReader mr = new FNLPReader("./tmpdata/FNLPDATA/ctb7.dat");
		while (mr.hasNext()){
			Sentence sen = mr.next;
			for(int i=0;i<sen.length();i++)
				System.out.print(mr.next.getDepClass(i)+"\n");
		}
		
	}
}