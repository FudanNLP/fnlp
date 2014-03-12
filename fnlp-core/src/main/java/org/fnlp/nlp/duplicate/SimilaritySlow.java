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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.nlp.duplicate.FingerPrint.Type;

/**
 * 计算两两相似度
 */
public class SimilaritySlow implements ISimilarity {

	public TreeSet<DocSim> dsMap;
	public double thres;
	private static final int lenDiffThresh = 4;
	public TIntHashSet[] features;
	public Type type;
	public ArrayList<Documents> docs;
	private int numThreads;
	private boolean[] dup;
	private int[] mergeto;
	int maxDocsNum = 5000;
	private ArrayList<TIntHashSet> lenGroup;

	public SimilaritySlow(int numThreads, Type type) {
		this.type = type;
		this.numThreads = numThreads;
		thres = 0.5;

	}


	public void feature(){
		features = new TIntHashSet[docs.size()];

		StringFeatureAlphabet fa = new StringFeatureAlphabet(); 

		for(int i=0;i<docs.size();i++){
			Set<String> set = FingerPrint.featureset(docs.get(i).content,type);
			features[i] = new TIntHashSet(set.size());
			Iterator<String> it = set.iterator();
			while(it.hasNext()){
				String str = it.next();				
				int idx = fa.lookupIndex(str);
				features[i].add(idx);
			}			
		}
		group();
	}


	private void group() {
		lenGroup = new ArrayList<TIntHashSet>();
		for(int i=0;i<features.length;i++){
			int len = features[i].size();
			if(len>=lenGroup.size()){
				for(int j=lenGroup.size();j<=len;j++){
					lenGroup.add(new TIntHashSet());
				}
			}				
			lenGroup.get(len).add(i);
		}

	}

	//Override
	public  TreeSet<DocSim> duplicate(ArrayList<Documents> docs) throws Exception {
		this.docs = docs;
		dsMap = new TreeSet<DocSim>();
		feature();
		dup = new boolean[docs.size()];
		mergeto = new int[docs.size()];
		ThreadPoolExecutor pool = new ThreadPoolExecutor(numThreads, numThreads, 1000,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());



		jobs=new AtomicInteger();
		int total = 0;
		for(int i=0;i<docs.size()-1;i++) {
			if(dup[i]){				
				continue;
			}
			for(int k=0;k<=lenDiffThresh;k++){
				int idx = features[i].size()-lenDiffThresh/2+k;
				if(idx<0||idx>=lenGroup.size())
					continue;
				TIntHashSet g = lenGroup.get(idx);
				TIntIterator it = g.iterator();
				for(int t=g.size();t>0;t--){
					int j = it.next();
					if(dup[j]||j==i){
						continue;
					}
					CalcSimilarity cs = new CalcSimilarity(i,j);
					total++;
					pool.execute(cs);
				}
			}
		}
		
		while(jobs.get()<total){
			Thread.sleep(10);
		}
		pool.shutdown();
		
		HashMap<Integer,ArrayList<Integer>>  map =   new HashMap<Integer,ArrayList<Integer>> ();
		for(int id1=0;id1<docs.size();id1++){
			if(!dup[id1]){
				ArrayList<Integer> li = new ArrayList<Integer>();
				li.add(id1);
				map.put(id1, li);
			}
		}
		for(int id1=0;id1<docs.size();id1++){
			if(dup[id1]){
				int root = findroot(id1);
				map.get(root).add(id1);
			}
		}
		TreeSet<DocSim> mapp =new TreeSet<DocSim>();
		Iterator<Entry<Integer, ArrayList<Integer>>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<Integer, ArrayList<Integer>> el = it.next();
			DocSim d = new DocSim(el.getValue());
			mapp.add(d);
		}
		return mapp;
	}
	private int findroot(int id1) {
		if(dup[id1])
			return findroot(mergeto[id1]);
		else
			return id1;
	}

	public  void printDocSim() {
		Iterator<DocSim> iter = dsMap.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next().toString());
		}
	}
	AtomicInteger jobs;


	public class CalcSimilarity implements Runnable {


		private int idx;
		private int idy;

		public CalcSimilarity(int i, int j) {
			this.idx =i;
			this.idy = j;
		}

		@Override
		public void run() {
			jobs.incrementAndGet();
			if(dup[idx]||dup[idy])
				return;
			try {
				double sim = simJaccard(features[idx],features[idy]);	
				if(sim>thres){
					synchronized (dup) {
						dup[idy]=true;
						mergeto[idy]=idx;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public double simJaccard(TIntHashSet s1, TIntHashSet s2) {
		int com = 0;
		if(s1==null||s2==null)
			return 0;
		TIntIterator it = s1.iterator();
		for ( int i = s1.size(); i-- > 0; ) {
			int v = it.next();
			if(s2.contains(v))
				com++;
		}
		double sim = com*1.0/(s1.size()+s2.size()-com);
		return sim;
	}
}