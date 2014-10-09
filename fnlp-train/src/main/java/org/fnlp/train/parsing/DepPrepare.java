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

package org.fnlp.train.parsing;

import java.util.Date;
import java.util.TreeSet;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.parser.dep.train.JointParerTrainer;
import org.fnlp.util.MyFiles;

public class DepPrepare {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String datapath = "../data";

		String allfile = datapath + "/FNLPDATA/all.dep";	
		String testfile = datapath + "/FNLPDATA/test.dep";	
		String trainfile = datapath + "/FNLPDATA/train.dep";
		MyFiles.delete(testfile);
		MyFiles.delete(trainfile);
		MyFiles.delete(allfile);

		FNLPCorpus corpus = new FNLPCorpus();
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);
		corpus.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-train.dat", null);
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");

		corpus.writeOne(trainfile);

		TreeSet<String> allRelSet = new TreeSet<String>();

		TreeSet<String> set1 = corpus.getAllRelations();
		allRelSet.addAll(set1);

		//处理测试数据
		FNLPCorpus corpust = new FNLPCorpus();
		//读自有数据
		corpust.read(datapath + "/FNLPDATA/WeiboFTB(v1.0)-test.dat", null);	
		corpust.writeOne(testfile);

		TreeSet<String> set2 = corpus.getAllRelations();
		allRelSet.addAll(set2);

		System.out.println(allRelSet);

		System.out.println(new Date().toString());
		System.out.println("Done!");
	}

}