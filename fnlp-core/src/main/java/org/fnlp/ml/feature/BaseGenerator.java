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

package org.fnlp.ml.feature;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.ISparseVector;

/**
 * 简单将data返回 特征不包含类别信息
 * 
 * @author xpqiu
 * 
 */
public class BaseGenerator extends Generator {

	private static final long serialVersionUID = 5209575930740335391L;
	

	public ISparseVector getVector(Instance inst) {

		return (ISparseVector) inst.getData();
	}

	public ISparseVector getVector(Instance inst, Object object) {
		return getVector(inst);
	}
}