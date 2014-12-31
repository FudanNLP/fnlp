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

package org.fnlp.ml.classifier.hier;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fnlp.ml.types.alphabet.LabelAlphabet;
/**
 * DAG结构
 * 用 节点+边表示
 * @author xpqiu
 * @version 1.0
 * Tree
 * package edu.fudan.ml.types
 */
public class Tree implements Serializable {

	private static final long serialVersionUID = 5846146204699950799L;
	public int size=0;
	private int depth=0;
	int[][] treepath;

	List<Integer> nodes = new ArrayList<Integer>();	
	TIntSet leafs = new TIntHashSet();
	/**
	 * 父节点和对应的子节点数组
	 */
	HashMap<Integer,Set<Integer>> edges = new HashMap<Integer,Set<Integer>>();
	/**
	 * 子节点-&gt;父节点
	 */
	HashMap<Integer,Integer> edgesInv = new HashMap<Integer,Integer>();
	/**
	 * 层次结构
	 */
	HashMap<Integer,Set<Integer>> hier = new HashMap<Integer, Set<Integer>>();

	public Tree(LabelAlphabet la,String sep){
		Map<String, Integer> map = la.toMap();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			int value = map.get(key);
			int idx = key.indexOf(sep, 0);
			int plabel = la.lookupIndex("Root");
			while(idx!=-1){
				String label = key.substring(0,idx);
				int clabel = la.lookupIndex(label);
				addEdge(plabel, clabel);		
				plabel = clabel;
				idx = key.indexOf(sep, idx+1);
			}
			if(plabel!=value)// 不能有指向自己的边
				addEdge(plabel, value);
		}
		travel();

	}


	public Tree() {
		// TODO Auto-generated constructor stub
	}


	public Integer getNode(int i) {
		return nodes.get(i);
	}


	/**
	 * 文件每一行为一个边
	 * @param file
	 * @param alphabet
	 * @throws IOException
	 */
	public void loadFromFileWithEdge(String file, LabelAlphabet alphabet) throws IOException {
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			addEdge(alphabet.lookupIndex(tok[0]),alphabet.lookupIndex(tok[1]));
		}
		travel();

	}
	/**
	 * 文件每一行为一条路径
	 * @param file
	 * @param alphabet
	 * @throws IOException
	 */
	public void loadFromFileWithPath(String file, LabelAlphabet alphabet) throws IOException {
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			for(int i=0;i<tok.length-1;i++){
				addEdge(alphabet.lookupIndex(tok[i]),alphabet.lookupIndex(tok[i+1]));
			}
		}
		travel();
		
	}


	public void loadFromFile(String file) throws IOException{
		File f = new File(file);
		FileInputStream in = new FileInputStream(f);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while((line=reader.readLine())!=null){
			String[] tok = line.split(" ");
			addEdge(Integer.parseInt(tok[0]),Integer.parseInt(tok[1]));
		}
		travel();
		
	}

	public TIntSet getLeafs(){

		return 	leafs;

	}
	/**
	 * 得到层次、叶子节点等信息
	 */
	private void travel() {
		for(int i=0;i<nodes.size();i++){
			int l = getLevel(nodes.get(i));
			if(l>hier.size()|| hier.get(l)==null){
				Set set = new HashSet<Integer>();
				hier.put(l,set);
			}
			hier.get(l).add(i);

			if(edges.get(i)==null){
				leafs.add(i);
			}
		}
		depth = hier.size();
		CalcPath();
	}
	/**
	 * 得到节点的层数,根节点为0
	 * @param i
	 * @return
	 */
	private int getLevel(int i) {
		int n=0;
		Integer j=i;
		while((j=edgesInv.get(j))!=null){
			n++;
		}
		return n;
	}

	/**
	 * i -> j
	 * @param i 父节点
	 * @param j 子节点
	 */
	private void addEdge(int i, int j) {
		if(!nodes.contains(i)){
			nodes.add(i);
			edges.put(i, new HashSet<Integer>());
			size++;
		}else if(!edges.containsKey(i)){
			edges.put(i, new HashSet<Integer>());
		}

		if(!nodes.contains(j)){
			nodes.add(j);
			size++;
		}
		edgesInv.put(j, i);

		if(!edges.get(i).contains(j)){
			edges.get(i).add(j);
		}

	}

	public static void main(String[] args) throws IOException{
		String file = "D:/Datasets/wipo/e.txt";
		Tree t = new Tree();
		t.loadFromFile(file);
		System.out.println(t.size);
		System.out.println(t.hier.size());
		t.dist(5, 6);
	}

	/**
	 * 由上到下存储路径
	 */
	public void CalcPath() {
		treepath = new int[size][];
		for(int i=0;i<size;i++){
			TIntArrayList  list= new TIntArrayList ();
			list.add(i);
			Integer j=i;
			while((j=edgesInv.get(j))!=null){
				list.add(j);
			}
			int s = list.size();
			treepath[i] = new int[s];
			for(int k=0;k<s;k++){
				treepath[i][s-k-1] = list.get(k);
			}
		}		
	}

	public int[] getPath(int i) {
		return treepath[i];
	}

	public ArrayList<Integer> getAnc(Integer i) {
		ArrayList<Integer>  list= new ArrayList<Integer> ();
		Integer j=i;
		while((j=edgesInv.get(j))!=null){
			list.add(j);
		}
		return list;
	}

	public int[] getAncIdx(Integer i) {
		ArrayList<Integer>  list = getAnc(i);
		int[] idx = new int[list.size()];
		for(int j=0;j<list.size();j++){
			idx[j] = (int) list.get(j);
		}
		return idx;
	}
	/**
	 * 计算两个节点的最短路径距离
	 * @param i
	 * @param j
	 * @return 距离值
	 */
	public int dist(int i, int j) {
		int[] anci = treepath[i];
		int[] ancj = treepath[j];
		int k=0;
		for(;k<Math.min(ancj.length, anci.length);k++){
			if(anci[k]!=ancj[k])
				break;
		}
				
		int d = anci.length+ancj.length-2*k+1;
		return d;
	}


	public int getDepth() {
		return depth;
	}



}