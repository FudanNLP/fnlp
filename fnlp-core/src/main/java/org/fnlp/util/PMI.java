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

package org.fnlp.util;
/*
 * 文件名：WordCount.java
 * 版权：Copyright 2008-20012 复旦大学 All Rights Reserved.
 * 描述：程序总入口
 * 修改人：xpqiu
 * 修改时间：2009-1-5
 * 修改内容：新增
 *
 * 修改人：〈修改人〉
 * 修改时间：YYYY-MM-DD
 * 跟踪单号：〈跟踪单号〉
 * 修改单号：〈修改单号〉
 * 修改内容：〈修改内容〉
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * pmi(x,y) = ln(pxy/px*py)/-ln(pxy)
 * @author Administrator
 * @version 1.0
 * @since 1.0
 */
public class PMI {

	HashMap<String, Integer> unigram;
	HashMap<String, Integer> bigram;
	HashMap<String, Float> pmi;
	boolean isSpace = false;
	private int count=0;
	

	public PMI() {
		unigram = new HashMap<String, Integer>();
		bigram = new HashMap<String, Integer>();
		pmi = new HashMap<String, Float>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PMI fm = new PMI();
		String fileName = "D:/xpqiu/项目/自选/CLP2010/CWS/Training-Unlabelled-A.txt";
		fm.read(fileName);
		fm.calcPMI();
		fm.save("pmi.txt", true);
		System.out.println("Done");

	}

	private void calcPMI() {
		System.out.println("bi count: "+bigram.size());
		Iterator<String> it = bigram.keySet().iterator();		
		while(it.hasNext()){
			String key = it.next();
			float c1 = unigram.get(String.valueOf(key.charAt(0)));
			float c2 = unigram.get(String.valueOf(key.charAt(1)));
			float c3 = bigram.get(key);
			float s = (float) ((Math.log((c1*c2)/count/count)/Math.log(c3/count))-1);
			pmi.put(key, s);			
		}
		System.out.println("bi count: "+pmi.size());
	}

	/**
	 * @param fileName
	 */
	public void read(String fileName) {
		File f = new File(fileName);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				read(files[i].toString());
			}
		} else {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(fileName), "utf-8");
				BufferedReader bin = new BufferedReader(read);
				String sent;
				while ((sent = bin.readLine()) != null) {
					calc(sent);
				}
			} catch (Exception e) {

			}
		}

	}

	/**
	 * @param filename
	 * @param bcount 是否输出词频
	 */
	public void save(String filename, boolean bcount) {

		
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					fos, "UTF-8"));
			Map.Entry<String,Double>[] entries= getSortedHashtableByValue(pmi); 
			 for(int i=0;i<entries.length;i++){
				bout.write(entries[i].getKey());
				if (bcount) {
					bout.write(" ");
					bout.write(entries[i].getValue().toString());
				}
				bout.write("\n");
			}
			bout.close();

		} catch (Exception e) {

		}
	}

	/**
	 * 
	 * @param sent
	 * @param b
	 * @return
	 */
	private void calc(String str) {
		// str = str.replaceAll("[\\[\\]0-9a-zA-Z/\\.<> =]+", " ").trim();
		String[] wordarray;
		if(isSpace){
			wordarray = str.split("\\s");
		}else{
			wordarray = new String[str.length()];
			for(int i=0;i<str.length();i++){
				wordarray[i] = String.valueOf(str.charAt(i));
			}
		}
		count += wordarray.length;
		for (int i = 0; i < wordarray.length; i++) {
			String w = wordarray[i].trim();
			if (w.length() == 0)
				continue;
			if (unigram.containsKey(w)) {
				unigram.put(w, unigram.get(w) + 1);
			} else {
				unigram.put(w, 1);
			}
			if(i<wordarray.length-1){
				String s = wordarray[i]+wordarray[i+1];
				if(s.trim().length()<2)
					continue;
//				System.out.println(s);
				if (bigram.containsKey(s)) {
					bigram.put(s, bigram.get(s) + 1);
				} else {
					bigram.put(s, 1);
				}
			}
		}
	}

	public static Map.Entry[] getSortedHashtableByValue(Map map) {   
	       Set set = map.entrySet();   
	       Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);   
	       Arrays.sort(entries, new Comparator() {   
	           public int compare(Object arg0, Object arg1) {   
	        	   Double key1 = Double.valueOf(((Map.Entry) arg0).getValue().toString());   
	               Double key2 = Double.valueOf(((Map.Entry) arg1).getValue().toString());   
	               return -key1.compareTo(key2);   
	           }   
	       });   
	       return entries;   
	} 

}