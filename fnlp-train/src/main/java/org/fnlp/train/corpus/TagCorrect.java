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

package org.fnlp.train.corpus;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

import org.fnlp.nlp.corpus.fnlp.FNLPSent;
import org.fnlp.util.MyCollection;

/**
 * ctb词性校正
 * @author xpqiu
 *
 */
public class TagCorrect {

	private HashSet<String> ppronoun;
	private HashSet<String> qpronoun;
	private HashSet<String> auxiliaryVerb;
	public TreeSet<String> pronount;


	public TagCorrect() throws IOException{
		ppronoun = MyCollection.loadSet("../data/FNLPDATA/词性字典/人称代词.txt");
		qpronoun = MyCollection.loadSet("../data/FNLPDATA/词性字典/疑问代词.txt");
		auxiliaryVerb = MyCollection.loadSet("../data/FNLPDATA/词性字典/情态词.txt");
		pronount = new TreeSet<String>();
	}

	public  void uniqueWord(String path, List<FNLPSent> sentList) {
		HashMap<String,Integer> uniWord = new HashMap<String,Integer>();
		for (FNLPSent sentence : sentList) {
			String[] words = sentence.words;
			String[] tags = sentence.tags;
			for (int i = 0; i < tags.length; i++) {
				if (tags[i].equals("代词")) {
					if (uniWord.containsKey(words[i])) {
						int count = uniWord.get(words[i]);
						uniWord.put(words[i], count+1);
					}
					else
						uniWord.put(words[i], 1);
				}
			}
		}
		//        MyCollection.writeFile(path);
	}


	public  void checkPronoun(String[] words,String[] tags,int i){
		if (tags[i].equals("代词")){
			if (qpronoun.contains(words[i])) 
				tags[i]= "疑问代词";
			else if (ppronoun.contains(words[i]))
				tags[i]= "人称代词";
			else{
				tags[i]="指示词";
				pronount.add(words[i]);
			}
		}else if(tags[i].equals("动词")){
			if (auxiliaryVerb.contains(words[i])) 
				tags[i]= "情态词";
		}
	}

}