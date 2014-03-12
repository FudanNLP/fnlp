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

package org.fnlp.nlp.pipe.seq;

import java.io.Serializable;
import java.util.Arrays;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.pipe.Pipe;

public class TokenNormalize extends Pipe implements Serializable {

	private static final long serialVersionUID = 8129957080708134793L;

	private LabelAlphabet labels;

	public TokenNormalize(LabelAlphabet labels) {
		this.labels = labels;
	}

	/**
	 * 将英文、数字标点硬标为S，目前废弃
	 */
	public void addThruPipe(Instance instance) throws Exception {
		String[][] data = (String[][]) instance.getData();

		int[][] tempData = new int[data[0].length][labels.size()];
		
		
		for (int i = 0; i < data[0].length; i++) {
			char s = data[0][i].charAt(0);
			if (Chars.isLetterOrDigitOrPunc(s)) {
				Arrays.fill(tempData[i], 1);
				tempData[i][labels.lookupIndex("S")] = 0;
			}
		}

//		for(int i = 0; i < tempData.length; i++) {
//			for(int j = 0; j < tempData[i].length; j++)
//				System.out.print(tempData[i][j]);
//			System.out.println();
//		}
		
		instance.setTempData(tempData);
	}
}