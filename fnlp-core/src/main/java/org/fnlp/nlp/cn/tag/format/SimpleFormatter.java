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

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;

public class SimpleFormatter {
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
		String[][] data = (String[][]) inst.getSource();

		for (int i = 0; i < data[0].length; i++) {
			sb.append(data[0][i]);
			sb.append("\t");
			sb.append(labels[i]);
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 每行为特征 预测值 真实值
	 * @param testSet
	 * @param labelsSet
	 * @param gold
	 * @return
	 */
	public static String format(InstanceSet testSet, String[][] labelsSet, String[][] gold) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < testSet.size(); i++) {
			Instance inst = testSet.getInstance(i);
			String[] labels = labelsSet[i];
			sb.append(format(inst, labels, gold[i]));
			sb.append("\n");
		}
		return sb.toString();
	}
	/**
	 * 每行为特征 预测值 真实值
	 * @param inst
	 * @param labels
	 * @param gold
	 * @return
	 */
	public static String format(Instance inst, String[] labels, String[] gold) {

		StringBuilder sb = new StringBuilder();
		String[][] data = (String[][]) inst.getSource();
		int feaNum = data.length;
		int seqNum = data[0].length;
		for (int i = 0; i < seqNum; i++) {
			for (int j = 0; j < feaNum; j++) {
				sb.append(data[j][i]);
				sb.append("\t");
			}
			sb.append(labels[i]);
			sb.append("\t");
			sb.append(gold[i]);
			sb.append("\n");
		}
		return sb.toString();
	}
}