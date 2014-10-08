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

package org.fnlp.nlp.cn.rl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gnu.trove.set.hash.THashSet;

public class RLSegTest {
	RLSeg rlseg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		CWSTagger tag = new CWSTagger("../models/seg.m");	

		rlseg = new RLSeg(tag,"../tmp/FNLPDATA/all.dict");
	}

	@After
	public void tearDown() throws Exception {
		rlseg.close();
	}

	@Test
	public void testGetNewWords() throws IOException {
//		THashSet<String> newset = new THashSet<String>();
//		THashSet<String> set;
////		set = rlseg.getNewWords("考几");
////		set = rlseg.getNewWords("抛诸脑后");
//		set = rlseg.getNewWords("买iphone");
//		System.out.println(set);
//		
	}

}