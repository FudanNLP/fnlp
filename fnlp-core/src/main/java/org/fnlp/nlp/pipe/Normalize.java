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

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.SparseVector;

/**
 * 归一化，data类型须为SparseVector
 * @author xpqiu
 *
 */
public class Normalize extends Pipe  implements Serializable {

	private static final long serialVersionUID = -4740915822925015609L;

	@Override
	public void addThruPipe(Instance instance) {
		SparseVector data = (SparseVector) instance.getData();
		data.normalize();
	}

}