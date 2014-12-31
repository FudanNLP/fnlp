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

package org.fnlp.nlp.pipe.templet;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;

/**
 * 基于模板的文本序列特征抽取
 * 处理数据格式为：String[][]
 * 例如：
 *     x1 x2 x3
 *     y1 y2 y3
 *     z1 z2 z3
 */
public class BaseTemplet implements Templet {

	private static final long serialVersionUID = -4019640352729137328L;

	String templet;

	int id;
	/**
	 * 特征标记，两维数组，第二维大小为2
	 */
	int[][] dims;

	/**
	 * 构造函数
	 * @param id
	 * @param dims
	 */
	public BaseTemplet(int id, int[][] dims) {
		this.id = id;
		this.dims = dims;
	}

	public int[] generateAt(Instance instance, IFeatureAlphabet features,
			int numLabels) throws Exception {

		String[][] data = (String[][]) instance.getData();

		int len = data[0].length;
		int[] index = new int[len];
		for(int pos = 0;pos<len;pos++){
			StringBuffer sb = new StringBuffer();
			sb.append(id);
			sb.append(':');
			for (int i = 0; i < dims.length; i++) {
				String rp = "";
				int k = dims[i][0]; //行号
				int j = dims[i][1]; //列号
				if (pos + j < 0 || pos + j >= len) {
					if (pos + j < 0)
						rp = "B_" + String.valueOf(-(pos + j) - 1);
					if (pos + j >= len)
						rp = "E_" + String.valueOf(pos + j - len);
				} else {
					rp = data[k][pos + j];
				}
				if (-1 != rp.indexOf('$'))
					rp = rp.replaceAll("\\$", "\\\\\\$");
				sb.append(rp);
				sb.append("//");
			}
//			System.out.println(sb.toString());
			index[pos] = features.lookupIndex(sb.toString(),numLabels);
		}
		return index;
	}

	public String toString() {
		return this.templet;
	}
}