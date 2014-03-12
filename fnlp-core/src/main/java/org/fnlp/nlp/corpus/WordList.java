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

package org.fnlp.nlp.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author xpqiu
 * @version 1.0
 * WordList
 */
public class WordList {

	class WordTree implements Serializable{


		private static final long serialVersionUID = 523580450351093949L;
		HashSet<String> wordSet;
		int depth;
		String tag;
		ArrayList<WordTree> childs;

		public WordTree(){
			wordSet = new HashSet<String>();
			childs = new ArrayList<WordTree>();
		}

	}

	public static WordTree dicts=null; //字典，用来生成字典特征
	public String dicDir = "../Data/wordlist";
	public WordList(int depth){
		if(dicts ==null){
			loaddict(depth);
		}
	}

	public String getFeatures(String word,String tag){
		return getFeaturesFromNodes(dicts,word, tag, 1);
	}

	public String getFeaturesFromNodes(WordTree node, String word,String tag,int depth){
		if(node.depth>depth)
			return " ";
		String res = "";
		String newfeature =tag;
		newfeature = newfeature+node.tag+".";
		if(node.wordSet.size()>0)
		{

			if(node.wordSet.contains(word))
				newfeature = newfeature +"1 ";
			else
				newfeature = newfeature +"0 ";

			res = res + newfeature;

		}

		Iterator<WordTree> it = node.childs.iterator();

		while(it.hasNext()){
			WordTree subnode = it.next();
			if(subnode==null)
				continue;
			res = res + getFeaturesFromNodes(subnode,word,newfeature,depth);
		}
		return res;
	}

	/**
	 * 
	 */
	private void loaddict(int depth) {


		dicts = new WordTree();
		dicts.depth=-1;
		dicts.tag = "";
		File f = new File(dicDir);
		if(!f.exists())
			return;
		else
			loadDir(f,dicts,depth);


	}


	/**
	 * @param dicts 
	 * @param file
	 */
	private void loadDir(File f, WordTree parent,int depth) {
		WordTree current;
		if(parent.depth>=depth)
			current = parent;
		else{

			current= new WordTree();
			parent.childs.add(current);
			current.depth = parent.depth+1;
			current.tag=f.getName().replace(".dic", "");
		}
		if(f.isDirectory()){
			File[] flist = f.listFiles(new FilenameFilter(){

				public boolean accept(File dir, String name) {
					if(name.endsWith(".dic"))
						return true;
					else
						return false;
				}

			});
			for(int i=0;i<flist.length;i++)
				loadDir(flist[i],current,depth);
		}else{

			if(!f.toString().endsWith(".dic"))
				return;
			try {		
				InputStreamReader  read = new InputStreamReader 
				(new FileInputStream(f.toString()),"utf-8");
				BufferedReader bin = new BufferedReader(read);
				String w;
				while((w=bin.readLine())!=null){
					current.wordSet.add(w.trim());
				}
			}catch(Exception e){

			}
		}


	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WordList wl = new WordList(2);


	}

}