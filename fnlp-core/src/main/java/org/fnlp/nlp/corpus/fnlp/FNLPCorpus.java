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

package org.fnlp.nlp.corpus.fnlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;
import org.fnlp.util.UnicodeReader;
import org.fnlp.util.ValueComparator;


/**
 * FudanNLP标准数据格式
 * @since FudanNLP 1.5
 */
public class FNLPCorpus {
	public LinkedList<FNLPDoc> docs  = new LinkedList<FNLPDoc>();

	public FNLPCorpus() {
	}

	public LinkedList<FNLPDoc> getDocumenList(){
		return this.docs;
	}

	public void add(FNLPDoc doc) {
		docs.add(doc);
	}
	/**
	 * 将数据输出到多个文件，每个DOC一个文件
	 * @param path
	 */
	public void write(String path){

		File f = new File(path);		
		if(!f.exists()){
			f.mkdirs();
		}

		Iterator<FNLPDoc> it = docs.iterator();
		while(it.hasNext()){
			FNLPDoc doc = it.next();
			doc.write(path);
		}

	}
	/**
	 * 将数据输出到一个文件
	 * @param path
	 */
	public void writeOne(String path){

		File f = new File(path);		
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(path),"utf8");

			Iterator<FNLPDoc> it = docs.iterator();
			while(it.hasNext()){
				FNLPDoc doc = it.next();
				out.write(doc.toString());
				out.write("\n");
			}

			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}


	}


	/**
	 * 统计词信息
	 * @param path
	 * @param b 是否输出词频
	 * @throws IOException 
	 */
	public void count(String path,boolean b) throws IOException{

		HashMap<String, Integer> wordsFreq = new HashMap<String, Integer>();
		HashMap<Integer, Integer> lensFreq = new HashMap<Integer, Integer>();
		HashMap<String, Integer> posFreq = new HashMap<String, Integer>();
		HashMap<String, Integer> relsFreq = new HashMap<String, Integer>();
		HashMap<String, HashSet<String>> wordsPOS = new HashMap<String, HashSet<String>>();
		
		int total = 0;
		int totalsent = 0;
		Iterator<FNLPDoc> dit = docs.iterator();
		while(dit.hasNext()){
			FNLPDoc doc = dit.next();
			Iterator<FNLPSent> sit = doc.sentences.iterator();
			while(sit.hasNext()){
				FNLPSent sent = sit.next();
				totalsent++;
				for(int i=0;i<sent.words.length;i++){
					total++;
					String w = sent.words[i];
					int len = w.length();
					String pos = sent.tags[i];
					if(!pos.equals("专有名"))
						continue;
					String rels = sent.relations[i];
//					if(len > 20){
//						System.out.println(w);						
//					}
					
					if (posFreq.containsKey(pos)) {
						posFreq.put(pos, posFreq.get(pos) + 1);
					} else {
						posFreq.put(pos, 1);
					}

					if (lensFreq.containsKey(len)) {
						lensFreq.put(len, lensFreq.get(len) + 1);
					} else {
						lensFreq.put(len, 1);
					}

					if (wordsFreq.containsKey(w)) {
						wordsFreq.put(w, wordsFreq.get(w) + 1);
						
					} else {
						wordsFreq.put(w, 1);						
					}
					
					if (wordsPOS.containsKey(w)) {
						wordsPOS.get(w).add(pos);						
					} else {
						HashSet<String> posset = new HashSet<String>();
						posset.add(pos);
						wordsPOS.put(w, posset);			
					}
					
					
					
					if (relsFreq.containsKey(rels)) {
						relsFreq.put(rels, relsFreq.get(rels) + 1);
					} else {
						relsFreq.put(rels, 1);
					}
				}
			}
		}
		System.out.println("总字数："+total);
		System.out.println("总句数："+totalsent);
		List<Entry> sortedwordsFreq = MyCollection.sort(wordsFreq);		
		MyCollection.write(sortedwordsFreq, path+"/wc.txt", b);
		
		List<Entry> sortedposFreq = MyCollection.sort(posFreq);		
		MyCollection.write(sortedposFreq, path+"/pos.txt", b);
		
		List<Entry> sortedlrelsFreq = MyCollection.sort(relsFreq);
		MyCollection.write(sortedlrelsFreq, path+"/relations.txt", b);

		List<Entry> sortedlensFreq = MyCollection.sort(lensFreq);
		MyCollection.write(sortedlensFreq, path+"/lc.txt", b);
		
		MyCollection.write(wordsPOS, path+"/wordpos.txt");
	}

	
	
	public void read(String path,String suffix) throws IOException {
		List<File> files = MyFiles.getAllFiles(path, suffix);

		List<String> carrier = new ArrayList<String>();
		Iterator<File> it = files.iterator();
		while(it.hasNext()){
			BufferedReader bfr =null;
			File file = it.next();
			try {
				bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			String line = null;			
			while ((line = bfr.readLine()) != null) {
				line = line.trim();
				if(line.equalsIgnoreCase("<doc>")){
					carrier.clear();
				}
				else if (line.matches("</doc>")){
					FNLPDoc doc = new FNLPDoc(carrier);
					docs.add(doc);
				}else
					carrier.add(line);
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		FNLPCorpus corpus = new FNLPCorpus();
//		corpus.read("./data/FNLPDATA/ctb7.dat",null);		
		corpus.readOurCorpus("./data/ourdata",null,"UTF8");
		corpus.count("./tmp/",false);
		System.out.println(new Date().toString());
		System.out.println("Done!");
		
	}
	/**
	 * 读分词/词性的文件
	 * 文件格式为：
	 * word1/pos1 word2/pos2 ... wordn/posn
	 * @param path
	 * @param suffix
	 * @param charset
	 * @throws IOException
	 */
	public void readPOS(String path, String suffix, String charset) throws IOException {
		List<File> files = MyFiles.getAllFiles(path, suffix);//".txt"
		
		Iterator<File> it = files.iterator();
		while(it.hasNext()){
			BufferedReader bfr =null;
			File file = it.next();
			try {
				FileInputStream in = new FileInputStream(file);
				bfr = new BufferedReader(new UnicodeReader(in,charset));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			FNLPDoc doc = new FNLPDoc();
			doc.name = file.getName();
			
			String line = null;
			while ((line = bfr.readLine()) != null) {
				line = line.trim();
				if (line.matches("^$"))
					continue;
				FNLPSent sent = new FNLPSent();
				sent.parseTagedLine(line);
				doc.add(sent);
			}			
			add(doc);
		}
	}
	
	/**
	 * 读只分词的文件
	 * 文件格式为：
	 * word1 word2 ... wordn
	 * @param path
	 * @param suffix
	 * @param charset
	 * @throws IOException
	 */
	public void readCWS(String path, String suffix, String charset) throws IOException {
		List<File> files = MyFiles.getAllFiles(path, suffix);//".txt"
		
		Iterator<File> it = files.iterator();
		while(it.hasNext()){
			BufferedReader bfr =null;
			File file = it.next();
			try {
				FileInputStream in = new FileInputStream(file);
				bfr = new BufferedReader(new UnicodeReader(in,charset));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			FNLPDoc doc = new FNLPDoc();
			doc.name = file.getName();
			
			String line = null;
			while ((line = bfr.readLine()) != null) {
				line = line.trim();
				if (line.matches("^$"))
					continue;
				FNLPSent sent = new FNLPSent();
				sent.parseSegedLine(line);
				doc.add(sent);
			}			
			add(doc);
		}
	}
	/**
	 * 读自己标注的文件
	 * @param path
	 * @param suffix
	 * @param charset
	 * @throws IOException
	 */
	public void readOurCorpus(String path, String suffix, String charset) throws IOException {
		List<File> files = MyFiles.getAllFiles(path, suffix);//".txt"
		
		Iterator<File> it = files.iterator();
		while(it.hasNext()){
			BufferedReader bfr =null;
			File file = it.next();
			try {
				FileInputStream in = new FileInputStream(file);
				bfr = new BufferedReader(new UnicodeReader(in,charset));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			FNLPDoc doc = new FNLPDoc();
			doc.name = file.getName();
			
			String line = null;
			while ((line = bfr.readLine()) != null) {
				line = line.trim();
				if (line.matches("^$"))
					continue;
				String[] toks = line.split("\\s+");
				assert(toks.length%5==0);
				int len = toks.length/5;
				
				FNLPSent sent = new FNLPSent(len);
				int base = 0;
				for(int i=0;i<len;i++){
					int idx = Integer.valueOf( toks[base]);
					sent.words[idx] = toks[base+1]; 
					sent.tags[idx] = toks[base+2];
					sent.heads[idx] = Integer.valueOf(toks[base+3]);
					sent.relations[idx] = toks[base+4];
					base += 5;
				}
				
				doc.add(sent);
			}			
			add(doc);
		}
	}

	public int getDocumenNum() {
		
		return docs.size();
	}
	
	
	public int getSentenceNum() {
		Iterator<FNLPDoc> it1 = docs.iterator();
		int n=0;
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			n += doc.sentences.size();
		}
		return n;
	}

	public  FNLPDoc getDoc(int idx) {
		if(idx<docs.size())
			return docs.get(idx);
		else 
			return null;
	}
	/**
	 * 得到所有词性
	 * @return
	 */
	public TreeSet<String> getAllPOS() {
		TreeSet<String> set = new TreeSet<String>(); 
		Iterator<FNLPDoc> it1 = docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				if(!sent.hasTag())
					continue;
				for(int i=0;i<sent.size();i++){					
					set.add(sent.tags[i]);
				}
			}

		}
		return set;
		
	}
	
	/**
	 * 得到所有关系类型
	 * @return
	 */
	public TreeSet<String> getAllRelations() {
		TreeSet<String> set = new TreeSet<String>(); 
		Iterator<FNLPDoc> it1 = docs.iterator();
		while(it1.hasNext()){
			FNLPDoc doc = it1.next();
			Iterator<FNLPSent> it2 = doc.sentences.iterator();
			while(it2.hasNext()){
				FNLPSent sent = it2.next();
				if(!sent.hasRelation())
					continue;
				for(int i=0;i<sent.size();i++){					
					set.add(sent.relations[i]);
				}
			}

		}
		return set;
		
	}


	

}