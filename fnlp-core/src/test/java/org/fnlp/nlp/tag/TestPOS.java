package org.fnlp.nlp.tag;

import java.util.ArrayList;

import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.util.MyCollection;

/**
 * 词性标注使用示例
 * @author xpqiu
 *
 */
public class TestPOS {
	
	static POSTagger tag;


	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		tag = new POSTagger("models/seg.m","models/pos.m");
		
		ArrayList<String> str = MyCollection.loadList("./testcase/test case pos.txt",null);
		str.add("周杰伦 生 于 台湾\n我们");
		str.add("分析和比较");
		
		for(String s:str){
			String t = tag.tag(s);
			System.out.println(t);
		}
		
		str.clear();
		str.add("周杰伦 生 于 台湾\n我们");
		
		for(String s:str){
			String t = tag.tagSeged2StringALL(s.split(" "));
			System.out.println(t);
		}
		
	}

}
