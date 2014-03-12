package org.fnlp.train.prepare;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.train.tag.CWSTrain;
import org.fnlp.util.MyFiles;
/**
 * 生成分词训练文件
 * @author xpqiu
 *
 */
public class PrepareSeg {

	/**
	 * 在FNLPDATA生成.seg文件，然后合并
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String dictfile = "./data/FNLPDATA/dict.seg";
		//合并训练文件
		String allfiles = "./data/FNLPDATA/all.seg";
		String segfile = "./data/FNLPDATA/temp.seg";
		String segfile_w = "./data/FNLPDATA/temp-w.seg";
		//清理
		(new File(dictfile)).delete();
		(new File(allfiles)).delete();
		new File(segfile).delete();
		new File(segfile_w).delete();

		FNLPCorpus corpus = new FNLPCorpus();
		//读自有数据
		corpus.readOurCorpus("./data/ourdata",".txt","UTF8");
		//读分词文件
		corpus.readCWS("./data/FNLPDATA/seg",".txt","UTF8");	
		//读FNLP数据
		corpus.read("./data/FNLPDATA/ctb7.dat", null);


		
		FNLP2BMES.w2BMES(corpus,segfile);		
		FNLP2BMES.w2BMES(corpus,segfile_w);
		

		//词典转BMES
		//搜狗词典
		DICT dict = new DICT();
		String sougou = "data/FNLPDATA/dict/SogouLabDic.dic.raw";

		dict.readSougou(sougou,2,3,"sougou");
		//互动词典
		String hudong = "data/FNLPDATA/dict/hudong.dic.all";
		dict.readSougou(hudong,2,3,"");
		//添加其他词典
		dict.readDictionary("data/FNLPDATA/dict",".dic");
		
		//添加其他词典
		dict.readDictionaryWithFrequency("data/FNLPDATA/dict",".dic.freq");
		
		


		//添加词性字典
		dict.readPOSDICT("data/FNLPDATA/词性字典", ".txt");
		dict.readPOSDICT("data/FNLPDATA/dict-sogou-input/txt", ".txt");

		dict.toBMES(dictfile,3);
		new File(dictfile).deleteOnExit();


		File allfile = new File(allfiles);
		if(allfile.exists()){
			allfile.delete();
		}
		FileCombine fc=new FileCombine(); 
		List<File> files = MyFiles.getAllFiles("./data/FNLPDATA/", ".seg");
		fc.combineFiles(files, "./data/FNLPDATA/all.seg");  

		//生成新字典		
		dictfile = "./data/FNLPDATA/all.seg";
		String dicfile = "./data/FNLPDATA/all.dict";
		DICT.BMES2DICT(dictfile,dicfile);


		System.out.println(new Date().toString());
		System.out.println("Done!");

		String param = "-iter 100 -c 0.01  ./data/template-seg ./data/FNLPDATA/all.seg ./models/seg.m";
		CWSTrain.main(param.split(" +"));


	}

}
