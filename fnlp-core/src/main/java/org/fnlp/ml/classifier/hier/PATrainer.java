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

package org.fnlp.ml.classifier.hier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.fnlp.ml.classifier.hier.inf.MultiLinearMax;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.eval.Evaluation;
import org.fnlp.ml.feature.BaseGenerator;
import org.fnlp.ml.loss.Loss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.util.MyArrays;
import org.fnlp.util.MyHashSparseArrays;
/**
 * 大规模层次化多类分类训练
 * 针对类别数很大，因此权重向量用稀疏数组表示
 * @author xpqiu
 * @since 1.0
 */
public class PATrainer {

	/**
	 * 特征权重数组，每个类对于一个权重
	 */
	private HashSparseVector[] weights;
	/**
	 * 输出的分类器
	 */
	private Linear classifier;
	/**
	 * 推理器
	 */
	private MultiLinearMax msolver;
	/**
	 * 特征生成器
	 */
	private BaseGenerator featureGen;
	/**
	 * 损失函数
	 */
	private Loss loss;

	// 最大迭代次数
	private int maxIter = Integer.MAX_VALUE;
	private Tree tree;
	private float c;
	/**
	 * 保存中间结果
	 */
	public boolean interim=false;
	public boolean optim=false;
	private boolean incremental =false;
	
	/**
	 * 收敛控制，保留最近的错误率个数
	 */
	private static final int historyNum = 5;
	/**
	 * 收敛控制，最小误差
	 */
	private static final float eps = 1e-10f;

	public PATrainer(Linear pc, Loss loss, int maxIter, float c, Tree tr){
		msolver = (MultiLinearMax) pc.inf;
		msolver.isUseTarget(true);
		featureGen = pc.gen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;		
		incremental = true;
		weights = pc.weights;
	}

	public PATrainer(Inferencer msolver, BaseGenerator featureGen, Loss loss,
			int maxIter, float c, Tree tr) {
		this.msolver = (MultiLinearMax) msolver;
		this.featureGen = featureGen;
		this.loss = loss;
		this.maxIter = maxIter;
		tree = tr;
		this.c = c;
	}

	public Linear getClassifier() {
		return classifier;
	}

	/**
	 * 训练
	 * 
	 * @param eval
	 */
	public Linear train(InstanceSet trainingList, Evaluation eval) {
		System.out.println("Sample Size: " + trainingList.size());
		LabelAlphabet labels = trainingList.getAlphabetFactory().DefaultLabelAlphabet();

		System.out.println("Class Size: " + labels.size());

		if(!incremental){
			// 初始化权重向量到类中心
			weights = Mean.mean(trainingList, tree);
			msolver.setWeight(weights);
		}
		
		
		float[] hisErrRate = new float[historyNum];
		int numSamples = trainingList.size();
		int frac = numSamples / 10;

		// 开始循环
		System.out.println("Begin Training...");
		long beginTime = System.currentTimeMillis();
		int loops = 0; //循环计数
		while (loops++ < maxIter) {
			System.out.print("Loop: " + loops);
			float totalerror = 0;
			trainingList.shuffle();
			long beginTimeInner = System.currentTimeMillis();
			for (int ii = 0; ii < numSamples; ii++) {

				Instance inst = trainingList.getInstance(ii);
				int maxC = (Integer) inst.getTarget();
				
//				HashSet<Integer> t = new HashSet<Integer>();
//				t.add(maxC);
				Predict pred = (Predict) msolver.getBest(inst, 1);
				
				//从临时数据中取出正确标签打分信息，并删除
				Predict oracle = (Predict) inst.getTempData();
				inst.deleteTempData();
				
				int maxE = pred.getLabel(0);
				int error;
				if (tree == null) {
					error = (pred.getLabel(0) == maxC) ? 0 : 1;
				} else {
					error = tree.dist(maxE, maxC);
				}
				float loss = error- (oracle.getScore(0) - pred.getScore(0));

				if (loss > 0) {// 预测错误，更新权重

					totalerror += 1;
					// 计算含层次信息的内积
					// 计算步长
					float phi = featureGen.getVector(inst).l2Norm2();
					float alpha = (float) Math.min(c, loss / (phi * error));
					if (tree != null) {
						int[] anc = tree.getPath(maxC);
						for (int j = 0; j < anc.length; j++) {
							weights[anc[j]].plus(featureGen.getVector(inst), alpha);
						}
						anc = tree.getPath(maxE);
						for (int j = 0; j < anc.length; j++) {
							weights[anc[j]].plus(featureGen.getVector(inst), -alpha);
						}
					} else {
						weights[maxC].plus(featureGen.getVector(inst), alpha);
						weights[maxE].plus(featureGen.getVector(inst), -alpha);
					}

				}
				if (frac==0||ii % frac == 0) {// 显示进度
					System.out.print('.');
				}
			}
			float acc = 1 - totalerror / numSamples;
			System.out.print("\t Accuracy:" + acc);
			System.out.println("\t Time(s):"
					+ (System.currentTimeMillis() - beginTimeInner) / 1000);
			
			if(optim&&loops<=2){
				int oldnum = 0;
				int newnum = 0;
				for(int i = 0;i<weights.length;i++){
					oldnum += weights[i].size();
					MyHashSparseArrays.trim(weights[i],0.99f);
					newnum += weights[i].size();
				}
				System.out.println("优化：\t原特征数："+oldnum + "\t新特征数："+newnum);					
			}
			

			if (interim) {
				Linear  p = new Linear(weights, msolver, featureGen, trainingList.getPipes(), trainingList.getAlphabetFactory());
				try {
					p.saveTo("./tmp/model.gz");
				} catch (IOException e) {
					System.err.println("write model error!");
				}
				msolver.isUseTarget(true);
			}

			if (eval != null) {
				System.out.print("Test:\t");
				Linear classifier = new Linear(weights, msolver);
				eval.eval(classifier,2);
				msolver.isUseTarget(true);
			}
			hisErrRate[loops%historyNum] = acc;
			if(MyArrays.viarance(hisErrRate) < eps){
				System.out.println("convergence!");
				break;	
			}
		}
		System.out.println("Training End");
		System.out.println("Training Time(s):"
				+ (System.currentTimeMillis() - beginTime) / 1000);		

		classifier = new Linear(weights, msolver, featureGen, trainingList.getPipes(), trainingList.getAlphabetFactory());
		return classifier;
	}

}