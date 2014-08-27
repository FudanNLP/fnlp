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

package org.fnlp.nlp.cn.anaphora;

import static org.junit.Assert.*;

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AR_ReaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
//		Linear cl=null;;
//		cl = Linear.loadFrom("./models/ar.m");
//		InstanceSet test = new InstanceSet(cl.getPipe());
//		String s = "随着中国经济融入世界经济进程的加快，和以高科技为主体的经济发展，众多跨国公司在中国不 在是单纯的建立生产基地，而是越来越多的将研发中心转移到了中国。目前已经有包括：微软、 摩托罗拉和贝尔实验室在内的几十家规模较大的跨国公司，将其研发中心在中国落户。 ";
//		POSTagger tag = new POSTagger("../models/seg.m","../models/pos.m");
//		String[][][] tagstr = tag.tag2DoubleArray(s);
//		AR_Reader reader = new AR_Reader(tagstr, s);
//		
//		test.loadThruPipes(reader);
//		for(int i=0;i<test.size();i++){
//			Instance inst = test.getInstance(i);
//			System.out.println(inst);
//			String ss = cl.getStringLabel(inst);
//			if(ss.equals("1"))
//				System.out.println(ss+"\n");
//		}
//		System.gc();
	}

}