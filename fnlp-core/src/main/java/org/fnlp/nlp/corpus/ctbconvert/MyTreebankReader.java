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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
/**
 * 读入句法分析树
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyTreebankReader {
	private static boolean isLeaf;
	private static int id;

	/**
	 * 判断是不是缺省结构
	 * @param tree
	 * @return
	 */
	private static boolean isDefault(Tree<Node> tree){
		Tree<Node> flag = tree;
		while(!flag.isLeaf()&&flag.children.size()==1)
			flag = flag.getFirstChild();
		if(flag.isLeaf()&&flag.label.getTag().equals("-NONE-"))
			return true;
		else
			return false;
	}
	/**
	 * 删除缺省节点
	 * @param tree
	 */
	private static void delDefault(Tree<Node> tree){
		if(!tree.isLeaf()){
			for(int i=0;i<tree.children.size();i++){
				if(isDefault(tree.getChild(i))){
					tree.removeChild(i); 
				}
			}
			for(int i=0;i<tree.children.size();i++){
				delDefault(tree.getChild(i));
			}
		}	
	}
	
	public static InstanceSet readTrees(String path, String suffix,
			Charset charset) throws IOException {
		InstanceSet dataSet = new InstanceSet();
		List<File> fileList = findFiles(path, -1, -1, suffix);
		for (File file : fileList) {
			System.out.println(file.toString());
//			if(file.toString().contains("0030")){
//				System.out.println(file.toString());
//			}
			TreeReaderIterator ite = new TreeReaderIterator(file, charset);
			while (ite.hasNext())
				dataSet.add(new Instance(ite.next()));
		}
		return dataSet;
	}
	
	public static InstanceSet readNewTrees(String path, 
			String suffix, Charset charset)throws IOException {
		List<File> fileList = findFiles(path, -1, -1, suffix);
		InstanceSet dataSet = new InstanceSet();
		for (File file : fileList) {
			System.out.println(file.toString());
//			if(file.toString().contains("0030")){
//				System.out.println(file.toString());
//			}
			TreeReaderIterator ite = new TreeReaderIterator(file, charset);
			while (ite.hasNext())	{
				System.out.print(".");
				Tree<Node> inst = ite.next();
				List<Tree<Node>> newTreeList = getNewTree(inst);
				for(int i=0;i<newTreeList.size();i++){
					dataSet.add(new Instance(newTreeList.get(i)));
				}
			}
			System.out.print("\n");
		}
		return dataSet;
	}
	/**
	 * 以逗号或分号为标志将树分成若干新的句法树
	 * @param inst
	 * @return
	 */
	private static List<Tree<Node>> getNewTree(Tree<Node> inst){	
		delDefault(inst);
		List<Tree<Node>> newTreeList = new ArrayList<Tree<Node>>();
		List<Tree<Node>> children = new ArrayList<Tree<Node>>();
		if(!inst.isLeaf()&&!inst.getFirstChild().isLeaf()){
			boolean hasPu = false;
			Tree<Node> newInst=null;
			for(int i=0;i<inst.getFirstChild().children.size();i++){
				children.add(inst.getFirstChild().getChild(i));				
				String tag = inst.getFirstChild().getLabel().getTag();
				String flag0 = inst.getFirstChild().getChild(i).getLabel().getTag();
				String data0 = inst.getFirstChild().getChild(i).getLabel().getData();			
				if(flag0.equals("PU")&&
						(data0.equals("，")
						||data0.equals("；")
						||data0.equals("、")
						||data0.equals("。")
						||data0.equals("！")
						||data0.equals("？"))){
					hasPu = true;
					if(children.size()!=0)
						newInst = new Tree<Node>(new Node(tag,"",0),children);
					else
						newInst = new Tree<Node>(new Node(tag,"",0));
					
					newTreeList.add(newInst);
					children = new ArrayList<Tree<Node>>();
				}				
			}
			if(!hasPu)
				newTreeList.add(inst);
		}
		return newTreeList;
	}

	public static InstanceSet readTrees(String path, int from, int to,
			String suffix, Charset charset) throws IOException {
		List<File> fileList = findFiles(path, from, to, suffix);
		InstanceSet dataSet = new InstanceSet();
		for (File file : fileList) {
			TreeReaderIterator ite = new TreeReaderIterator(file, charset);
			while (ite.hasNext())	{
				dataSet.add(new Instance(ite.next()));
			}
		}
		return dataSet;
	}

	private static List<File> findFiles(String path, int from, int to,
			String suffix) {
		File fp = new File(path);
		List<File> fileList = new ArrayList<File>();
		appendFiles(fileList, fp, from, to, suffix);
		return fileList;
	}

	private static void appendFiles(List<File> fileList, File fp, int from,
			int to, String suffix) {
		if (fp.isDirectory()) {
			File[] nfiles = fp.listFiles();
			for (int l = 0; l < nfiles.length; l++)
				appendFiles(fileList, nfiles[l], from, to, suffix);
		} else if (fp.isFile()) {
			if (checkFileName(fp.getName(), from, to, suffix))
				fileList.add(fp);
		}
	}

	private static boolean checkFileName(String name, int from, int to,
			String suffix) {
		boolean b = true;
		int fid = parseName(name);
		if (suffix!=null&& !name.endsWith('.' + suffix))
			b = false;
		if (from == -1 && to == -1)
			b = true;
		else if ((from!=-1&&fid <from) || (to!=-1&& fid > to))
			b = false;
		return b;
	}

	

	private static int parseName(String name) {
		int fid = 0;
		for (int i = 0; i < name.length(); i++) {
			if (Character.isDigit(name.charAt(i)))
				fid = fid * 10 + Character.digit(name.charAt(i), 10);
		}
		return fid;
	}

	
	private static class TreeReaderIterator implements Iterator<Tree<Node>> {

		Tree<Node> nextTree = null;
		PushbackReader in;

		public TreeReaderIterator(File file, Charset charset)
				throws IOException {
			//add by xpqiu
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), charset));
			StringBuilder sb = new StringBuilder();
			

			String line = null;
			while ((line = in.readLine()) != null) {
//				line = line.trim();	
				if(line.length()==0)
					continue;
				if(line.startsWith("<")&&line.endsWith(">"))
					continue;
				sb.append(line);
				sb.append("\n");
			}
			in.close();
			
			this.in = new PushbackReader(new StringReader(sb.toString()));
			//end add 
			
