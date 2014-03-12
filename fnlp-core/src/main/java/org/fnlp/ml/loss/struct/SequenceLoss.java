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

package org.fnlp.ml.loss.struct;

import org.fnlp.ml.loss.Loss;

public class SequenceLoss implements Loss {
	/**
	 * 
	 * @author xpqiu
	 *
	 */
	public static enum Type	{
		POINT, EDGE
	}
	
	Type type;
	
	public SequenceLoss(Type type)	{
		this.type = type;
	}

	public float calc(Object o1, Object o2) {
		
		float errCount = 0;
		if (o1 instanceof int[] && o2 instanceof int[]) {
			int[] pred = (int[]) o1;
			int[] gold = (int[]) o2;

			if (type == Type.POINT)	{
				for (int i = 0; i < pred.length; i++) {
					if (pred[i] != gold[i])
						errCount++;
				}
			}else if (type == Type.EDGE)	{
				for (int i = 1; i < pred.length; i++) {
					if (pred[i - 1] != gold[i - 1] || pred[i] != gold[i])
						errCount++;
				}
			}
		}

		return errCount;
	}

}