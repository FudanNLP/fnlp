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

package org.fnlp.train.seg;

import gnu.trove.set.hash.THashSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fnlp.nlp.corpus.Tags;
import org.fnlp.train.pos.DictPOS;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

public class DICT {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DICT dic = new DICT();
		String file = "./tmpdata/FNLPDATA/all.cws";
		String dicfile = "./tmpdata/FNLPDATA/all.dict";
		BMES2DICT(file,dicfile);

		System.out.println(new Date().toString());
		System.out.println("Done!");

	}





	public static void BMES2DICT(String file, String dicfile) throws UnsupportedEncodingException,
	FileNotFoundException, IOException {


		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));
		String line = null;			
		int count=0;
		THashSet<String> dict = new THashSet<String>();
		StringBuilder sb = new StringBuilder();
		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;

			String[] toks = line.split("\\s+");
			String label = toks[1];
			String w = toks[0];
			if(w.equals(" ")){//空格特殊处理
				if(sb.length()>0){
					dict.add(sb.toString());
					sb = new StringBuilder();
				}
				continue;
			}
			sb.append(w);
			if (label.equals("E") || label.equals("S")) {
				dict.add(sb.toString());
				sb = new StringBuilder();
			}
		}
		MyCollection.write(dict,dicfile);
	}
	THashSet<String> set = new THashSet<String>();


	public void readSougou(String dict,int minLen,int maxLen,String name)
			throws IOException {

		for(int len=minLen;len<=maxLen;len++){
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(dict),"utf8"));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				String[] words =line.split("\\s+");

				if(name.equals("sougou")&&words.length<3)
					continue;
				else if(words.length<1)
					continue;

				String w = words[0];
				w = w.replaceAll("(\\s|　|　|\\t)+", "");
				if(w.contains("　"))
					System.out.println();
				if(w.length()!=len)
					continue;
				ArrayList<String> subwords = getAllSubWords(w,2,3);
				if(subwords!=null){
					int c = MyCollection.isContain(set,subwords);			
					if(c>1)
						continue;
				}
				if(set.contains(w))
					continue;

				set.add(w);
			}
			bfr.close();
		}
	}

	private static ArrayList<String> getAllSubWords(String str,int minLen,int maxLex) {
		int len = str.length();

		if(len<minLen*2)
			return null;
		ArrayList<String> subwords = new ArrayList<String>();
		for(int i=0;i<len-minLen;i++){
			for(int j=minLen;j<=maxLex;j++){
				int end = i+j;
				if(end<=len)
					subwords.add(str.substring(i,end));
			}
		}
		return subwords;
	}

	/**
	 * 读取带词频的字典
	 * @param dict
	 * @param string 
	 * @param file
	 * @throws IOException
	 */
	public void readDictionaryWithFrequency(String path, String suffix) throws IOException {		

		List<File> dicts = MyFiles.getAllFiles(path, suffix);

		for(File fdict: dicts){
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fdict),"utf8"));
			String line = null;			
			while ((line = bfr.readLine()) != null) {
				String[] toks = line.split("(\\s|　|　|\\t)+");
				line = toks[0];
				if(line.length()==0||set.contains(line))
					continue;

				set.add(line);
			}
			bfr.close();
		}
	}

	/**
	 * 读取不带词频的字典
	 * @param dict
	 * @param string 
	 * @param file
	 * @throws IOException
	 */
	public void readDictionary(String path, String suffix) throws IOException {		

		List<File> dicts = MyFiles.getAllFiles(path, suffix);

		for(File fdict: dicts){
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fdict),"utf8"));
			String line = null;			
			while ((line = bfr.readLine()) != null) {
				line = line.replaceAll("(\\s|　|　|\\t)+", "");
				if(line.length()==0||set.contains(line))
					continue;

				set.add(line);
			}
			bfr.close();
		}
	}
	/**
	 * 读词性的字典
	 * @param path
	 * @param suffix 
	 * @throws IOException
	 */
	public void readPOSDICT(String path, String suffix) throws IOException {
		DictPOS dp = new DictPOS();
		dp.loadPath(path,suffix);
		set.addAll(dp.dict.keySet());
	}
	/**
	 * @param dict
	 * @param string 
	 * @throws IOException
	 */
	public void readPOSTrain(String path, String suff) throws IOException {		
		List<File> dicts = MyFiles.getAllFiles(path,suff);

		for(File fdict: dicts){
			BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(fdict),"utf8"));
			String line = null;			
			while ((line = bfr.readLine()) != null) {
				if(line.length()==0){
					continue;
				}				
				int idx = line.indexOf("\t");
				line = line.substring(0,idx);
				set.add(line);
			}
			bfr.close();
		}
	}


	/**
	 * 将字典专为BMES标签
	 * @param file
	 * @throws IOException
	 */
	public void toBMES(String file,int duplicateNum) throws IOException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file,true), "UTF-8"));
		for(String line:set){
			if(line.length()>1){
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<duplicateNum;i++){
					sb.append(line);
					if(i<duplicateNum-1)
						sb.append(" ");
				}
				line = sb.toString();
			}
			String s = Tags.genSegSequence(line,"\\s+",4);
			bout.write(s);
		}
		bout.close();	
	}


}