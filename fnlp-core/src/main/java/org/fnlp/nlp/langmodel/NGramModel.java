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

package org.fnlp.nlp.langmodel;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.TIntHashSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.lang.Math;

import org.fnlp.ml.types.alphabet.HashFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.NGram;
/**
 * 元语言模型
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2014年11月3日 下午5:42:42
 */
public class NGramModel {
	/**
	 * 
	 */
	private static final String base = "BBB";

	/**
	 * n
	 */
	protected int n;

	/**
	 * key为前ngram，value为频率
	 */
	TFloatArrayList prob = new TFloatArrayList();
	/**
	 * key为前n-1 gram，value为频率
	 */
	TIntArrayList n1gramCount = new TIntArrayList();

	/**
	 * ngram 到 n-1gram 的对应关系
	 */
	TIntIntHashMap index = new TIntIntHashMap();
	/**
	 * 1gram词表
	 */
	LabelAlphabet wordDict = new LabelAlphabet();
	/**
	 * ngram词表
	 */
	LabelAlphabet ngramDict = new LabelAlphabet();
	/**
	 * n-1gram词表
	 */
	LabelAlphabet n1gramDict = new LabelAlphabet();



	public NGramModel(int n) {
		this.n = n;
	}

	


	/**
	 * 训练数据，得到每个词对的出现次数
	 * @param inputFile 训练数据文件
	 * @throws Exception 
	 */
	public void build(String... inputFile) throws Exception {
		System.out.println("read file ..."); 
				
		
		for(String file:inputFile){
			LinkedList<Integer> n1gramList = new LinkedList<Integer>();
			for(int i=0;i<n;i++){
				n1gramList.add(-1);
			}
			Scanner scanner = new Scanner(new FileInputStream(file), "utf-8");
			while(scanner.hasNext()) { 
				String s = scanner.next();
				int idx = wordDict.lookupIndex(s);
				n1gramList.add(idx);
				n1gramList.remove();
				assert n1gramList.size()==n;
				
				String[] grams = getNgram(n1gramList);
				String n1gram = grams[0];
				String ngram = grams[1];
				
				int n1gramidx = n1gramDict.lookupIndex(n1gram);
				if(n1gramidx==n1gramCount.size())
					n1gramCount.add(1);
				else if(n1gramidx>n1gramCount.size()){
					throw new Exception();
				}
				else{
					int count = n1gramCount.get(n1gramidx);
					n1gramCount.set(n1gramidx, count+1);
				}

				
				int ngramidx = ngramDict.lookupIndex(ngram);
				
				if(ngramidx==prob.size())
					prob.add(1);
				else if(ngramidx>prob.size()){
					throw new Exception();
				}
				else{
					float count = prob.get(ngramidx);
					prob.set(ngramidx, count+1);
				}
				
				
				
				if(index.contains(ngramidx))
					assert(index.get(ngramidx) == n1gramidx);
				else
					index.put(ngramidx, n1gramidx);

			}
			scanner.close();
		}
		
		ngramDict.setStopIncrement(true);
		n1gramDict.setStopIncrement(true);
		wordDict.setStopIncrement(true);
		System.out.println("词表大小" + wordDict.size());

	}
	
	
	public String[] getNgram(List<Integer> n1gramList){
		StringBuilder buf = new StringBuilder();
		
		for(int k = 0; k < n-1; k++)   {
			buf.append(n1gramList.get(k));
			buf.append(' ');
		}
		//n-1 gram
		String n1gram = buf.toString();		
		
		//ngram
		buf.append(n1gramList.get(n-1));		
		String ngram = buf.toString();
		return new String[]{n1gram,ngram};		
	}

	/**
	 * 保存模型数据到saveModelFile
	 * @param saveModelFile
	 * @throws IOException
	 */
	public void save(String saveModelFile) throws IOException {
		System.out.println("save ...");
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(saveModelFile))));
		out.writeObject(new Integer(n));
		out.close();
		System.out.println("OK");
	}

	/**
	 * 加载模型数据文件
	 * @param modelFile
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load(String modelFile) throws IOException, ClassNotFoundException {
		System.out.println("加载N-Gram模型:" + modelFile);
		System.out.println("load ...");
		System.out.println(modelFile);
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(modelFile))));
		n = (Integer) in.readObject(); 
		System.out.println("ngram " + n);
		in.close();
		System.out.println("load end");
	}



	/**
	 * 计算str的概率  P(A1A2.。。AN)
	 * @param str
	 * @return
	 */
	public double getProbability(String str) {

		double score = 0;	
		String[] toks = str.split("\\s+");
		if(toks.length<n-1)
			return 0.0;
		LinkedList<Integer> n1gramList = new LinkedList<Integer>();
		int i=0;
		n1gramList.add(-1);
		for(;i<n-1;i++){
			int idx = wordDict.lookupIndex(toks[i]);
			n1gramList.add(idx);
		}
		for(;i<toks.length;i++){
			int idx = wordDict.lookupIndex(toks[i]);
			n1gramList.add(idx);
			n1gramList.remove();
			assert n1gramList.size()==n;
			
			String[] grams = getNgram(n1gramList);
			String n1gram = grams[0];
			String ngram = grams[1];
			
			double s=0;
			int n1gramidx = n1gramDict.lookupIndex(n1gram);
			if(n1gramidx !=-1){						
				int ngramidx = ngramDict.lookupIndex(ngram);
				if(ngramidx ==-1)
					s = 1.0/(n1gramCount.get(n1gramidx)+wordDict.size());
				else
					s = prob.get(ngramidx);
			}
			score+=Math.log(s);			
		}
		return score;
	}


	/**
	 * str的概率/length
	 * @param str
	 * @return
	 */
	public double normalise(String str) {
		double d = getProbability(str);
		d = d / str.length(); //词有长有短,乘以一个系数
		return d;
	}





}