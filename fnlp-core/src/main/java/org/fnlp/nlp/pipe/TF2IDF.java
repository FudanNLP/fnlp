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

package org.fnlp.nlp.pipe;

import java.io.Serializable;
import java.util.Iterator;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.sv.HashSparseVector;

import gnu.trove.iterator.TIntFloatIterator;

/**
 * 由TF计算IDF
 * 
 * @author xpqiu
 * 
 */
public class TF2IDF extends Pipe implements Serializable {

	private static final long serialVersionUID = 5563900451276233502L;
	public int[] idf;

	public TF2IDF(InstanceSet train, InstanceSet test) {

		int numFeatures = 0;
		// 得到最大的特征维数
		for (int i = 0; i < train.size(); i++) {
			int len = ((HashSparseVector) train.getInstance(i).getData()).size();
			if (len > numFeatures)
				numFeatures = len;
		}
		for (int i = 0; i < test.size(); i++) {
			int len = ((HashSparseVector) test.getInstance(i).getData()).size();
			if (len > numFeatures)
				numFeatures = len;
		}
		idf = new int[numFeatures + 1];

	}

	@Override
	public void addThruPipe(Instance inst) {
		HashSparseVector data = (HashSparseVector) inst.getData();
		TIntFloatIterator it = data.data.iterator();
		while (it.hasNext()) {
			it.advance();
			idf[it.key()]++;
		}
	}

}