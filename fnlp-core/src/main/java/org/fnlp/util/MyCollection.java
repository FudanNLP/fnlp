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

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.set.hash.TCharHashSet;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
/**
 * 常用集合操作
 * @author xpqiu
 *
 */
public class MyCollection {

	/**
	 * 由大到小排序
	 * @param map
	 * @return 数组下标
	 */
	public static int[] sort(TIntFloatHashMap tmap) {
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();

		TIntFloatIterator it = tmap.iterator();
		while (it.hasNext()) {
			it.advance();
			int id = it.key();
			float val = it.value();
			map.put(id, Math.abs(val));
		}
		it = null;

		List<Entry> list = sort(map);
		int[] idx = new int[list.size()];
		Iterator<Entry> it1 = list.iterator();
		int i=0;
		while (it1.hasNext()) {
			Entry entry = it1.next();
			idx[i++] = (Integer) entry.getKey();
		}
		return idx;
	}

	/**
	 * 由大到小排序
	 * @param map
	 * @return
	 */
	public static List<Map.Entry> sort(Map map) {
		LinkedList<Map.Entry> list = new LinkedList<Map.Entry>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry>() {
			@Override
			public int compare(Entry o1,Entry o2) {
				// make sure the values implement Comparable
				return -((Comparable) o1.getValue()).compareTo(o2.getValue());
			}
		});
		return list;
	}

	public static void TSet2List(THashSet<String> newset, ArrayList<String> al) {
		TObjectHashIterator<String> it = newset.iterator();
		while(it.hasNext()){
			String s = it.next();
			al.add(s);
		}

	}
	/**
	 * 输出List<Entry>到文件
	 * @param entryList
	 * @param file
	 * @param b 是否输出值域
	 */
	public static void write(List<Entry> entryList, String file, boolean b) {
		try {
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			Iterator<Entry> it = entryList.iterator();
			while (it.hasNext()) {
				Entry entry = it.next();
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
	/**
	 * 将Map写到文件
	 * @param map
	 * @throws IOException 
	 */
	public static void write(Map map,String file) throws IOException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		Iterator iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String key = entry.getKey().toString();
			String v = entry.getValue().toString();
			bout.append(key);
			bout.append("\t");
			bout.append(v);
			bout.newLine();
		}
		bout.close();
	}

	/**
	 * 每行为一个字符集合
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<TCharHashSet> loadTCharHashSetArray(String path) throws IOException{


		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			System.out.print("没找到文件："+path);
			return null;
		}
		ArrayList<TCharHashSet> setArray= new ArrayList<TCharHashSet>();
		String line = null;			
		int count=0;

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			TCharHashSet set = new TCharHashSet();
			for(int i=0;i<line.length();i++){
				char c = line.charAt(i);
				if(c!='\t')
					set.add(c);

			}
			setArray.add(set);
		}
		bfr.close();
		return setArray;
	}

	/**
	 * 每行为一个或多个元素
	 * @param path
	 * @param b true,每行为一个元素;false: 每行为多个元素
	 * @return
	 * @throws IOException
	 */
	public static THashSet<String> loadTSet(String path,boolean b) throws IOException{

		THashSet<String> dict = new THashSet<String>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			System.out.print("没找到文件："+path);
			return dict;
		}
		String line = null;		
		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			if(b)
				dict.add(line);
			else{
				String[] toks = line.split("\\s+");
				for(String tok:toks)
					dict.add(tok);
			}
		}
		bfr.close();
		return dict;
	}

	/**
	 * 每行为一个元素
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static THashSet<String> loadTSet(String path) throws IOException{
		return loadTSet(path,true);
	}
	/**
	 * 去除重复的集合
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static void cleanSet(String path) throws IOException{
		THashSet<String> set = loadTSet(path,true);
		write(set, path);
	}
	

	/**
	 * 每行为一个元素
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> loadSet(String path) throws IOException{
		return loadSet(path, true);
	}
	/**
	 * 每行为一个或多个元素
	 * @param path
	 * @param b true,每行为一个元素;false: 每行为多个元素
	 * @return
	 * @throws IOException
	 */
	public static HashSet<String> loadSet(String path,boolean b) throws IOException{
		HashSet<String> dict = new HashSet<String>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			System.out.print("没找到文件："+path);
			return dict;
		}
		String line = null;		

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			
			if(b)
				dict.add(line);
			else{
				String[] toks = line.split("\\s+");
				for(String tok:toks)
					dict.add(tok);
			}
		}
		return dict;
	}
	
	/**
	 * 每行为一个或多个元素
	 * @param path
	 * @param b true,每行为一个元素;false: 每行为多个元素
	 * @return
	 * @throws IOException
	 */
	public static Set<String> loadSet(Set<String> dict,String path,boolean b) throws IOException{

		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			System.out.print("没找到文件："+path);
			return dict;
		}
		String line = null;		

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			
			if(b)
				dict.add(line);
			else{
				String[] toks = line.split("\\s+");
				for(String tok:toks)
					dict.add(tok);
			}
		}
		return dict;
	}

	public static TObjectFloatHashMap<String> loadTStringFloatMap(String path) throws IOException {
		TObjectFloatHashMap<String> dict = new TObjectFloatHashMap<String>();
		BufferedReader 	bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"));

		String line = null;
		while ((line = bfr.readLine()) != null) {
			if (line.length() == 0)
				continue;
			int idx = line.lastIndexOf("\t");
			dict.put(line.substring(0, idx), Float.parseFloat(line.substring(idx + 1)));
		}
		bfr.close();
		return dict;
	}

	/**
	 * 将文件读入到HashMap
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String,String> loadStringStringMap(String path) throws IOException{
		return loadStringStringMap(path,false);
	}

	/**
	 * 将文件读入到HashMap
	 * @param path
	 * @param isRevert 是否颠倒顺序
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String,String> loadStringStringMap(String path,boolean isRevert) throws IOException{

		HashMap<String,String> dict = new HashMap<String,String>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			return dict;
		}
		String line = null;			
		int count=0;

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			int idx = line.lastIndexOf("\t");
			if(idx==-1)
				continue;
			if(isRevert)
				dict.put(line.substring(idx+1),line.substring(0,idx));
			else
				dict.put(line.substring(0,idx), line.substring(idx+1));		
		}
		bfr.close();
		return dict;
	}


	/**
	 * 将文件读入到HashMap
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String,Float> loadStringFloatMap(String path) throws IOException{

		HashMap<String,Float> dict = new HashMap<String,Float>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		} catch (FileNotFoundException e) {
			return dict;
		}
		String line = null;			
		int count=0;

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			int idx = line.lastIndexOf("\t");
			String key = line.substring(0,idx);
			String v = line.substring(idx+1);
			dict.put(key, Float.parseFloat(v));		
		}
		return dict;
	}

	/**
	 * 从多文件中读入Map
	 * @param sfiles
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static HashMap<String,Float> loadStringFloatMapInMultiFiles(String sfiles) throws NumberFormatException, IOException {
		HashMap<String, Float> map = new HashMap<String, Float>();

		String[] files = sfiles.split(";");
		for(String f:files){
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),"utf8"));
			String line;
			while ((line = br.readLine()) != null) {
				if(line.length()==0)
					continue;
				int idx = line.lastIndexOf("\t");
				if(idx==-1)
					continue;
				String key = line.substring(0,idx);
				float v = Float.parseFloat(line.substring(idx+1));
				if (map.containsKey(key)) {
					float tempV = map.get(key);
					map.put(key, v + tempV);
				}
				else
					map.put(key, v);
			}
		}
		return map;	
	}

	public static void write(Iterable set, String file) {
		try {
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			Iterator it = set.iterator();
			while (it.hasNext()) {
				String entry = it.next().toString();
				bout.write(entry);
				bout.write("\n");
			}
			bout.close();

		} catch (Exception e) {

		}
	}
	
	
	public static HashMap<String, HashSet<String>> loadMultiValueSetMap(String path) throws IOException {
		return loadMultiValueSetMap(new FileInputStream(path));
	}

	public static HashMap<String, HashSet<String>> loadMultiValueSetMap(InputStream is) throws IOException {
		HashMap<String, HashSet<String>> dict = new HashMap<String, HashSet<String>>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(is,"utf8"));
		} catch (Exception e) {
			return dict;
		}
		String line = null;
		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			String[] toks = line.split("\\s");
			HashSet<String> v = dict.get(toks[0]);
			if(v==null){
				v = new HashSet<String>();
			}
			for(int i=1;i<toks.length;i++){
				v.add(toks[i]);
			}
			dict.put(toks[0], v);		
		}
		return dict;
	}
	
	
	public static HashMap<String, String[]> loadMultiValueMap(String path) throws IOException {
		return loadMultiValueMap(new FileInputStream(path));
	}

	public static HashMap<String, String[]> loadMultiValueMap(InputStream is) throws IOException {
		HashMap<String, String[]> dict = new HashMap<String, String[]>();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(is,"utf8"));
		} catch (Exception e) {
			return dict;
		}
		String line = null;			
		int count=0;

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			String[] toks = line.split("\\s");
			String[] v = Arrays.copyOfRange(toks, 1, toks.length);
			dict.put(toks[0], v);		
		}
		return dict;
	}
	
	
	/**
	 * 写多值Map,Map结构为HashMap<String, HashSet<String>>
	 * @param map HashMap<String, HashSet<String>>
	 * @param file
	 * @see MyCollection#write(HashMap, String, boolean)
	 */
	public static void writeMultiValueMap(Map map,	String file) {
		writeMultiValueMap(map, file,true,"\t");
	}

	/**
	 * 写多值Map,Map结构为HashMap<String, Collection<String>>
	 * @param map HashMap<String, Collection<String>>
	 * @param file
	 */
	public static void writeMultiValueMap(Map<String, Collection<String>> map,	String file,boolean hasKey,String delim) {

		try {
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			Iterator<Entry<String, Collection<String>>> it1 = map.entrySet().iterator();
			while(it1.hasNext()){
				Entry<String, Collection<String>> entry = it1.next();
				if(hasKey){
					bout.write(entry.getKey());
					bout.write("\t");
				}
				Collection<String> val = entry.getValue();
				if(val==null){
					if(it1.hasNext())
						bout.write("\n");
					continue;
				}
				Iterator<String> it = val.iterator();
				while (it.hasNext()) {
					String en = it.next();
					bout.write(en);
					if(it.hasNext())
						bout.write(delim);
				}
				if(it1.hasNext())
					bout.write("\n");
			}
			bout.close();

		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
			
		}
	}

	/**
	 * 写多值Map,Map结构为HashMap<String, HashSet<String>>
	 * @param map HashMap<String, HashSet<String>>
	 * @param file
	 * @return 
	 * @throws IOException 
	 */
	public static HashSet<HashSet<String>> loadSetSet(String file) throws IOException {
		HashSet<HashSet<String>> dict = new HashSet<HashSet<String>> ();
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));
		} catch (Exception e) {
			return dict;
		}
		String line = null;			
		int count=0;

		while ((line = bfr.readLine()) != null) {			
			if(line.length()==0)
				continue;
			HashSet<String> set = new HashSet<String>();
			String[] toks = line.split("\\s");
			for(String t:toks){
				set.add(t);
			}
			dict.add(set);
		}
		return dict;
	}



	public static int isContain(THashSet<String> set,
			ArrayList<String> subwords) {
		int i = 0;
		for(String s: subwords){
			if(set.contains(s))
				i++;
		}
		return i;
	}


	public static int getLength(THashSet<String> set) {
		int i = 0;
		TObjectHashIterator<String> it = set.iterator();
		while(it.hasNext()){
			String s = it.next();
			if(s.length()>i)
				i=s.length();
		}
		return i;
	}
	/**
	 * 从文件读入字符串数组
	 * @param file
	 * @param delim 分隔符
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> loadList(String file,String delim) throws IOException {
		ArrayList<String> list= new ArrayList<String>();
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));

		String line = null;			

		while ((line = bfr.readLine()) != null) {			
			if(line.length()==0)
				continue;			
			if(delim!=null){
				String[] toks = line.split(delim);
				for(String t:toks){
					list.add(t);
				}
			}else{
				list.add(line);
			}
		}
		bfr.close();
		return list;
	}

	public static List<String> asList(String[] strs) {
		ArrayList<String> list= new ArrayList<String>();
		for(int i=0;i<strs.length;i++)
			list.add(strs[i]);
		return list;
	}

	public static void writeMultiValueMap1(MultiValueMap<String, String> c2e,
			String c2ePath) {
		
		
	}



}