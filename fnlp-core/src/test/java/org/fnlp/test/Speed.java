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

public class Speed {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int len = 10000000;
		Double[] w1 = new Double[len];
		double[] w2 = new double[len];
		Float[] w3 = new Float[len];
		float[] w4 =  new float [len];
		
		
		
		for(int i=0;i<len;i++){
			w1[i]=(double) i/100.0;
			w2[i]=(double) i/100.0;
			w3[i]=(float) i/100.0f;
			w4[i]=(float) i/100.0f;
		}

		long stime;
		long etime;
		
		stime = System.currentTimeMillis();
		Double res1 = 0.0;
		for(int i=0;i<len;i++){
			res1 += w1[i]*w1[i];
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		double res2 = 0;
		
		for(int i=0;i<len;i++){
			res2 += w2[i]*w2[i];
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		Float res3 = 0.0f;
		for(int i=0;i<len;i++){
			res3 += w3[i]*w3[i];
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
		
		stime = System.currentTimeMillis();
		float res4 = 0.0f;
		for(int i=0;i<len;i++){
			res4 += w4[i]*w4[i];
		}
		etime = System.currentTimeMillis();		
		System.out.println("time:" + (etime-stime));
	}

}