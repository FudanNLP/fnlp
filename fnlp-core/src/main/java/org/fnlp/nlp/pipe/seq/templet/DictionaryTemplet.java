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

package org.fnlp.nlp.pipe.seq.templet;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ontology.Dictionary;
/**
 * 通过字典生成特征
 * @author xpqiu
 *
 */
public class DictionaryTemplet implements Templet, Serializable {

	private static final long serialVersionUID = -4516243129442692024L;
	private Dictionary d;
	private int[] args;
	private int id;
	private String text;

	public DictionaryTemplet(Dictionary d, int id, int ... args) {
		this.d = d;
		this.id = id;
		this.args = args;
		Arrays.sort(args);
		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(":dict");
		for(int i=0; i<args.length; i++) {
			sb.append(':');
			sb.append(args[i]);
		}
		sb.append(':');
		this.text = new String(sb);
	}

	@Override
	public int generateAt(Instance instance, IFeatureAlphabet features, int pos, int ... numLabels) {
		assert(numLabels.length == 1);
		String[][] data = ( String[][]) instance.getData();
		
		int len = data[0].length;
		
		StringBuffer sb = new StringBuffer(text);
		for(int i=0; i<args.length; i++) {
			int idx = pos+args[i];
			if(idx>=0&&idx<len)
				sb.append((data[0][idx]));
		}
		int index = -1;		
		if(d.contains(sb.toString())){
			sb.append(d.name);
			index = features.lookupIndex(sb.toString(), numLabels[0]);
		}
		return index;
	}

	public int getOrder() { return 0; }

	public int[] getVars() { return new int[]{0}; }

	public int offset(int... curs) {
		return 0;
	}



}