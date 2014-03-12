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

package org.fnlp.ml.classifier.linear;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.SeriesPipes;

public class ClassifierPool {

	private ExecutorService pool;
	private int numThread;
	private Linear classifier;
	ArrayList<Future> f ;
	private SeriesPipes pp;

	public ClassifierPool(int numThread2){
		numThread = numThread2;
		pool = Executors.newFixedThreadPool(numThread);
		f= new ArrayList<Future>();
	}

	public void classify(String c) throws Exception{
		Instance inst = new Instance(c);

		ClassifyTask t = new ClassifyTask(inst);
		f.add(pool.submit(t));		
	}

	public String getRes(int i) throws Exception {
		// TODO Auto-generated method stub
		return (String) f.get(i).get();
	}

	class ClassifyTask implements Callable {

		private Instance inst;
		public  ClassifyTask(Instance inst) {
			this.inst = inst;	
		}

		public String call() {

			try {
				pp.addThruPipe(inst);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String type = classifier.getStringLabel(inst);
			return type;

		}
	}

	public  void loadFrom(String modelfile) throws Exception {

		classifier= Linear.loadFrom(modelfile);
		pp = (SeriesPipes) classifier.getPipe();

	}

	public void reset() {
		pool.shutdownNow();
		pool=Executors.newFixedThreadPool(numThread);
		f= new ArrayList<Future>();
	}

}