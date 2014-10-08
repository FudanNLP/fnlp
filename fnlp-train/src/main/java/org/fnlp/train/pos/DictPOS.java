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

package org.fnlp.train.pos;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;
/**
 * 读取代词性的字典
 * @author Administrator
 *
 */
public class DictPOS {
	
	/**
	 * 词以及相应的词性集合
	 */
	public TreeMap<String,TreeSet<String>> dict = new TreeMap<String,TreeSet<String>>();
	/**
	 * 所有词性集合
	 */
	private TreeSet<String> posSet = new TreeSet<String>();

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DictPOS dp = new DictPOS();
		String path = "../data/FNLPDATA/词性字典";
		String out = "../data/FNLPDATA/dict.pos";
		dp.loadPath(path,".txt");
		dp.save(out);
		System.out.println("Done!");
	}
	
	/**
	 * 读入词典
	 * @param path
	 * @param suffix 
	 * @param out
	 * @throws IOException
	 */
	public void loadPath(String path, String suffix)
			throws IOException {
		List<File> files = MyFiles.getAllFiles(path, suffix);
		for(File f:files){
			load(f.toString());
		}
		
	}

	
	int maxLen = 5;
	private String filter = "市区县";


	public void load(String in) throws IOException{

		int begin = in.lastIndexOf("\\");
		int idx = in.indexOf('-',begin);
		if(idx==-1)
			idx = in.indexOf('.',begin);
		if(idx==-1)
			idx = in.length();
		String pos = in.substring(begin+1,idx);
		THashSet<String> set = MyCollection.loadTSet(in);

		TObjectHashIterator<String> it = set.iterator();
		while(it.hasNext()){
			String s = it.next();
			s = s.replaceAll("(\\s|　|　|\\t)+", "");
			if(s.length()==0)
				continue;
			add(pos, s);	
			String ss = filter(pos,s);
			if(ss!=null){
				add(pos,ss);
			}

		}
	}

	private String filter(String pos, String s) {
		if(pos.equals("地名"))
			return null;
		if(pos.equals("叹词")&&s.length()<3){
			return s+s;
		}
		if(s.length()<3)
			return null;
		char c = s.charAt(s.length()-1);
		if(filter.indexOf(c)!=-1){
			s = s.substring(0,s.length()-1);
			return s;
		}
		return null;
	}
	
	/**
	 * 增加词+词性
	 * @param pos
	 * @param s
	 */
	private void add(String pos, String s) {
		if(s.length()>maxLen)
			return;
		if(pos.length()==0)
			return;
		posSet.add(pos);
		if(dict.containsKey(s)){
			TreeSet<String> sett = dict.get(s);
			sett.add(pos);
		}else{
			TreeSet<String> sett  = new TreeSet<String>();
			sett.add(pos);
			dict.put(s, sett);
		}
	}

	public void save(String out)throws IOException {		

		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(out), "UTF-8"));

		Iterator<Entry<String, TreeSet<String>>> it = dict.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, TreeSet<String>> entry = it.next();
			String s = entry.getKey();
			TreeSet<String> sett = entry.getValue();
			//			if(sett.size()<2)
			//				continue;
			Iterator<String> it1 = sett.iterator();
			while(it1.hasNext()){
				String pos = it1.next();
				if(pos.equals(""))
					continue;
				bout.write(s);
				bout.write("\t");
				bout.write(pos);
				bout.newLine();
				bout.newLine();
			}
		}
		bout.close();
	}

	public Set<String> getPosSet() {
		return posSet;
	}

	

}