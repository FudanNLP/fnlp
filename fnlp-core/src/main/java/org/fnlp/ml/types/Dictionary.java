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

package org.fnlp.ml.types;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.util.MultiValueMap;
import org.fnlp.util.MyCollection;
import org.fnlp.util.exception.LoadModelException;

public class Dictionary {
	private int MAX_LEN = 7;
	private int MIN_LEN = 2;
	/**
	 * 词典，词和相应的词性
	 */
	private MultiValueMap<String,String> dp;

	private TreeMap<String, int[]> index = new TreeMap<String, int[]>();
	private int indexLen = 2;
	private boolean isAmbiguity = false;


	public Dictionary(){
		MAX_LEN = Integer.MIN_VALUE;
		MIN_LEN = Integer.MAX_VALUE;
		dp = new MultiValueMap<String, String>();	
	}

	/**
	 * 
	 * @param b 是否模糊处理
	 */
	public Dictionary(boolean b) {
		this();
		this.setAmbiguity(b);
	}
	/**
	 * 
	 * @param path
	 * @throws IOException
	 */
	public Dictionary(String path) throws IOException {
		this(path,false);
	}

	/**
	 * 
	 * @param path
	 * @param b 使用模糊处理
	 * @throws IOException
	 */
	public Dictionary(String path, boolean b) throws IOException {
		this();
		this.setAmbiguity(b);
		ArrayList<String[]> al = loadDict(path);		
		add(al); 
		createIndex();
	}


	/**
	 * 加入不带词性的词典
	 * @param al 词的数组
	 */
	public void addSegDict(Collection<String> al) {
		for(String s: al){
			addDict(s);
		}
		createIndex();
	}

	/**
	 * 
	 * @param word 词
	 * @param poses 词性数组
	 */
	public void add(String word, String... poses) {		
		addDict(word,poses);
		indexLen = MIN_LEN;
		createIndex();
	}

	/**
	 * 
	 * @param al 词典 ArrayList&lt;String[]&gt;
	 * 						每一个元素为一个单元String[].
	 * 						String[] 第一个元素为单词，后面为对应的词性
	 */
	public void add(ArrayList<String[]> al) {
		for(String[] pos: al) {
			addDict(pos[0], Arrays.copyOfRange(pos, 1, pos.length));
		}
		indexLen = MIN_LEN;
		createIndex();
	}
	/**
	 * 在目前词典中增加新的词典信息
	 * @param path
	 * @throws LoadModelException 
	 */
	public void addFile(String path) throws LoadModelException{
		try {
			ArrayList<String[]> al = loadDict(path);		
			add(al); 
			indexLen = MIN_LEN;
			createIndex();
		} catch (IOException e) {
			throw new LoadModelException("加载词典错误"+e.toString());
		}
	}


	/**
	 * 通过词典文件建立词典
	 * @param path
	 * @return 
	 * @throws FileNotFoundException
	 */
	private ArrayList<String[]> loadDict(String path) throws IOException {
		Scanner scanner = new Scanner(new FileInputStream(path), "utf-8");
		ArrayList<String[]> al = new ArrayList<String[]>();
		while(scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if(line.length() > 0) {
				String[] s = line.split("\\s");
				al.add(s);
			}
		}
		scanner.close();
		return al;
	}
	/**
	 * 增加词典信息
	 * @param word
	 * @param poses
	 */
	private void addDict(String word, String... poses){
		if(word.length() > MAX_LEN)
			MAX_LEN = word.length();
		if(word.length() < MIN_LEN)
			MIN_LEN = word.length();
		if(poses==null||poses.length==0){
			if(!dp.containsKey(word))
				dp.put(word, null);
			return;
		}

		for(int j = 0; j < poses.length; j++) {
			dp.put(word, poses[j]);
			
		}
	}

	/**
	 * 建立词的索引
	 */
	private void createIndex() {
		indexLen = MIN_LEN;
		TreeMap<String, TreeSet<Integer>> indexT = new TreeMap<String, TreeSet<Integer>>();
		for(String s: dp.keySet()) {
			if(s.length() < indexLen)
				continue;
			String temp = s.substring(0, indexLen);
			//System.out.println(temp);
			if(indexT.containsKey(temp) == false) {
				TreeSet<Integer> set = new TreeSet<Integer>();
				set.add(s.length());
				indexT.put(temp, set);
			} else {
				indexT.get(temp).add(s.length());
			}
		}
		for(Entry<String, TreeSet<Integer>> entry: indexT.entrySet()) {
			String key = entry.getKey();
			TreeSet<Integer> set = entry.getValue();
			int[] ia = new int[set.size()];
			int i = set.size();
			//			System.out.println(key);
			for(Integer integer: set) {
				ia[--i] = integer;

			}
			//			for(int j = 0; j < ia.length; j++) 
			//				System.out.println(ia[j]);

			index.put(key, ia);
		}
		//		System.out.println(indexT);
	}

	public int getMaxLen() {
		return MAX_LEN;
	}

	public int getMinLen() {
		return MIN_LEN;
	}

	public boolean contains(String s) {
		return dp.containsKey(s);
	}

	public int[] getIndex(String s) {
		return index.get(s);
	}

	public TreeSet<String> getPOS(String s) {
		return dp.getSet(s);
	}

	public int getDictSize() {
		return dp.size();
	}

	public int getIndexLen() {
		return indexLen;
	}

	public boolean isAmbiguity() {
		return isAmbiguity;
	}

	private void setAmbiguity(boolean isAmbiguity) {
		this.isAmbiguity = isAmbiguity;
	}

	public Set<String> getDict() {
		return dp.keySet();
	}
	public MultiValueMap<String, String> getPOSDict() {
		return dp;
	}

	public TreeMap<String, int[]> getIndex() {
		return index;
	}
	public int size(){
		return dp.size();
	}

	public void save(String path) {
		MyCollection.writeMultiValueMap(dp, path);
		
	}





}