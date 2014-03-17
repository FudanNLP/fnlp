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
		
		String datapath = "../data";

		String dictfile = datapath + "/FNLPDATA/dict.seg";
		//合并训练文件
		String allfiles = datapath + "/FNLPDATA/all.seg";
		String segfile = datapath + "/FNLPDATA/temp.seg";
		String segfile_w = datapath + "/FNLPDATA/temp-w.seg";
		//清理
		(new File(dictfile)).delete();
		(new File(allfiles)).delete();
		new File(segfile).delete();
		new File(segfile_w).delete();

		FNLPCorpus corpus = new FNLPCorpus();
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");
		//读分词文件
		corpus.readCWS(datapath + "/FNLPDATA/seg",".txt","UTF8");	
		//读分词+词性文件
		corpus.readPOS(datapath + "/FNLPDATA/pos",".txt","UTF8");	
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);


		
		FNLP2BMES.w2BMES(corpus,segfile);		
		FNLP2BMES.w2BMES(corpus,segfile_w);
		

		//词典转BMES
		//搜狗词典
		DICT dict = new DICT();
		String sougou = datapath + "/FNLPDATA/dict/SogouLabDic.dic.raw";

		dict.readSougou(sougou,2,3,"sougou");
		//互动词典
		String hudong = datapath + "/FNLPDATA/dict/hudong.dic.all";
		dict.readSougou(hudong,2,3,"");
		//添加其他词典
		dict.readDictionary(datapath + "/FNLPDATA/dict",".dic");
		
		//添加其他词典
		dict.readDictionaryWithFrequency(datapath + "/FNLPDATA/dict",".dic.freq");
		
		


		//添加词性字典
		dict.readPOSDICT(datapath + "/FNLPDATA/词性字典", ".txt");
		dict.readPOSDICT(datapath + "/FNLPDATA/dict-sogou-input/txt", ".txt");

		dict.toBMES(dictfile,3);
		new File(dictfile).deleteOnExit();


		File allfile = new File(allfiles);
		if(allfile.exists()){
			allfile.delete();
		}
		FileCombine fc=new FileCombine(); 
		List<File> files = MyFiles.getAllFiles(datapath + "/FNLPDATA/", ".seg");
		fc.combineFiles(files, datapath + "/FNLPDATA/all.seg");  

		//生成新字典		
		dictfile = datapath + "/FNLPDATA/all.seg";
		String dicfile = datapath + "/FNLPDATA/all.dict";
		DICT.BMES2DICT(dictfile,dicfile);


		System.out.println(new Date().toString());
		System.out.println("Done!");

		String param = "-iter 100 -c 0.01  ../data/template-seg ../data/FNLPDATA/all.seg ../models/seg.m";
		CWSTrain.main(param.split(" +"));


	}

}
