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

package org.fnlp.nlp.pipe.templet;

import java.util.ArrayList;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.sv.BinarySparseVector;
import org.fnlp.nlp.pipe.Pipe;

public class TemplatePipe extends Pipe {

	private static final long serialVersionUID = -4863048529473614384L;
	private IFeatureAlphabet features;
	private ArrayList<RETemplate> group;
	public TemplatePipe(AlphabetFactory af,RETemplateGroup group){
		this.features = af.DefaultFeatureAlphabet();
		this.group = group.group;
	}
	@Override
	public void addThruPipe(Instance inst) throws Exception {
		String str = (String) inst.getSource();
		BinarySparseVector sv = (BinarySparseVector) inst.getData();
		List<RETemplate> templates = new ArrayList<RETemplate>();
		for(int i=0;i<group.size();i++){
			RETemplate qt = group.get(i);
			float w = qt.matches(str);
			if(w>0){
//				System.out.println(qt.comment);
				int id = features.lookupIndex("template:"+qt.comment);
				sv.put(id);
			}
		}
	}

}