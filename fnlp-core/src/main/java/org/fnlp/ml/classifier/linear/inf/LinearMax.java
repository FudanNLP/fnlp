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

package org.fnlp.ml.classifier.linear.inf;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.feature.Generator;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.ISparseVector;

/**
 * @author xpqiu
 * @version 1.0
 */
public class LinearMax extends Inferencer {

	private static final long serialVersionUID = -7602321210007971450L;
	
	private Generator generator;
	private int ysize;

	public LinearMax(Generator generator, int ysize) {
		this.generator = generator;
		this.ysize = ysize;
	}
	
	public Predict getBest(Instance inst)	{
		return getBest(inst, 1);
	}

	public Predict getBest(Instance inst, int n) {

		Integer target = null;
		if (isUseTarget && inst.getTarget() != null)
			target = (Integer) inst.getTarget();

		Predict<Integer> pred = new Predict<Integer>(n);
		Predict<Integer> oracle = null;
		if (target != null) {
			oracle = new Predict<Integer>(n);
		}

		for (int i = 0; i < ysize; i++) {
			ISparseVector fv = generator.getVector(inst, i);
			float score = fv.dotProduct(weights);
			if (target != null && target == i)
				oracle.add(i,score);
			else
				pred.add(i,score);
		}
		return pred;
	}
	
}