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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.cn.tag.AbstractTagger;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.util.MultiValueMap;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;
import org.fnlp.util.exception.LoadModelException;
/**
 * 增加英文词性标签
 * @author Xipeng
 *
 */
public class POSAddEnTag {

	
	
	static String c2ePath = "../data/map/pos-fnlp2e.txt";

		
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//
		POSAddEnTag pp = new POSAddEnTag();
		pp.addEnTag("../models/pos.m");
		
		System.out.print("Done");
	}

	public void addEnTag(String file) throws LoadModelException,
			IOException {
		AbstractTagger cl = new POSTagger(file);
			

		addEnTag(cl,file);
		cl.saveTo(file);
	}

	private void addEnTag(AbstractTagger cl, String file) throws IOException {
		LabelAlphabet label = cl.factory.DefaultLabelAlphabet();	
		HashMap<String, String> map = MyCollection.loadStringStringMap(c2ePath);
		cl.factory.remove("label-en");
		LabelAlphabet enLabel = cl.factory.buildLabelAlphabet("label-en");
		enLabel.clear();
		enLabel.setStopIncrement(false);
		for(int i=0;i<label.size();i++){
			String cn = label.lookupString(i);
			String en = map.get(cn);			
			if(en==null)
				System.out.println("POSTag Not Found: "+cn);
			int id = enLabel.lookupIndex(en);			
		}
		enLabel.setStopIncrement(true);
	}

}