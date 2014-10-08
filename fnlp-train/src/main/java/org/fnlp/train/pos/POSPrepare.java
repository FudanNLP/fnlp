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

package org.fnlp.train.pos;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

public class POSPrepare {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		TreeSet<String> allpostSet = new TreeSet<String>();

		String datapath = "../data";

		String ctbfile = datapath + "/FNLPDATA/ctb.pos";
		(new File(ctbfile)).delete();
		String trainfile = datapath + "/FNLPDATA/train.pos";
		(new File(trainfile)).delete();
		String testfile = datapath + "/FNLPDATA/test.pos";
		(new File(testfile)).delete();
		String dictfile = datapath + "/FNLPDATA/dict.pos";
		(new File(dictfile)).delete();

		FNLPCorpus corpus = new FNLPCorpus();
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);
		corpus.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-train.dat", null);

		//读分词+词性文件
		corpus.readPOS(datapath + "/FNLPDATA/pos",".txt","UTF8");	
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");

		FNLP2POS.trans(corpus,ctbfile);

		allpostSet.addAll(corpus.getAllPOS());

		


		//读字典
		DictPOS dp = new DictPOS();
		String out = datapath + "/FNLPDATA/dict.pos";
//		dp.loadPath(datapath + "/FNLPDATA/词性字典",".txt");
//		dp.loadPath(datapath + "/FNLPDATA/dict-sogou-input/txt", ".txt");
		dp.save(out);
		
		allpostSet.addAll(dp.getPosSet());


		//合并

		List<File> files = MyFiles.getAllFiles(datapath + "/FNLPDATA/", ".pos");

		MyFiles.combine(trainfile,files.toArray(new File[files.size()]));  


		//处理测试数据
		FNLPCorpus corpust = new FNLPCorpus();
		//读自有数据
		corpust.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-test.dat", null);	
		
		allpostSet.addAll(corpust.getAllPOS());
		
		
		System.out.println(allpostSet);

		FNLP2POS.trans(corpust,testfile);
		
		
		//check
		
		String c2ePath = datapath + "/map/pos-fnlp2e.txt";
		HashMap<String, String> e2c = MyCollection.loadStringStringMap(c2ePath);		
		
		for(String pos: allpostSet){
			if(!e2c.containsKey(pos)){
				System.out.print("not pos:");
				System.out.println(pos);			}
		}
		
		String allposset = datapath + "/FNLPDATA/allpos";
		MyCollection.write(allpostSet, allposset);


		System.out.println(new Date().toString());
		System.out.println("Done!");



	}

}