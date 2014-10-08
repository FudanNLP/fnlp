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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.corpus.fnlp.FNLPDoc;
import org.fnlp.nlp.corpus.fnlp.FNLPSent;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

/**
 * 将CONLL格式转为FNLP格式
 * @author xpqiu
 *
 */
public class CoNLL2FNLP{

	private static boolean HASID = false;
	private static HashMap<String, String> posdict;
	private static HashMap<String, String> reldict;
	private static HashMap<String, String> NRdict;
	List<File> files;
	Charset charset;
	FNLPCorpus corpus;
	ChineseTrans ct = new ChineseTrans();
	private TagCorrect tc;

	public CoNLL2FNLP(String path) throws IOException {		
		this(path, "UTF8",null);
	}

	public CoNLL2FNLP(String path, String charsetName, String suffix) throws IOException {
		files = MyFiles.getAllFiles(path, suffix);
		charset = Charset.forName(charsetName);
		tc = new TagCorrect();
	}

	public void read() throws IOException {
		corpus = new FNLPCorpus();
		List<String> carrier = new ArrayList<String>();
		Iterator<File> it = files.iterator();
		while(it.hasNext()){
			BufferedReader bfr =null;
			File file = it.next();
			try {
				
				FileInputStream in = new FileInputStream(file);
				bfr = new BufferedReader(new InputStreamReader(in,charset));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FNLPDoc docs = new FNLPDoc();
			docs.name = file.getName();
			String line = null;
			carrier.clear();
			while ((line = bfr.readLine()) != null) {
				
				line = line.trim();
				
				if (line.matches("^$")){
					if(carrier.size()>0){
						FNLPSent sent = new FNLPSent();						
						sent.parse(carrier,1,HASID); //TODO: 需要根据不同语料修改
						//归一化
						for(int i=0;i<sent.words.length;i++){
							sent.words[i] = ct.normalize(sent.words[i]);
						}
						correct(sent);
						docs.add(sent);
						carrier.clear();
					}
				}else
					carrier.add(line);
			}
			if(!carrier.isEmpty()){
				FNLPSent sent = new FNLPSent();
				
				sent.parse(carrier,1,HASID); //TODO: 需要根据不同语料修改
				correct(sent);
				docs.add(sent);
				carrier.clear();
			}
			corpus.add(docs);
		}
	}

	public void correct(FNLPSent sent) {
		
		for(int i=0;i<sent.tags.length;i++){
//			if(sent.words[i].equals("觉得"))
//				System.out.print("");
			String newtag = posdict.get(sent.tags[i]);
			if(newtag!=null)
				sent.tags[i] = newtag;
			String pos = NRdict.get(sent.words[i]);
			if(pos!=null){
				if(sent.tags[i].equals("实体名")){
					sent.tags[i] = pos;
				}
			}
			String rel = reldict.get(sent.relations[i]);
			
			if(rel!=null){
				sent.relations[i] = rel;
			}
			tc.checkPronoun(sent.words, sent.tags, i);
		}
		
	}

	public static void main(String[] args) throws IOException{
		 posdict = MyCollection.loadStringStringMap("../data/map/pos-ctb2fnlp.txt");
		 reldict = MyCollection.loadStringStringMap("../data/map/rel-ctb2fnlp.txt");
		 NRdict = MyCollection.loadStringStringMap("../data/map/pos-nr.txt");
		CoNLL2FNLP reader = new CoNLL2FNLP("../data/ctb/result.txt","utf-8",".txt");
		HASID = true;
		reader.read();		
		reader.corpus.writeOne("../data/FNLPDATA/ctb7.dat");
		reader.corpus.count("../data/FNLPDATA/count", false);
		MyCollection.write(reader.tc.pronount,"../data/FNLPDATA/pronount.txt");
		System.out.println("Done!");
	}
	
}