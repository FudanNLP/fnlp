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

package org.fnlp.nlp.corpus.ctbconvert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * 成分句法树
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Tree<T> {
	/**
	 * 该树的孩子是不是并列结构
	 */
	public boolean isCoor;
	T label;
	List<Tree<T>> children;
	/**
	 * 树的孩子是并列结构时位于孩子列表的位置
	 */
	public List<Integer>  CCLocation;

	/**
	 * 树的孩子中被和把字的位置
	 */
	public int BaeLocation;
	
	/**
	 * 树（VP）的孩子中得字的位置
	 */
	public int DeLocation;
	
	public Tree(T label) {
		this.label = label;
		this.children = Collections.emptyList();
		isCoor = false;
		CCLocation = new ArrayList<Integer>();
		BaeLocation = -1;
		DeLocation = -1;
	}

	public Tree(T label, List<Tree<T>> children) {
		// TODO Auto-generated constructor stub
		this.label = label;
		this.children = children;
		isCoor = false;
		CCLocation = new ArrayList<Integer>();
		BaeLocation = -1;
		DeLocation = -1;
	}

	public T getLabel() {
		return label;
	}

	public void setLabel(T label)	{
		this.label = label; 
	}
	
	public Tree<T> getChild(int i) {
		if (i < 0 || i > children.size())
			throw new IndexOutOfBoundsException();
		return children.get(i);
	}
	
	public Tree<T> getFirstChild()	{
		if (Collections.EMPTY_LIST == children)
			return null;
		return children.get(0);
	}
	
	public Tree<T> getLastChild()	{
		if (Collections.EMPTY_LIST == children)
			return null;
		return children.get(children.size()-1);
	}

	public void setChildren(List<Tree<T>> children) {
		this.children = null;
		if (children != null)
			this.children = children;
		else
			this.children = Collections.emptyList();
	}
	
	public void addChild(int p, Tree<T> child)	{
		if (p < 0 || p > children.size())
			throw new IndexOutOfBoundsException();
		if (Collections.EMPTY_LIST == children)	{
			children = new ArrayList<Tree<T>>(1);
			children.add(child);
		}else
			children.add(p, child);
	}
	
	public List<Tree<T>> getChildren()	{
		return children;
	}
	
	public void appendChild(Tree<T> child)	{
		if (Collections.EMPTY_LIST == children)
			children = new ArrayList<Tree<T>>(1);
		children.add(child);
	}
	
	public void removeChild(int p)	{
		if (p >= 0 && p < children.size())	{
			children.remove(p);
			if (children.isEmpty())
				children = Collections.emptyList();
		}
	}
	
	public boolean isTerminal()	{
		return (Collections.EMPTY_LIST == children);
	}
	
	public boolean isPreTerminal()	{
		return (children.size() == 1 && Collections.EMPTY_LIST == children.get(0).children);
	}
	
	public List<T> getTerminals()	{
		List<T> terminals = new ArrayList<T>();
		appendTerminals(terminals, this);
		return terminals;
	}
	
	private void appendTerminals(List<T> terminals, Tree<T> tree) {
		if (Collections.EMPTY_LIST == tree.children)
			terminals.add(tree.getLabel());
		
		for(Tree<T> child : tree.children)
			appendTerminals(terminals, child);
	}

	public List<T> getPreTerminals()	{
		List<T> preterms = new ArrayList<T>();
		appendPreTerminals(preterms, this);
		return preterms;
	}
	
	private void appendPreTerminals(List<T> preterms, Tree<T> tree) {
		if (tree.isPreTerminal())
			preterms.add(tree.getLabel());
		
		for(Tree<T> child : tree.children)
			appendPreTerminals(preterms, child);
	}
	
	public String toString()	{
		StringBuffer buf = new StringBuffer();
		toStringBuffer(buf, this);
		return buf.toString();
	}

	private void toStringBuffer(StringBuffer buf, Tree<T> tree) {
		if (!tree.isTerminal())
			buf.append('(');
		
		buf.append(tree.getLabel());
		for(Tree<T> child : tree.children)	{
			buf.append(' ');
			toStringBuffer(buf, child);
		}
		if (!tree.isTerminal())
			buf.append(')');
	}

	public Iterator iterator()	{
		return new TreeIterator();
	}
	
	private class TreeIterator implements Iterator<Tree<T>>	{
		Stack<Tree<T>> stack;

		private TreeIterator() {
			stack = new Stack<Tree<T>>();
			stack.add(Tree.this);
		}

		public boolean hasNext() {
			return (!stack.isEmpty());
		}

		public Tree<T> next() {
			Tree<T> cur = stack.pop();
			for(int i = cur.children.size()-1; i >= 0; i--)
				stack.push(cur.getChild(i));
			return cur;
		}

		public void remove() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}
		
	}

	public boolean isLeaf() {
		return isTerminal();
	}

	
}