package org.fnlp.nlp.cn.rl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.POSTagger;

import gnu.trove.set.hash.THashSet;

public class Seg {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		CWSTagger seg = new CWSTagger("./models/seg.m");	
		POSTagger pos = new POSTagger(seg, "./models/pos.m");

		RLSeg rlseg = new RLSeg(seg,"./tmpdata/FNLPDATA/all.dict");
//		tag.setDictionary(rlseg.tempdict);
		String file = "./tmpdata/20120927-微博分词-5000-test-utf-8.txt";
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./tmp/complex.txt"), "UTF-8"));
		BufferedWriter bcqa = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./tmp/seged.txt"), "UTF-8"));
		String line = null;	
		int i=0;
		while ((line = bfr.readLine()) != null) {
			System.out.println(i++);

			if(line.length()==0)
				continue;
			String[] toks = seg.tag2Array(line);
			
			for(int j=0;j<toks.length;j++){
				bcqa.write(toks[j]);
				if(j<toks.length-1)
				bcqa.write(" ");
			}
			bcqa.write("\n");
			bcqa.write("\n");
			int oov = rlseg.update(toks);
//			int oov = rlseg.calcOOV(toks,2);
			if(oov>3){
//			if(oov>2 || sent.length()>4&&toks.length<sent.length()/2.5){
				for(int j=0;j<toks.length;j++){
					bout.write(toks[j]);
					bout.write(" ");
				}
				bout.write("\n");
				bout.flush();
			}
//
//			tag.setDictionary(rlseg.tempdict);
		}
		bcqa.close();
		bout.close();
		bfr.close();
		System.out.println("Done!");
	}

}
