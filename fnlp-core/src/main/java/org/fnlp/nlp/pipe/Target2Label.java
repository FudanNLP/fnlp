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
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.util.exception.UnsupportedDataTypeException;

/**
 * 将目标值对应的索引号作为类别
 * 
 * @author xpqiu
 * @version 1.0 Target2Label
 */
public class Target2Label extends Pipe implements Serializable {

	private static final long serialVersionUID = -4270981148181730985L;
	private LabelAlphabet labelAlphabet;


	public Target2Label(LabelAlphabet labelAlphabet) {
		this.labelAlphabet = labelAlphabet;
		useTarget = true;
	}

	@Override
	public void addThruPipe(Instance instance) throws UnsupportedDataTypeException {
		// 处理类别
//		instance.setTempData(instance.getTarget());

		Object t = instance.getTarget();
		if (t == null)
			return;

		if (t instanceof String) {
			instance.setTarget(labelAlphabet.lookupIndex((String) t));
		} else if (t instanceof Object[]) {
			Object[] l = (Object[]) t;
			int[] newTarget = new int[l.length];
			for (int i = 0; i < l.length; ++i)
				newTarget[i] = labelAlphabet.lookupIndex((String) l[i]);
			instance.setTarget(newTarget);
		} else if (t instanceof List) {
			List l = (List) t;
			int[] newTarget = new int[l.size()];
			for (int i = 0; i < l.size(); ++i)
				newTarget[i] = labelAlphabet.lookupIndex((String) l.get(i));
			instance.setTarget(newTarget);
		}else{
			throw new UnsupportedDataTypeException(t.getClass().toString());
		}
	}
}