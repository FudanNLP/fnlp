package org.fnlp.nlp.tag;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.cn.tag.NERTagger;
import org.fnlp.util.MyCollection;

/**
 * 实体名识别使用示例
 * @author xpqiu
 *
 */
public class TestNER {	


	/**
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		NERTagger tag = new NERTagger("./models/seg.m","./models/pos.m");
		ArrayList<String> str = MyCollection.loadList("./testcase/test case ner.txt",null);
		
		
		for(String s:str){
			System.out.println(s);
			HashMap<String, String> t = tag.tag(s);
			System.out.println(t);
		}
		
		
		testFile(tag,"../FudanNLP/example-data/text/1.txt");
		
	}
	public static void testFile(NERTagger tag, String file) throws Exception{
		BufferedReader bin;
		StringBuilder res = new StringBuilder();
		String str1=null;
		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			while(str!=null){
				str = ChineseTrans.toFullWidth(str);
				res.append(str);				
				res.append("\n");
				str = lbin.readLine();
			}
			lbin.close();
			str1 = res.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		long beginTime = System.currentTimeMillis();
		HashMap<String, String> map = new HashMap<String, String>(); 
		tag.tag(str1,map);
		float totalTime = (System.currentTimeMillis() - beginTime)/ 1000.0f;
		System.out.println("总时间(秒):" + totalTime);
		System.out.println("速度(字/秒):" + str1.length()/totalTime);
		System.out.println(map);
	}

}
