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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 依存句法树
 * @author xpqiu
 *
 */
public class DependencyTree implements Serializable {

	private static final long serialVersionUID = -4766669720074872942L;

	public String word;
	public String pos;

	/**
	 * 原句中的顺序id
	 */
	public int id;
	private int size=1;

	/**
	 * 依赖关系类型
	 */
	public String relation;
	public List<DependencyTree> leftChilds;
	public List<DependencyTree> rightChilds;
	/**
	 * 父节点
	 */
	private DependencyTree parent = null;

	public DependencyTree(int id)   {
		this(id, null, null,null);
	}

	public DependencyTree(int id, String word)  {
		this(id, word, null,null);
	}
	//change
	public DependencyTree(int id,String word,String pos )   {
		this(id, word, pos,null);
	}
	//add
	public DependencyTree(int id, String word, String pos,String depClass)  {
		this.word = word;
		this.pos = pos;
		this.id = id;
		this.relation = depClass;
		leftChilds = new ArrayList<DependencyTree>();
		rightChilds = new ArrayList<DependencyTree>();
	}
	public String getDepClass(){
		return this.relation;
	}
	public void setDepClass(String depClass){
		this.relation = depClass;
	}
	public void addLeftChild(DependencyTree ch) {
		int id = ch.id;
		int i=0;
		for(;i<leftChilds.size();i++){
			int cid = leftChilds.get(i).id;
			if(cid>id)
				break;
		}
		leftChilds.add(i, ch);
		ch.setParent(this);
		updatesize(ch.size);
	}


	public void addRightChild(DependencyTree ch)    {
		rightChilds.add(ch);
		ch.setParent(this);
		updatesize(ch.size);
	}
	/**
	 * 更新树大小
	 * @param size
	 */
	private void updatesize(int size) {
		this.size+=size;
		if(parent!=null){
			parent.updatesize(size);
		}


	}

	/**
	 * 设置父节点
	 * @param tree
	 */
	private void setParent(DependencyTree tree) {
		parent = tree;
	}

	public DependencyTree getParent(){
		return parent;
	}
	public String toString()    {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < leftChilds.size(); i++) {
			sb.append(leftChilds.get(i).toString());
		}
		sb.append(id).append(" ");
		sb.append(word);
		sb.append(" ");
		sb.append(pos);
		sb.append(" ");
		if(parent!=null)
			sb.append(parent.id);
		else
			sb.append(-1);
		sb.append(" ");
		if(relation!=null)
			sb.append(relation);
		else
			sb.append("核心词");
		sb.append("\n");
		for (int i = 0; i < rightChilds.size(); i++) {
			sb.append(rightChilds.get(i).toString());
		}
		return sb.toString();
	}

	public String toBracketString()    {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(id);
		//      if (word != null)   {
		//          sb.append("[");
		//          sb.append(word);
		//          sb.append("]");
		//      }
		sb.append(" ");
		for (int i = 0; i < leftChilds.size(); i++) {
			sb.append(leftChilds.get(i));
		}
		sb.append("-");
		for (int i = 0; i < rightChilds.size(); i++) {
			sb.append(rightChilds.get(i));
		}
		sb.append("]");
		return sb.toString();
	}

	public int[] toHeadsArray() {
		int[] heads = new int[size];
		toArrays(this,heads);
		return heads;
	}

	public static void toArrays(DependencyTree dt, int[] heads) {
		for(int i = 0; i < dt.leftChilds.size(); i++)   {
			DependencyTree ch = dt.leftChilds.get(i);
			heads[ch.id] = dt.id;
			toArrays(ch, heads);
		}
		for(int i = 0; i < dt.rightChilds.size(); i++)  {
			DependencyTree ch = dt.rightChilds.get(i);
			heads[ch.id] = dt.id;
			toArrays(ch, heads);
		}
	}

	public int size() {
		return size;
	}

	public List<DependencyTree> getAllChild(){
		List<DependencyTree> childs = new ArrayList<DependencyTree>();
		childs.addAll(leftChilds);
		childs.addAll(rightChilds);
		return childs;
	}

	public boolean contain(DependencyTree dt) {
		if(this.equals(dt))
			return true;
		for(DependencyTree ch: leftChilds)   {
			if(ch.contain(dt))
				return true;
		}
		for(DependencyTree ch: rightChilds)   {
			if(ch.contain(dt))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<List<String>> toList() {
		ArrayList<List<String>> lists = new ArrayList<List<String>>(size);
		for(int i=0;i<size;i++){
			lists.add(null);
		}
		toList(lists);		
		return lists;
	}

	private void toList(ArrayList<List<String>> lists) {
		ArrayList<String> e = new ArrayList<String>();
		e.add(word);
		e.add(pos);
		if(parent==null){
			e.add(String.valueOf(-1));
			e.add("Root");
		}
		else{
			e.add(String.valueOf(parent.id));
			e.add(relation);
		}
		lists.set(id, e);
		for (int i = 0; i < leftChilds.size(); i++) {
			leftChilds.get(i).toList(lists);
		}
		for (int i = 0; i < rightChilds.size(); i++) {
			rightChilds.get(i).toList(lists);
		}
		
	}
	//错误
	public String[] getWords() {
		String[] words = new String[size];
		getWords(words);
		return words;
	}
	//错误
	private void getWords(String[] words) {
		words[id] = word;
		for (int i = 0; i < leftChilds.size(); i++) {
			leftChilds.get(i).getWords(words);
		}
		for (int i = 0; i < rightChilds.size(); i++) {
			rightChilds.get(i).getWords(words);
		}
		
	}
	/**
	 * 得到依赖类型字符串
	 * @return
	 */
		
	public String getTypes()    {
		StringBuffer sb = new StringBuffer();
		String ste;
		String[] str;
		for (int i = 0; i < leftChilds.size(); i++) {
			sb.append(leftChilds.get(i).getTypes());
		}
		if(relation!=null)
			sb.append(relation);	
		else
			sb.append("核心词");
		sb.append(" ");
		for (int i = 0; i < rightChilds.size(); i++) {
			sb.append(rightChilds.get(i).getTypes());
		}
				
			return sb.toString();	
	}
	
}