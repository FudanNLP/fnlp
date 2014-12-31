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

package org.fnlp.nlp.parser.dep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.nlp.parser.dep.DependencyTree;

/**
 * FudanNLP标准数据格式
 * @since FudanNLP 1.5
 */
public class TreeCacheSent {

	public String[] owords;
	public String[][] words;
	public String[] otags;
	public String[][] tags;
	public int[] heads;
	public String[] relations;
	/**
	 * 下标起始位置，缺省为0
	 */
	private int start = 0;



	public TreeCacheSent(List<String> list) {
		parse(list,start,true);
	}

	public TreeCacheSent() {
		// TODO Auto-generated constructor stub
	}



	/**
	 * @param list 一个句子，每行是一组标记 上海	{"NR	2	NMOD", "浦东	NR	6	NMOD"}
	 * @param pos 标记开始位置，默认是1
	 * @param b 第一列是否为序列标记
	 */
	public void parse(List<String> list,int pos,boolean b) {

		int len = list.size();
		owords = new String[len];
		otags = new String[len];
		words = new String[len][];
		tags = new String[len][];
		heads = new int[len];
		relations = new String[len];
		int start=0;
		if(b){
			start = 1;	
		}
		for(int j=0;j<len;j++){

			String[] toks = list.get(j).split("[\\t\\s]+");
			if(b){
				assert (j+pos) == Integer.parseInt(toks[0]);
			}
			owords[j] = toks[start];
			if(owords[j].equals("*"))
				words[j] = null;
			else{
				words[j] = owords[j].split("\\|");
				Arrays.sort(words[j]);
			}
			if(toks.length>start+1){
				otags[j] = toks[start+1];
				if(otags[j].equals("*"))
					tags[j] = null;
				else{
					tags[j] = otags[j].split("\\|");
					Arrays.sort(tags[j]);
				}
			}
			if(toks.length>start+2)
				heads[j] = Integer.parseInt(toks[start+2])-pos;
			if(toks.length>start+3)
				relations[j] = toks[start+3];
			if(toks.length>start+4)
				System.err.println("格式列表太多！");
		}


	}



	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(int j=0;j<words.length;j++){
			if(words[j]!=null){
				sb.append(j);
				sb.append("\t");
				sb.append(owords[j]);
				if(tags!=null){
					sb.append("\t");
					sb.append(otags[j]);
				}
				if(heads!=null){
					sb.append("\t");
					sb.append(heads[j]);
				}
				if(relations!=null){
					sb.append("\t");
					sb.append(relations[j]);
				}
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public int size() {		
		return words.length;
	}


	public boolean hasTag() {
		if(tags[0]!=null)
			return true;
		else
			return false;
	}

	public DependencyTree toTree() {
		ArrayList<DependencyTree> nodes = new ArrayList<DependencyTree>();
		DependencyTree root = null;
		for(int j=0;j<words.length;j++){
			DependencyTree node = new DependencyTree(j, owords[j],otags[j], relations[j]);
			nodes.add(node);
		}
		for(int j=0;j<words.length;j++){
			int head = heads[j];
			if(head==-1)
				root = nodes.get(j);
			else{
				if(head>j)
					nodes.get(head).addLeftChild(nodes.get(j));
				else
					nodes.get(head).addRightChild(nodes.get(j));
			}
		}
		return root;
	}
}