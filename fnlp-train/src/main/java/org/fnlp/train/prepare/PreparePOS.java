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

package org.fnlp.train.prepare;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.train.tag.POSAddEnTag;
import org.fnlp.train.tag.POSTrain;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

public class PreparePOS {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String datapath = "../data";
		
		String file = datapath + "/FNLPDATA/ctb.pos";
		(new File(file)).delete();
		String allfiles = datapath + "/FNLPDATA/all.pos";
		(new File(allfiles)).delete();
		String dictfile = datapath + "/FNLPDATA/dict.pos";
		(new File(dictfile)).delete();
		
		FNLPCorpus corpus = new FNLPCorpus();
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);
		//读分词+词性文件
		corpus.readPOS(datapath + "/FNLPDATA/pos",".txt","UTF8");	
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");
		
		String c2ePath = datapath + "/map/pos-fnlp2e.txt";
		HashMap<String, String> e2c = MyCollection.loadStringStringMap(c2ePath);
		TreeSet<String> posSet = corpus.getAllPOS();
		for(String pos: posSet){
			if(!e2c.containsKey(pos)){
				System.out.print("not pos:");
				System.out.println(pos);
				return;
				
			}
		}
		
		FNLP2POS.trans(corpus,file);
		String file_w = datapath + "/FNLPDATA/ctb_w.pos";
		FNLP2POS.trans_w(corpus,file_w);
		new File(file_w).deleteOnExit();
		
		//读字典
		DictPOS dp = new DictPOS();
		String out = datapath + "/FNLPDATA/dict.pos";
		dp.loadPath(datapath + "/FNLPDATA/词性字典",".txt");
		dp.loadPath(datapath + "/FNLPDATA/dict-sogou-input/txt", ".txt");
		dp.save(out);
		
		
		//合并
		
		FileCombine fc=new FileCombine(); 
		List<File> files = MyFiles.getAllFiles(datapath + "/FNLPDATA/", ".pos");
//		List<File> files2 = MyFiles.getAllFiles(datapath + "/FNLPDATA/pos/", ".txt");
//		files.addAll(files2);
		fc.combineFiles(files, allfiles);  
		
		
		System.out.println(new Date().toString());
		System.out.println("Done!");
		
		
		String param = "-iter 50 -c 0.01  ../data/template-s ../data/FNLPDATA/all.pos ../models/pos.m";
		POSTrain.main(param.split(" +"));
		//增加英文词性
		POSAddEnTag pp = new POSAddEnTag();
		pp.addEnTag("../models/pos.m");
		
	}

}