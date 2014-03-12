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

package org.fnlp.nlp.cn.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 多线程序列标注
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class TaggerPool {

	private ExecutorService pool;
	private int numThread;
	private AbstractTagger tagers;
	List<Future> f;

	public TaggerPool( int num) throws Exception{
		numThread = num;
		pool = Executors.newFixedThreadPool(numThread);

		f = new ArrayList<Future>();
	}

	public void tag(String c) throws Exception{

		ClassifyTask t = new ClassifyTask(c);
		f.add(pool.submit(t));

	}

	class ClassifyTask implements Callable {
		private String inst;
		public  ClassifyTask(String inst) {
			this.inst = inst;
		}

		public String call() {
			//			System.out.println("Thread: "+ idx);
			String type = (String) tagers.tag(inst);
			return type;

		}
	}

	public  void loadPosTagger(String seg, String pos) throws Exception {
		tagers= new POSTagger(seg,pos);
	}

	public String getRes(int i) throws Exception {
		return (String) f.get(i).get();
	}

	public static void main(String[] args){
		String[] data = new String[]{
				"美国宇航局公布了世界上功率最大的火箭研制计划",
				"据称，这种火箭体积要比将“阿波罗”号宇航员送上月球的土星V型火箭大10%，",
				"功率比土星V型火箭高20%，耗资数360亿美元，可以运送宇航员前往小行星和火星等深空探索目的地。"
		};
		try {
			TaggerPool multitager = new TaggerPool(4);
			multitager.loadPosTagger("models/seg.m","models/pos.m");

			for(int i = 0; i < data.length; i++)	{
				multitager.tag(data[i]);
			}
			String[] res = new String[data.length];
			for(int i = 0; i < data.length; i++)	{

				res[i] = multitager.getRes(i);
			}
			for(int i = 0; i < data.length; i++)	{
				System.out.println(res[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}