package org.fnlp.train.tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.fnlp.nlp.corpus.Tags;
import org.fnlp.nlp.pipe.seq.String2Sequence;


public class ProcessCorpus {

	private static boolean labeled=false;

	static String delimer = "[\\s"+String.valueOf((char)12288)+"]+"; //全角空格
	public static void main(String[] args) throws Exception {
		
		// CLP Gold等没有标记的分词语料等处理为带有BMES标记的语料
		String input1 ="D:/corpus/segmentation/raw/";
		String output1 = "D:/corpus/segmentation/processed/";
		
		
		File f = new File(input1);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				processLabeledData(files[i].toString(),output1+files[i].getName());
			}
		}else{
			processLabeledData(input1,output1);
		}
		
		System.out.println("Done");
	}

	public static void processUnLabeledData(String input,String output) throws Exception{
		FileInputStream is = new FileInputStream(input);
//		is.skip(3); //skip BOM
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, "utf8"));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		while(true) {
			String sent = r.readLine();
			if(sent==null) break;
			String s = Tags.genSegSequence(sent, delimer, 4);
			w.write(s);
		}
		w.close();
		r.close();
	}

	public static void processLabeledData(String input,String output) throws Exception{
		FileInputStream is = new FileInputStream(input);
		is.skip(3); //skip BOM
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, "utf8"));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
		while(true) {
			String sent = r.readLine();
			if(null == sent) break;
			String s = Tags.genSegSequence(sent, delimer, 4);
			w.write(s);
		}
		r.close();
		w.close();
	}

}
