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
import org.fnlp.nlp.cn.Chars;

/**
 * 将序列标注转换成字符串标签
 * 
 * @author xpqiu
 * 
 */
public class Seq2StrWithTag {

	public static String format(InstanceSet testSet, String[][] labelsSet) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < testSet.size(); i++) {
			Instance inst = testSet.getInstance(i);
			String[] labels = labelsSet[i];
			sb.append(format(inst, labels));
		}
		return sb.toString();
	}

	public static String format(Instance inst, String[] labels) {
		String[][] data = (String[][]) inst.getSource();

		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < data.length; j++) {		
			String label = labels[j];
			String w = data[j][0];
			// 处理连接在一起的英文字符串
			if(Chars.getType(w).equals("E")){
				if(label.contains("B-")) w = "[" + w + " ";
				else if(label.contains("M-")) w = w + " ";
				else if(label.contains("E-")) w = w + "]";
			}
			sb.append(w);
			int tagidx = label.indexOf("-");
			if (tagidx != -1) {
				String tag = label.substring(tagidx + 1);
				label = label.substring(0, tagidx);
				if (label.equals("E") || label.equals("S")) {
					sb.append("/" + tag + " ");
				}
			}else	{
				if (label.equals("E") || label.equals("S"))
					sb.append(" ");
			}
		}
		return sb.toString();
	}
}