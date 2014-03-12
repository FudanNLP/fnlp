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

package org.fnlp.ml.classifier.hier.inf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.fnlp.ml.classifier.hier.Predict;
import org.fnlp.ml.classifier.hier.Tree;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.feature.Generator;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.ISparseVector;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;

/**
 * 树层次结构求最大
 * @author xpqiu
 * @version 1.0 LinearMax package edu.fudan.ml.solver
 */
public class MultiLinearMax extends Inferencer implements Serializable	{


	private static final long serialVersionUID = 460812009958228912L;
	private LabelAlphabet alphabet;
	private Tree tree;
	int numThread;
	private transient ExecutorService pool;

	private HashSparseVector[] weights;
	private Generator featureGen;
	private int numClass;
	/**
	 * 类结构树的叶子节点集合，没有树就对应到各个标签
	 * 因为leafs = alphabet.toTSet();是不可序列化，故需要每次加载是重新生成
	 */
	transient TIntSet leafs =null;
	private boolean isUseTarget = true;


	/**
	 * 
	 * @param featureGen
	 * @param alphabet
	 * @param tree
	 * @param threads
	 */
	public MultiLinearMax(Generator featureGen, LabelAlphabet alphabet, Tree tree,int threads) {
		this.featureGen = featureGen;
		this.alphabet = alphabet;
		numThread = threads;
		this.tree = tree;
		pool = Executors.newFixedThreadPool(numThread);

		numClass = alphabet.size();
		if(tree==null){
			leafs = alphabet.toTSet();
		}else
			leafs= tree.getLeafs();
	}
	/**
	 * 预测最佳标签
	 */
	public Predict getBest(Instance inst) {
		return getBest(inst, 1);
	}
	/**
	 * 预测前n个最佳标签
	 */
	public Predict getBest(Instance inst, int n) {
		Integer target =null;
		if(isUseTarget)
			target = (Integer) inst.getTarget();

		ISparseVector fv = featureGen.getVector(inst);

		//每个类对应的内积
		float[] sw = new float[alphabet.size()];
		Callable<Float>[] c= new Multiplesolve[numClass];
		Future<Float>[] f = new Future[numClass];

		for (int i = 0; i < numClass; i++) {
			c[i] = new Multiplesolve(fv,i);
			f[i] = pool.submit(c[i]);
		}

		//执行任务并获取Future对象
		for (int i = 0; i < numClass; i++){ 			
			try {
				sw[i] = (Float) f[i].get();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		Predict pred = new Predict(n);
		Predict oracle = null;
		if(target!=null){
			oracle = new Predict(n);
		}

		TIntIterator it = leafs.iterator();

		while(it.hasNext()){
			
			float score=0;
			int i = it.next();

			if(tree!=null){//计算含层次信息的内积
				int[] anc = tree.getPath(i);
				for(int j=0;j<anc.length;j++){
					score += sw[anc[j]];
				}
			}else{
				score = sw[i];
			}

			//给定目标范围是，只计算目标范围的值
			if(target!=null&&target.equals(i)){
				oracle.add(i,score);
			}else{
				pred.add(i,score);
			}

		}
		if(target!=null){
			inst.setTempData(oracle);
		}
		return pred;
	}

	class Multiplesolve implements Callable {
		ISparseVector fv;
		int idx;
		public  Multiplesolve(ISparseVector fv2,int i) {
			this.fv = fv2;
			idx = i;
		}

		public Float call() {

			// sum up xi*wi for each class
			float score = fv.dotProduct(weights[idx]);
			return score;

		}
	}
	public void setWeight(HashSparseVector[] weights) {
		this.weights = weights;

	}

	public void isUseTarget(boolean b) {
		isUseTarget = b;

	}

	private void writeObject(ObjectOutputStream oos) throws IOException{
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
//		System.out.println("s readObject");
		ois.defaultReadObject();
		if(tree==null){
			leafs = alphabet.toTSet();
		}else
			leafs= tree.getLeafs();
		pool = Executors.newFixedThreadPool(numThread);
	}
}