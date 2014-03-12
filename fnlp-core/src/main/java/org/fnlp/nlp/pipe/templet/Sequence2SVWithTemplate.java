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

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.BinarySparseVector;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 将字符序列转换成特征序列 因为都是01特征，这里保存的是索引号
 * 
 * @author xpqiu
 * 
 */
public class Sequence2SVWithTemplate extends Pipe{

	private static final long serialVersionUID = -4782249062779216625L;
	TempletGroup templets;
	public IFeatureAlphabet features;
	LabelAlphabet labels;

	public Sequence2SVWithTemplate(TempletGroup templets,
			IFeatureAlphabet features, LabelAlphabet labels) {
		this.templets = templets;
		this.features = features;
		this.labels = labels;
	}

	public void addThruPipe(Instance instance) throws Exception {	
		String[][] data = (String[][]) instance.getData();

		BinarySparseVector sv = new BinarySparseVector();
		for (int j = 0; j < templets.size(); j++) {
			int[] idx = templets.get(j).generateAt(instance,
					this.features, 1);
			sv.put(idx);
		}
		instance.setData(sv);
	}
}