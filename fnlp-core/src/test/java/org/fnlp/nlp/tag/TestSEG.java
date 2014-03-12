package org.fnlp.nlp.tag;

import java.util.ArrayList;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;

/**
 * 分词使用示例
 * @author xpqiu
 *
 */
public class TestSEG {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		CWSTagger tag = new CWSTagger("./models/seg.m");
		ArrayList<String> str = MyCollection.loadList("./testcase/seg.txt",null);
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
		
		ArrayList<String> str1 = MyCollection.loadList("data/FNLPDATA/seg/bad case.txt",null);
		for(String s:str1){		
			s = s.trim();
			String s1 = s.replaceAll(" ", "");
			String t = tag.tag(s1);
			System.out.println("处理： "+t);
			if(!t.equals(s))
				System.err.println("正确： "+s);	
		}
		
	}
	

}
