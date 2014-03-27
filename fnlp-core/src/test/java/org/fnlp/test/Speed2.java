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

package org.fnlp.test;

import org.fnlp.ml.types.LinearSparseVector;
import org.fnlp.ml.types.sv.HashSparseVector;

public class Speed2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		int len = 100000;
		float[] w =  new float [len];
		
		for(int i=0;i<len;i++){
			w[i]=(float) i/100.0f;
		}
		
		HashSparseVector sv1 = new HashSparseVector();
		LinearSparseVector sv2 = new LinearSparseVector();
		
		
		long stime;
		long etime;
		
		stime = System.currentTimeMillis();
		for(int i=0;i<len;i++){
			sv1.put(i, w[i]);
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		for(int i=0;i<len;i++){
			sv2.put(i, w[i]);
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		for(int i=0;i<len;i++){
			sv1.get(i);
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		for(int i=0;i<len;i++){
			sv2.get(i);
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));

	}

}