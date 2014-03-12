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
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.SparseVector;

import gnu.trove.iterator.TIntFloatIterator;

/**
 * 计算IFIDF
 * 
 * @author xpqiu
 * 
 */
public class TFIDF extends Pipe implements Serializable {

	private static final long serialVersionUID = 2937341538282834618L;
	int[] idf;
	private int docNum;

	public TFIDF(int[] idf, int docNum) {
		this.idf = idf;
		this.docNum = docNum;
	}

	@Override
	public void addThruPipe(Instance inst) {
		HashSparseVector data = (HashSparseVector) inst.getData();
		TIntFloatIterator it = data.data.iterator();
		while (it.hasNext()) {
			it.advance();
			int id = it.key();
			if (idf[id] > 0) {
				float value = (float) (it.value()*Math.log(docNum / idf[id]));
				data.put(id, value);
			}
		}

	}

}