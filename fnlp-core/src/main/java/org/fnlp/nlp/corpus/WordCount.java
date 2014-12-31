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

package org.fnlp.nlp.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;

public class WordCount {
	HashMap<String, Integer> wordsFreq = new HashMap<String, Integer>();
	CWSTagger seg;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		WordCount wc = new WordCount();
		 wc.seg = new CWSTagger("../models/seg.m");
		
		wc.count("./tmp/filterTweets.y");
		wc.count("./tmp/filterTweets.n");		
		wc.write("./tmp/wc.txt", true);
		wc.filter(500);
		wc.write("./tmp/wcc.txt", false);
	}

	private void filter(int i) {
		HashMap<String, Integer> newwordsFreq = new HashMap<String, Integer>();
		for(Entry<String, Integer> e : wordsFreq.entrySet()){
			
			Integer v = e.getValue();
			if(v>i){
				String key = e.getKey();
				newwordsFreq.put(key, v);
			}
		}
		wordsFreq.clear();
		wordsFreq = newwordsFreq;
	}

	private void count(String ifile) throws IOException {
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(ifile),"utf8"));
		String line = null;	
		int count=0;
		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			if(count%1000==0)
				System.out.println(count);
			count++;
			if(seg!=null){
				String[] words = seg.tag2Array(line);
				for(String w : words){
					add(w);
				}
				
			}
			
		}
		bfr.close();
		
	}

	/**
	 * 统计词信息
	 * @param w
	 */
	public void add(String w){

		if (wordsFreq.containsKey(w)) {
			wordsFreq.put(w, wordsFreq.get(w) + 1);
		} else {
			wordsFreq.put(w, 1);
		}
	}

	public void write(String path, boolean b){
		List<Entry> sortedwordsFreq = MyCollection.sort(wordsFreq);		
		MyCollection.write(sortedwordsFreq, path, b);
	}

}