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

package org.fnlp.train.seg;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.util.MyFiles;
/**
 * 生成分词训练文件
 * @author xpqiu
 *
 */
public class SegPrepare {

	/**
	 * 在FNLPDATA生成.seg文件，然后合并
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String datapath = "../data";
		
		
		String allfile = datapath + "/FNLPDATA/all.seg";	
		String testfile = datapath + "/FNLPDATA/test.seg";	
		String trainfile = datapath + "/FNLPDATA/train.seg";
		MyFiles.delete(testfile);
		MyFiles.delete(trainfile);
		MyFiles.delete(allfile);

		String dictfile = datapath + "/FNLPDATA/dict.seg";

		String segfile = datapath + "/FNLPDATA/temp.seg";
		//清理
		MyFiles.delete(dictfile);
		MyFiles.delete(segfile);
		

		FNLPCorpus corpus = new FNLPCorpus();
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");
		//读分词文件
		corpus.readCWS(datapath + "/FNLPDATA/seg",".txt","UTF8");	
		//读分词+词性文件
		corpus.readPOS(datapath + "/FNLPDATA/pos",".txt","UTF8");	
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);			
		corpus.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-train.dat", null);



		FNLP2BMES.w2BMES(corpus,segfile);		
		//FNLP2BMES.w2BMES(corpus,segfile_w); //?


		//词典转BMES
		//搜狗词典
		DICT dict = new DICT();
		String sougou = datapath + "/FNLPDATA/dict/SogouLabDic.dic.raw";

//		dict.readSougou(sougou,2,3,"sougou");
		//互动词典
		String hudong = datapath + "/FNLPDATA/dict/hudong.dic.all";
//		dict.readSougou(hudong,2,3,"");
		//添加其他词典
		dict.readDictionary(datapath + "/FNLPDATA/dict",".dic");

		//添加其他词典
//		dict.readDictionaryWithFrequency(datapath + "/FNLPDATA/dict",".dic.freq");




		//添加词性字典
		dict.readPOSDICT(datapath + "/FNLPDATA/词性字典", ".txt");
		dict.readPOSDICT(datapath + "/FNLPDATA/dict-sogou-input/txt", ".txt");

		dict.toBMES(dictfile,3);
		new File(dictfile).deleteOnExit();

		//合并训练文件
		
		
		
		List<File> files = MyFiles.getAllFiles(datapath + "/FNLPDATA/", ".seg");
		MyFiles.combine(trainfile,files.toArray(new File[files.size()]));
		
		//生成新字典		
		String dicfile = datapath + "/FNLPDATA/train.dict";
		DICT.BMES2DICT(trainfile,dicfile);

		//处理测试数据
		FNLPCorpus corpust = new FNLPCorpus();
		//读自有数据
		corpust.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-test.dat", null);	
		
		FNLP2BMES.w2BMES(corpust,testfile);		
		
		


		System.out.println(new Date().toString());
		System.out.println("Done!");




	}

}