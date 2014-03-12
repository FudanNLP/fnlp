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

import java.io.Serializable;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.ISparseVector;

/**
 * 生成特征向量，包含类别信息
 * 
 * @author xpqiu
 * @version 1.0
 */
public abstract class Generator implements Serializable {

	private static final long serialVersionUID = 8640098825477722199L;
	
	public Generator()	{
	}
	
	public ISparseVector getVector(Instance inst) {
		return getVector(inst, inst.getTarget());
	}

	public abstract ISparseVector getVector(Instance inst, Object object);

}