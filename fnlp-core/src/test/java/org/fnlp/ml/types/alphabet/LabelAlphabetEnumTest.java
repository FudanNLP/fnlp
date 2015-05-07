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

package org.fnlp.ml.types.alphabet;

import org.fnlp.nlp.cn.PartOfSpeech;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LabelAlphabetEnumTest {
	static LabelAlphabetEnum<PartOfSpeech> label ;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		label = new LabelAlphabetEnum<PartOfSpeech>(PartOfSpeech.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() {
		System.out.println(label.lookupIndex(PartOfSpeech.人称代词.name()));
		System.out.println(label.lookupIndex("人称代词"));
	}

}