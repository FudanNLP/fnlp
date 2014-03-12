package org.fnlp.nlp.tag;

import java.util.ArrayList;

import org.fnlp.ml.types.Dictionary;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class TestDictSEG {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		CWSTagger tag = new CWSTagger("./models/seg.m");
		Dictionary dict=new Dictionary();
		dict.addFile("./models/dict.txt");
		tag.setDictionary(dict);
		ArrayList<String> str = MyCollection.loadList("./testcase/test case seg.txt",null);
		for(String s:str){			
			String t = tag.tag(s);
//			t = tag.tag(t);
			System.out.println(t);
		}
		tag.setEnFilter(false);
		for(String s:str){
			String t = tag.tag(s);
			System.out.println(t);
		}
		
		String t = tag.tagFile("data/FNLPDATA/seg/bad case.txt");
		System.out.println(t);
		
	}
	

}
