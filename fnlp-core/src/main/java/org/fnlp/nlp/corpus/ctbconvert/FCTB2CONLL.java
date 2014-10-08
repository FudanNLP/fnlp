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

package org.fnlp.nlp.corpus.ctbconvert;

import java.io.IOException;
import java.nio.charset.Charset;

import org.fnlp.ml.types.InstanceSet;
/**
 * CTB转为FNLP格式
 * @author Xipeng
 *
 */
public class FCTB2CONLL {

	public static void main(String[] args) throws IOException{
		DependentTreeProducter rp = new DependentTreeProducter();
		InstanceSet ins = MyTreebankReader.readTrees("../data/ctb/data", null,Charset.forName("UTF8"));
//		InstanceSet ins = MyTreebankReader.readNewTrees("./data/ctb/data", null,Charset.forName("UTF8"));
		
		rp.write(ins, "../data/ctb/result.txt", "../data/headrules.txt");
		System.out.print("Done!");
	}
}