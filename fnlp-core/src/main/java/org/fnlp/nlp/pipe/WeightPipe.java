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

import org.fnlp.ml.types.Instance;

public class WeightPipe extends Pipe {

	private static final long serialVersionUID = 1L;
	private static float[] weight = {};
	
	public WeightPipe(boolean b){
		if(b){
			weight = new float[10];
			int i=0;
			for(;i<5;i++){
				weight[i] = 2f;
			}
			for(;i<10;i++){
				weight[i] = 1.5f;
			}
		}
	}

	@Override
	public void addThruPipe(Instance inst) throws Exception {
		
		Object sdata =  inst.getData();
		int len;
		if(sdata instanceof int[][]){//转换后的特征
			int[][] data = (int[][]) sdata;
			len = data.length;
		}else{
			System.err.println("WeightPipe: Error");
			return;
		}
		
		float w;
		if(len<weight.length)
			w = weight[len-1];
		else
			w = 1f;

		inst.setWeight(w);

	}

}