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

package org.fnlp.ml.classifier.linear.update;

import org.fnlp.ml.loss.Loss;
import org.fnlp.ml.types.Instance;

/**
 * 线性分类的参数更新类，采用PA算法
 */
public class LinearMaxPAUpdate extends AbstractPAUpdate {

	public LinearMaxPAUpdate(Loss loss) {
		super(loss);
	}

	@Override
	protected int diff(Instance inst, float[] weights, Object target,
			Object predict) {

		int[] data = (int[]) inst.getData();
		int gold;
		if (target == null)
			gold = (Integer) inst.getTarget();
		else
			gold = (Integer) target;
		int pred = (Integer) predict;

		for (int i = 0; i < data.length; i++) {
			if (data[i] != -1) {
				int ts = data[i] + gold;
				int ps = data[i] + pred;
				diffv.put(ts, 1.0f);
				diffv.put(ps, -1.0f);
				diffw += weights[ts]-weights[ps];  // w^T(f(x,y)-f(x,ybar))
			}
		}

		return 1;
	}

}