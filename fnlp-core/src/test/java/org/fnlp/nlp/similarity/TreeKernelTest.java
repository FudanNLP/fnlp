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

package org.fnlp.nlp.similarity;

import java.util.Vector;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
import org.fnlp.ontology.graph.WordGraph;
import org.fnlp.util.MyStrings;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TreeKernelTest {
	
	CNFactory factory;
	TreeKernel tk;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
//		factory = CNFactory.getInstance("../models",Models.ALL);
//		WordGraph wg = new WordGraph();
//		
//		wg.read("../models/wordgraph.txt");
//		tk = new TreeKernel();
//		tk.setWordGraph(wg);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalc() throws Exception {
		Vector<String[]> s = new Vector<String[]>();
		s.add(new String[]{"听歌","唱歌"});
		s.add(new String[]{"听歌","听个歌"});
		s.add(new String[]{"听下面的歌","听周杰伦的歌"});
		s.add(new String[]{"听吴世宇的歌","听周杰伦的歌"});
		s.add(new String[]{"要导航","我要导航"});
		s.add(new String[]{"我想去人民广场","我想到人民广场"});
		s.add(new String[]{"我要去肯德基","我要去麦当劳"});
		s.add(new String[]{"我要去肯德基","我要先去肯德基"});
		s.add(new String[]{"发短信","发一条短信"});
		s.add(new String[]{"我想去南京东路","我想去#POI#"});
		s.add(new String[]{"播放流行的歌","我想听流行的歌"});
		s.add(new String[]{"拍张照片","拍照"});
//		for(int i = 0; i < s.size(); i++){
//			float score = tk.calc(factory.parse2T(s.get(i)[0]), factory.parse2T(s.get(i)[1]));
//			System.out.println(MyStrings.toString(s.get(i), " ") );
//			System.out.println(score );
//		}
		
	}

}