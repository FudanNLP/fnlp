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

package org.fnlp.nlp.cn.tag.format;

import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
/**
 * 
 * @author xpqiu
 *
 */
public class BasicFormatter {
	public static String format(InstanceSet testSet, String[][] labelsSet) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < testSet.size(); i++) {
			Instance inst = testSet.getInstance(i);
			String[] labels = labelsSet[i];
			sb.append(format(inst, labels));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static String format(Instance inst, String[] labels) {

		StringBuilder sb = new StringBuilder();
		List data = (List) inst.getSource();

		for (int j = 0; j < data.size(); j++) {
			sb.append(data.get(j));
			sb.append('\t');
			sb.append(labels[j]);
			sb.append("\n");
		}
		return sb.toString();
	}
}