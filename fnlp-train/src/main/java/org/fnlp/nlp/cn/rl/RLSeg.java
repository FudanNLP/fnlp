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

package org.fnlp.nlp.cn.rl;


import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class RLSeg {

	static String dicfile;
	private CWSTagger tag;
	THashSet<String> dict;
	/**
	 * 新的临时词典
	 */
	static THashSet<String> tempdict;
	/**
	 * 非词典
	 */
	static THashSet<String> nodict;

	BufferedWriter bwNew;
	BufferedWriter bwNo;
	float prop = 0.5f;
	private String newdictfile = "../tmp/dict-new.txt";
	private String nodictfile = "../tmp/dict-no.txt";

	public RLSeg(CWSTagger tag, String string) throws IOException{
		this.tag = tag;
		dicfile = string;
		dict = MyCollection.loadTSet(dicfile);
		tempdict = MyCollection.loadTSet(newdictfile);
		nodict = MyCollection.loadTSet(nodictfile);

		bwNew = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(newdictfile ,true), "UTF-8"));
		bwNo = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(nodictfile ,true), "UTF-8"));

	}

	/**
	 * 主程序
	 * @param args 
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		CWSTagger tag = new CWSTagger("./models/seg.m");	

		RLSeg rlseg = new RLSeg(tag,"./tmpdata/FNLPDATA/all.dict");

		tag.setDictionary(tempdict);

		System.out.println("\n处理文件：");
		String s4 = tag.tagFile("../example-data/data-tag.txt");
		System.out.println(s4);
		String[] toks = s4.split("\\s+");
		int newset = rlseg.update(toks);
		rlseg.close();

		tag.setDictionary(tempdict);

		System.out.println("\n处理文件：");
		String s = tag.tagFile("../example-data/data-tag.txt");
		System.out.println(s);


	}

	void close() throws IOException {
		bwNew.close();
		bwNo.close();

	}

	int update(String[] toks) throws IOException {
		if(toks==null)
			return 0;
		THashSet<String> newdict = new THashSet<String>();
		String nowords = "";
		int count = 0;
		for(int i=0;i<toks.length;i++){//取得包含新词的最长子串
			if(Chars.isLetterOrDigitOrPunc(toks[i]))
				continue;

			if(!dict.contains(toks[i])&&!tempdict.contains(toks[i])){
				nowords += "" + toks[i];
				count++;
			}else{
				if(nowords.length()>0){
					System.out.println(nowords);
					newdict.add(nowords.trim());
					nowords = "";
				}
			}
		}


		TObjectHashIterator<String> it = newdict.iterator();
		while(it.hasNext()){
			String s = it.next();
			if(nodict.contains(s))
				continue;
			System.out.println("搜索： "+s);
			THashSet<String> sset = getNewWords(s);
			if(sset==null||sset.size()==0)
				continue;
			System.out.println(sset);
			tempdict.addAll(sset);
			if(!sset.contains(s)&&!nodict.contains(s)){
				nodict.add(s);
				bwNo.write(s);
				bwNo.write("\n");
			}

		}
		bwNew.flush();
		bwNo.flush();
		return count;
	}

	public static int getOccur(String src,String find){
		int o = 0;
		int index=-1;
		while((index=src.indexOf(find,index))>-1){
			++index;
			++o;
		}
		return o;
	}

	public static String genQuery(String src){

		StringBuilder sb = new StringBuilder(src);
		int ulen = src.length()-1;
		for(int i=0;i<src.length()-ulen+1;i++){
			sb.append("+");
			sb.append(src.substring(i, i+ulen));
		}
		return sb.toString();
	}

	public THashSet<String> getNewWords(String s) throws IOException {
		if(s.length()==0)
			return null;
		THashSet<String> newset = new THashSet<String>();
		HashMap<String,Float> map = new HashMap<String, Float>();
		String q = genQuery(s);
		String res = SearchByBaidu.search(q);
		if(res.length()==0)
			return null;

		String[] words = tag.tag2Array(res);

		for(int i=0;i<words.length;i++){
			String w = words[i];
			if(w.length()<2||dict.contains(w)||tempdict.contains(w))
				continue;
			//				if(dict.contains(words[i]))
			//					continue;
			if(map.containsKey(w))
				map.put(w, map.get(w)+1);
			else
				map.put(w, 1f);
		}
		//			Set<Entry<String, Float>> set = map.entrySet();
		//			for(Entry e:set){
		//				e.setValue((Float) e.getValue()/words.length);
		//			}
		List<Entry> list = MyCollection.sort(map);



		int num = getOccur(res, s);

		float thres = num*prop;
		thres = thres<50?50:thres;
		for(Entry e:list){
			String ss = (String) e.getKey();
			if((Float) e.getValue()>thres&&ss.length()>1&&!dict.contains(ss)&&!tempdict.contains(ss)){				
				newset.add(ss);
				bwNew.write(ss);
				bwNew.write("\n");
			}

		}

		newset.remove("快照");
		return newset;
	}

	/**
	 * 
	 * @param toks
	 * @param j
	 * @return
	 */
	public int calcOOV(String[] toks, int j) {
		int count = 0;
		for(int i=0;i<toks.length;i++){//取得包含新词的最长子串
			if(Chars.isLetterOrDigitOrPunc(toks[i]))
				continue;			
			if(toks[i].length()>j)
				continue;
			if(!dict.contains(toks[i])&&!tempdict.contains(toks[i])){				
				count++;
			}

		}
		return count;
	}
}