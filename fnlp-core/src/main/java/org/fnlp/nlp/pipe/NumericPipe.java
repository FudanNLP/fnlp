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
import org.fnlp.util.MyArrays;

/**
 * 处理数值数据
 * 
 * @author xpqiu
 * 
 */
public class NumericPipe extends Pipe implements Serializable {

	boolean normalizelength = false;
	public NumericPipe() {

	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1342039394164863109L;

	public void addThruPipe(Instance instance) {
		String[][] data = (String[][]) instance.getData();
		if(normalizelength){
			for (int i = 0; i < data.length; i++) {
				String[] arr = data[i];
				int[] d = MyArrays.string2int(arr);

				int len = 10;

				int[] nd = normlise(d,len);

				data[i] = MyArrays.int2string(nd); 
			}
		}
		instance.setData(data);
	}


	private int[] normlise(int[] d, int len) {
		if(d.length<=len)
			return d;
		float r = d.length*1.0f/len;
		int[] nd = new int[len];
		for(int i=0;i<len;i++){
			int idx = (int) Math.round(i*r);
			nd[i]=d[idx];

		}
		return nd;
	}
}