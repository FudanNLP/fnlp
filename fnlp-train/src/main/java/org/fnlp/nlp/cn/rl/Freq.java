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

package org.fnlp.nlp.cn.rl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.fnlp.util.MyCollection;

public class Freq {

	private void unigram(HashMap<String, Integer> wordsFreq, String[] words) {
		for(int i=0;i<words.length;i++){
			String w = words[i];
			int len = w.length();

			if (wordsFreq.containsKey(w)) {
				wordsFreq.put(w, wordsFreq.get(w) + 1);
			} else {
				wordsFreq.put(w, 1);
			}
		}
	}
	
	public void countWords(String inpath,String outpath, boolean b) throws Exception{

		HashMap<String, Integer> wordsFreq = new HashMap<String, Integer>();

		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(inpath),"utf8"));
		String line = null;			
		int count=0;
		while ((line = bfr.readLine()) != null) {
			line = line.replaceAll("百\\s?度\\s?知\\s?道.*","");
			if(line.length()==0)
				continue;
			count++;
			String[] words = line.split(" +");

			unigram(wordsFreq, words);
//				bigram(wordsFreq, words);
			}
		System.out.println("问题个数： "+count);
		List<Entry> sortedwordsFreq = MyCollection.sort(wordsFreq);		
		writeFile(sortedwordsFreq, outpath+"/wc.txt", b);
		
		System.out.println("ngram个数： "+wordsFreq.size());

	}

	private void bigram(HashMap<String, Integer> wordsFreq, String[] words) {
		for(int i=1;i<words.length;i++){					
			String w = words[i-1]+" "+words[i];
//			w = words[i-2]+" "+w;
			if(w.contains(">"))
				continue;
			if (wordsFreq.containsKey(w)) {
				wordsFreq.put(w, wordsFreq.get(w) + 1);
			} else {
				wordsFreq.put(w, 1);
			}
		}
	}

	private void writeFile(List<Entry> entryList, String file, boolean b) {
		try {
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			Iterator<Entry> it = entryList.iterator();
			while (it.hasNext()) {
				Entry entry = it.next();
				if((Integer)entry.getValue()<1000)
					continue;
				bout.write(entry.getKey().toString());
				if (b) {
					bout.write("\t");
					bout.write(entry.getValue().toString());
				}
				bout.write("\n");
			}
			bout.close();

		} catch (Exception e) {

		}
	}

	public static void main(String[] args) throws Exception{
		Freq f = new Freq();
		f.countWords("./tmp/cqa-seg.txt","./tmp", true);
		System.out.println("Done!");
		
	}
}