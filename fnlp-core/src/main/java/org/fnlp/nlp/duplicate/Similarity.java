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

package org.fnlp.nlp.duplicate;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.nlp.duplicate.FingerPrint.Type;


/**
 * 计算两两相似度
 */
public class Similarity {
	/**
	 * 相似度矩阵&lt;a,&lt;b,sim&gt;&gt;
	 */
	public TIntIntHashMap[] similarityMap;
	public TreeSet<DocSim> dsMap;
	public double thres;
	private static final double lenDiffThresh = 0.3;
	public TreeMap<String, TIntArrayList> locationMap;
	public Type type;
	public int[] featureLen;
	public ArrayList<String> docs;
	public int capacity=100;
	private int numThreads;


	public Similarity(int numThreads, Type type) {
		this.type = type;
		this.numThreads = numThreads;
		thres = 0.7;

	}


	public void feature(){
		locationMap = new TreeMap<String, TIntArrayList>();
		Set<String> set;
		featureLen = new int[docs.size()];
		for(int i=1;i<docs.size();i++){
			set = FingerPrint.featureset(docs.get(i),type);

			featureLen[i]=set.size();
			Object[] sa =  set.toArray();			
			for(int j = 0; j < sa.length; j++) {
				TIntArrayList al = locationMap.get((String)sa[j]);
				if(al == null) {
					al = new TIntArrayList();
					al.add(i);
					locationMap.put((String)sa[j], al);
				}
				else
					al.add(i);
			}
		}
	}


	public  void duplicate(ArrayList<String> docs) throws Exception {
		this.docs = docs;
		dsMap = new TreeSet<DocSim>();
		feature();

		similarity();
		System.out.println("去重复");

		boolean[] dup = new boolean[docs.size()];
		for(int id1=0;id1<docs.size();id1++){
			if(dup[id1]||similarityMap[id1]==null)
				continue;


			ArrayList<Integer> ids = new ArrayList<Integer>();
			ids.add(id1);
			TIntIntIterator it = similarityMap[id1].iterator();
			for ( int i = similarityMap[id1].size(); i-- > 0; ) {
				it.advance();
				int id2 = it.key();				
				double sim = ((double)(it.value()* 2)) / (featureLen[id1] + featureLen[id2]);
				if(sim > thres ){
					dup[id2]= true;
					ids.add(id2);
				}
			}
			DocSim docSim = new DocSim(ids);
			dsMap.add(docSim);
		}
	}

	public  void printDocSim() {
		Iterator<DocSim> iter = dsMap.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
		}
	}

	public  void similarity() throws InterruptedException {	
		System.out.println("相似度");
		ThreadPoolExecutor pool = new ThreadPoolExecutor(numThreads, numThreads, 1000,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(capacity ));
		similarityMap = new TIntIntHashMap[docs.size()];
		
		Iterator<Entry<String, TIntArrayList>> iterator = 
				locationMap.entrySet().iterator();


		while(iterator.hasNext()) {
			if(pool.getQueue().remainingCapacity()==0){
				Thread.sleep(10);
				continue;
			}
			Entry<String, TIntArrayList> entry = iterator.next();

			TIntArrayList al = entry.getValue();
			CalcSimilarity cs = new CalcSimilarity(al);
			pool.execute(cs);
		}
		while(pool.getActiveCount()>0){
			Thread.sleep(10);
		}
		pool.shutdown();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {


	}
	public class CalcSimilarity implements Runnable {

		
		private TIntArrayList al;

		public CalcSimilarity() {

		}

		public CalcSimilarity(TIntArrayList al2) {
			this.al =al2;
		}

		@Override
		public void run() {
			for(int i = 0; i < al.size(); i++) {
				int a = al.get(i);

				for(int j = i + 1; j < al.size(); j++) {
					int b = al.get(j);
					double lenDiff;
					lenDiff = Math.abs(featureLen[a]-featureLen[b]);
					lenDiff /=Math.max(featureLen[a],featureLen[b]);
					if(lenDiff>lenDiffThresh)
						continue;
					
					int ids,idl;
					if(a<=b){
						ids = a;
						idl=b;
					}else{
						ids = b;
						idl=a;
					}					
					if(similarityMap[ids] == null)
						similarityMap[ids] = new TIntIntHashMap();
					synchronized (similarityMap[ids]) {						
						similarityMap[ids].adjustOrPutValue(idl, 1, 1);
					}

				}

			}
		}
	}
}