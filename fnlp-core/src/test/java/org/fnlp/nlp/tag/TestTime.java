package org.fnlp.nlp.tag;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.fnlp.nlp.cn.ner.TimeNormalizer;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.util.MyCollection;

public class TestTime {

	private static int m;
	static JointParser parser;
	static CWSTagger stag;
	static POSTagger ttag;
	static TimeNormalizer normalizer;
	private static String file;
	private static ArrayList<String> strs;


	public static void main(String[] args) throws Exception{
		file = "../FudanNLP/example-data/data-tag.txt";
		strs = MyCollection.loadList(file,null);
		for(m=1;m<=4;m++){
			System.out.println(m);
			MemoryStatic.start();
			switch(m){
			case 1: 
				stag = new CWSTagger("./models/seg.m");
				System.out.println("分词");
				break;
			case 2: 
				ttag = new POSTagger("models/seg.m","models/pos.m");
				System.out.println("词性标注");
				break;
			case 3: 
				ttag = new POSTagger("models/seg.m","models/pos.m"); 
				parser = new JointParser("models/dep.m");
				System.out.println("句法分析");
				break;
			case 4:
				normalizer = new TimeNormalizer("./models/TimeExp.m");
				System.out.println("时间");
				break;
			}
			Runtime.getRuntime().gc();
			long diff = MemoryStatic.end();

			System.out.println("内存占用："+ diff/1024.0/1024 +"M");
			MemoryStatic.start();
			test();
			long diff1 = MemoryStatic.end();
			System.out.println("分析过程内存占用："+ diff1/1024.0/1024 +"M");
			stag = null;
			ttag=null;
			parser=null;
			normalizer=null;
			Runtime.getRuntime().gc();
		}
	}
	static DecimalFormat  df = new DecimalFormat("0");
	/**
	 * 批量测试
	 * @param tag
	 * @param file 文件名
	 * @throws Exception
	 */
	public static void test() throws Exception{
		
		long beginTime = System.currentTimeMillis();
		int n=0;
		int count = 0;
		while(n++<100){
			for(int i=0;i<strs.size();i++){
				String s = strs.get(i);
				switch(m){
				case 1: 
					stag.tag(s);
					break;
				case 2: 
					ttag.tag(s);
					break;
				case 3: 
					String[][] ss = ttag.tag2Array(s);
					String tree = parser.parse2String(ss[0],ss[1],true);
					break;
				case 4:
					normalizer.parse(s);
				}

				count+=s.length();
			}
		}
		float totalTime = (System.currentTimeMillis() - beginTime)/ 1000.0f;
		System.out.println("总时间(秒):" + totalTime);
		System.out.println("速度(千字/秒):" + df.format(count/totalTime/1000)+"K");

	}
}
