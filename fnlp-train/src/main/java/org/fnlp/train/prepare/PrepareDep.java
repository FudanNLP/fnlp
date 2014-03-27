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

import java.util.Date;

import org.fnlp.nlp.corpus.fnlp.FNLPCorpus;
import org.fnlp.nlp.parser.dep.train.JointParerTrainer;

public class PrepareDep {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String datapath = "../data";
		
		FNLPCorpus corpus = new FNLPCorpus();
		//读FNLP数据
		corpus.read(datapath + "/FNLPDATA/ctb7.dat", null);
		//读自有数据
		corpus.readOurCorpus(datapath + "/ourdata",".txt","UTF8");
		
		String path = datapath + "/FNLPDATA/all.pos";
		corpus.writeOne(path);
		
		String param = "-iter 50 -c 0.01 "+path+" ../models/dep.m";
		JointParerTrainer.main(param.split(" +"));
//		Malt2ParerTester.main(param.split(" +"));
		
		System.out.println(new Date().toString());
		System.out.println("Done!");
	}

}