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

package org.fnlp.ontology.graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 词典
 * @author Xipeng
 *
 */
public class WordGraph {
	
	private ArrayList<Word> words;
	
	private ArrayList<HashSet<Integer>> symIndex;

	
	private HashMap<String,Integer> index;
		
    private SparseMatrix<WordRelationEnum> edges; 
    
          
    public WordGraph() {  
    	words = new ArrayList<Word>();
    	index = new HashMap<String, Integer>();
    	symIndex = new ArrayList<HashSet<Integer>>();
    	
    	edges = new SparseMatrix<WordRelationEnum>(100);
    	
    }  
    
    public void read(String path) throws IOException{
    	
		BufferedReader bfr;
			bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		
		String line = null;		

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			String[] toks = line.split("\\s+");
			WordRelationEnum rel = WordRelationEnum.getWithName(toks[0]);
			if(toks.length<2)
				continue;
			
			addRel(rel,Arrays.copyOfRange(toks, 1, toks.length));
			
					
		}
		bfr.close();
    }
      
    public void addRel(WordRelationEnum rel, String[] toks){
    	int[] ids = new int[toks.length];
		for(int i=0;i<toks.length;i++){
			Word w  = new Word(toks[i]);
			ids[i] = add(w);
		}
		
		if(rel==WordRelationEnum.SYM){
			HashSet<Integer> set = null;
			for(int i=0;i<ids.length;i++){	
				int j = containSym(ids[i]);
				if(j!=-1){
					set = symIndex.get(j);
					break;
				}
				
			}
			if(set==null){
				set = new HashSet<Integer>();
				symIndex.add(set);
			}
			for(int i=0;i<ids.length;i++){	
				set.add(ids[i]);
			}
		}
		
		for(int i=0;i<ids.length;i++){				
			for(int j=i+1;j<ids.length;j++){
				edges.set(ids[i],ids[j], rel);
				if(rel.getDirection()==Direction.BOTH)
					edges.set(ids[j],ids[i], rel);
			}
		}
    }

	private int containSym(int i) {
		for(int j=0;j<symIndex.size();j++){
			HashSet<Integer> sett = symIndex.get(j);
			if(sett.contains(i)){
				return j;
			}
		}
		return -1;
	}
    
    private int add(Word w) {
    	Integer id = index.get(w.word);
    	if(id==null){
    		id = words.size();
    		words.add(w);
    		index.put(w.word, id);
    	}
    	return id;
	}

	   
       
    

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		WordGraph wg = new WordGraph();
		wg.read("./models/wordgraph.txt");
		boolean b = wg.isSym("去","到");
		System.out.println(b);
		b = wg.isSym("我","到");
		System.out.println(b);
		b = wg.isAntonym("唱","听");
		System.out.println(b);

	}
	
	public boolean isAntonym(String w1, String w2) {
		if(w1.equals(w2))
			return false;
		Integer id1 = index.get(w1);
		Integer id2 = index.get(w2);
		if(id1==null||id2==null)
			return false;
		WordRelationEnum rel = edges.get(id1,id2);
		if(rel==WordRelationEnum.ANTONYM)
			return true;
		else
			return false;
	}
	
	public void addAntonym(String[] s) {
		addRel(WordRelationEnum.ANTONYM,s);		
	}
	
	

	public boolean isSym(String w1, String w2) {
		if(w1.equals(w2))
			return true;
		Integer id1 = index.get(w1);
		Integer id2 = index.get(w2);
		if(id1==null||id2==null)
			return false;
		WordRelationEnum rel = edges.get(id1,id2);
		if(rel==WordRelationEnum.SYM)
			return true;
		else
			return false;
	}

	public void addSym(String[] s) {
		addRel(WordRelationEnum.SYM,s);		
	}
	
	public String toString(){
		StringBuilder sb= new StringBuilder();
		long[] idx = edges.getKeyIdx();
		for (long i:idx){
			int[] idices = edges.getIndices(i);
			String w1 = words.get(idices[0]).word;
			String w2 = words.get(idices[1]).word;
			WordRelationEnum rel = edges.get(idices[0],idices[1]);
			sb.append(rel.name());
			sb.append(": ");
			sb.append(w1);
			sb.append(" ");
			sb.append(w2);
			sb.append("\n");					
		}
		
		return sb.toString();
		
	}

	public String getSymID(String w) {
		Integer id = index.get(w);
		if(id==null)
			return w;
		int idx = containSym(id);
		if(idx!=-1){
			return "SYM"+idx;
		}
		return w;
	}

	public ArrayList<String[]> getSymID() {
		ArrayList<String[]> al = new ArrayList<String[]>();
		for(int i=0;i<symIndex.size();i++){
			al.add(new String[]{"SYM"+i});
		}
		return al;
	}

}