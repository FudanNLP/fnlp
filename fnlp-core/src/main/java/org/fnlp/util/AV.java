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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @version 1.0
 * @since 1.0
 */
public class AV {

	HashMap<String, Set> left;
	HashMap<String, Set> right;

	HashMap<String, Integer> av;
	boolean isSpace = false;
	private int count=0;
	int maxLen = 4;

	public AV() {
		left = new HashMap<String, Set>();
		right = new HashMap<String, Set>();
		av = new HashMap<String, Integer>();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		AV fm = new AV();
//		String fileName = "D:/xpqiu/项目/自选/CLP2010/CWS/Training-Unlabelled-B.txt";
		String fileName = "D:/xpqiu/项目/自选/CLP2010/CWS/data/Training-Labelled.txt";
		fm.read(fileName);
		fileName = "D:/xpqiu/项目/自选/CLP2010/CWS/data/Training-Unlabelled-B.txt";
		fm.read(fileName);
		fileName = "D:/xpqiu/项目/自选/CLP2010/CWS/data/Test-B-Simplified.txt";
		fm.read(fileName);
		fm.calcAV();
		fm.save("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt", true);
		System.out.println("Done");

	}

	private void calcAV() {
		System.out.println("count: "+left.size());
		Iterator<String> it = left.keySet().iterator();		
		while(it.hasNext()){
			String key = it.next();
			Double l = Math.log(left.get(key).size());
			Double r = Math.log(right.get(key).size());
			av.put(key, (int)Math.min(l, r));
		}
		System.out.println("av count: "+av.size());
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
				e.printStackTrace();

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
			Map.Entry<String,Integer>[] entries= getSortedHashtableByValue(av); 
			for(int i=0;i<entries.length;i++){
				if(entries[i].getValue()==0)
					continue;
				bout.write(entries[i].getKey());
				if (bcount) {
					bout.write("\t");
					bout.write(entries[i].getValue().toString());
				}
				bout.write("\n");
			}
			bout.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param sent
	 * @param b
	 * @return
	 */
	private void calc(String str) {
		for(int i=0;i<str.length();i++){
			for(int j=1;j<maxLen&&j+i<=str.length();j++){
				String substr = str.substring(i,i+j);
				if(!left.containsKey(substr))
					left.put(substr, new HashSet());
				if(!right.containsKey(substr))
					right.put(substr, new HashSet());
				if(i==0)
					left.get(substr).add("BIGEN");
				else
					left.get(substr).add(str.charAt(i-1));
				if(i+j==str.length())
					right.get(substr).add("END");
				else
					right.get(substr).add(str.charAt(i+j));				
			}
		}
	}

	public static Map.Entry[] getSortedHashtableByValue(Map map) {   
		Set set = map.entrySet();   
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);   
		Arrays.sort(entries, new Comparator() {   
			public int compare(Object arg0, Object arg1) {   
				Integer key1 = Integer.valueOf(((Map.Entry) arg0).getValue().toString());   
				Integer key2 = Integer.valueOf(((Map.Entry) arg1).getValue().toString());   
				return -key1.compareTo(key2);   
			}   
		});   
		return entries;   
	} 

}