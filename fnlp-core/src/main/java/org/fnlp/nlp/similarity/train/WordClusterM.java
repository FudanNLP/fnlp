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

package org.fnlp.nlp.similarity.train;

import gnu.trove.iterator.TIntIterator;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
/**
 * Brown 词聚类算法，多线程版
 * @author xpqiu
 * @since FudanNLP 1.5
 */
public class WordClusterM extends WordCluster{


	private static final long serialVersionUID = 58160232476872689L;
	transient int numThread =4;
	transient private ExecutorService pool;
	transient  float maxL;
	transient  int maxc1;
	transient  int maxc2;
	transient  AtomicInteger count = new AtomicInteger();

	
	public WordClusterM(int threads) {
		this.numThread = threads;
		pool = Executors.newFixedThreadPool(numThread);
	}

	

	
	public synchronized void getmax(float f, int i, int j){
		 if (f > maxL) {
				maxL = f;
				maxc1 = i;
				maxc2 = j;
			}
	}
	
class Multiplesolve implements Runnable {
		
		
		int c1,c2;
		public  Multiplesolve(int c1, int c2) {
			this.c1 = c1;
			this.c2 = c2;
		}
		@Override
		public void run() {
			float l= calcL(c1, c2);
			getmax(l,c1,c2);
			count.decrementAndGet();
		}

	}
	/**
	 * merge clusters
	 */
	public void mergeCluster() {
		maxc1 = -1;
		maxc2 = -1;
		maxL = Float.NEGATIVE_INFINITY;
		TIntIterator it1 = slots.iterator();
		
		while(it1.hasNext()){
			int i = it1.next();
			TIntIterator it2 = slots.iterator();
//			System.out.print(i+": ");
			while(it2.hasNext()){
				int j= it2.next();
				
				if(i>=j)
					continue;
//				System.out.print(j+" ");
				Multiplesolve c = new Multiplesolve(i,j);
				count.incrementAndGet();
				pool.execute(c);				
			}
//			System.out.println();
		}
		
			while(count.get()!=0){//等待所有子线程执行完  
				try {
					Thread.sleep(slotsize*slotsize/1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}  
			}  

		merge(maxc1,maxc2);
	}


	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		/**
		 * 分析命令参数
		 */
		Options opt = new Options();

		opt.addOption("path", true, "保存路径");
		opt.addOption("res", true, "评测结果保存路径");
		opt.addOption("slot", true, "槽大小");
		opt.addOption("thd", true, "线程个数");

		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.err.println("Parameters format error");
			return;
		}
		
		int threads = Integer.parseInt(cl.getOptionValue("thd", "3"));
		System.out.println("线程数量:"+threads);
		
		int slotsize = Integer.parseInt(cl.getOptionValue("slot", "20"));
		System.out.println("槽大小:"+slotsize);
		
		String file = cl.getOptionValue("path", "./tmp/SogouCA.mini.txt");
		System.out.println("数据路径:"+file);
		
		String resfile = cl.getOptionValue("res", "./tmp/cluster.txt");
		System.out.println("测试结果:"+resfile);
		
		long starttime = System.currentTimeMillis();
		SougouCA sca = new SougouCA(file);
		
		WordClusterM wc = new WordClusterM(threads);
		wc.slotsize = slotsize;
		wc.read(sca);
		
		wc.startClustering();
		wc.saveModel(resfile+".m");
		wc.saveTxt(resfile);
		wc = (WordClusterM) WordCluster.loadFrom(resfile+".m");
		wc.saveTxt(resfile+"1");
		long endtime = System.currentTimeMillis();
		System.out.println("Total Time:"+(endtime-starttime)/60000);
		System.out.println("Done");
		System.exit(0);
	}	
}