//			this.in = new PushbackReader(new InputStreamReader(
//					new FileInputStream(file), charset));
			nextTree = nextTree();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return (nextTree != null);
		}

		public Tree<Node> next() {
			Tree<Node> tree = nextTree;
			nextTree = nextTree();
			return tree;
		}

		private Tree<Node> nextTree() {
			Tree<Node> tree = null;
			id = 0;
			try {
				skipWhiteSpace();
				if (isLeftBracket())
					tree = readTree();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return tree;
		}

		private Tree<Node> readTree() throws IOException {
			Tree<Node> tree = null;		
			int c = in.read();
			if(c!='(')
				throw new IOException();
			tree = new Tree<Node>(readLabel());
			if(!isLeaf)
				tree.setChildren(readChildren());
			c =in.read();
			if(c!=')')
				throw new IOException();
			skipWhiteSpace();
			return tree;
		}

		private List<Tree<Node>> readChildren() throws IOException {
			List<Tree<Node>> children = new ArrayList<Tree<Node>>();
			while (!isRightBracket()) {
				Tree<Node> child = readTree();
				children.add(child);
				skipWhiteSpace();
			}
			return children;
		}

		private Node readLabel() throws IOException {
			isLeaf = false;
			StringBuilder buf = new StringBuilder();
			StringBuilder bufWord = new StringBuilder();
			int ch = in.read();
//			System.out.print((char) ch);
			Node label = new Node();
			if (ch != '(') {
				while (ch != ' ') {
					buf.append((char) ch);
					if (!isRightBracket())
						ch = in.read();
					else
						break;
				}
				
				if (buf.length() != 0)
					label.ctbClass = strip(buf);
				else
					buf.append("ROOT");
				label.setTag(buf.toString());
				label.setData("");
				if(ch==' '){
					skipWhiteSpace();
					if(!isLeftBracket()){
						isLeaf = true;						
						while(ch !=')'){
							if (!isRightBracket())
								ch = in.read();
							else 
								break;
							bufWord.append((char)ch);
						}
						
//						System.out.print(bufWord.toString());
						
						label.setData(bufWord.toString());
						if(!label.getTag().equals("-NONE-")){
							id++;
							label.setId(id);
						}
						else
							label.setId(-1);
					}
				}
			} else {
				in.unread(ch);
			}
			skipWhiteSpace();		
			label.setCore(new Node());
			return label;
		}
		
		private String strip(StringBuilder buf) {
			String depClass = "";
			int idx = buf.indexOf("=");
			int idx2 = buf.indexOf("-");
			if (idx2 > 0)	{
				if (idx == -1)
					idx = idx2;
				else
					idx = (idx < idx2 ? idx : idx2);
			}
			if (idx != -1){
				depClass = buf.substring(idx+1, buf.length());
				buf.delete(idx, buf.length());
			}
			return depClass;
		}

		private boolean isLeftBracket() throws IOException {
			boolean ret = false;
			int ch = in.read();
			if(ch == -1)
				return false;
			in.unread(ch);
			if (ch == '(')
				ret = true;
			return ret;
		}

		private boolean isRightBracket() throws IOException {
			boolean ret = false;
			int ch = in.read();
			if(ch == -1)
				return true;
			in.unread(ch);
			if (ch == ')')
				ret = true;
			
			return ret;
		}

		private void skipWhiteSpace() throws IOException {
			int ch;
			
			do {
				ch = in.read();
			} while (Character.isWhitespace(ch));
			in.unread(ch);
		}

	}
	public static void main(String[] args) throws IOException{
		InstanceSet ins = MyTreebankReader.readNewTrees("./data/ctb/data","mz",Charset.forName("UTF8"));
		System.out.print(ins.size());
		for(int i=0;i<ins.size();i++){
			Tree<Node> tr = (Tree<Node>)(ins.get(i).getData());
//			System.out.println(	tr.getLabel().getTag());
			Iterator it = tr.iterator();
			while(it.hasNext()){
				Tree<Node> te = (Tree<Node>) it.next();
	//			if(te.isLeaf())
				System.out.println(te.getLabel().getId()+" "+te.getLabel().getTag()+" "+te.getLabel().getData());
			}
		}
	}
}