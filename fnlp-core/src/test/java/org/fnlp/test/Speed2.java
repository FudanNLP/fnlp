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
