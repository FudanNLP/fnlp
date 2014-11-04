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

package org.fnlp.nlp.langmodel;

import java.io.IOException;

public class NGramModelTest{
	
		
	public static void main(String[] args) throws Exception {
		//
		String segfile_mini = "../tmp/wiki_mini_simp_seg";
		
		NGramModel model = new NGramModel(2);
		model.build(segfile_mini);
		
//		System.out.println("perplexity:" + model.computePerplexity("tmp/poi.dic"));
		System.out.println(model.getProbability("利用 符号"));
	}

	
}