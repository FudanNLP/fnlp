///**
//*  This file is part of FNLP (formerly FudanNLP).
//*  
//*  FNLP is free software: you can redistribute it and/or modify
//*  it under the terms of the GNU Lesser General Public License as published by
//*  the Free Software Foundation, either version 3 of the License, or
//*  (at your option) any later version.
//*  
//*  FNLP is distributed in the hope that it will be useful,
//*  but WITHOUT ANY WARRANTY; without even the implied warranty of
//*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//*  GNU Lesser General Public License for more details.
//*  
//*  You should have received a copy of the GNU General Public License
//*  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
//*  
//*  Copyright 2009-2014 www.fnlp.org. All rights reserved. 
//*/
//
//package org.fnlp.ml.classifier.struct;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
//import org.fnlp.ml.classifier.linear.Linear;
//import org.fnlp.ml.classifier.linear.OnlineTrainer;
//import org.fnlp.ml.classifier.linear.inf.Inferencer;
//import org.fnlp.ml.classifier.linear.update.Update;
//import org.fnlp.ml.loss.Loss;
//import org.fnlp.ml.types.Instance;
//import org.fnlp.ml.types.InstanceSet;
//import org.fnlp.util.MyArrays;
//
//public class OnlineHybridTrainer extends OnlineTrainer {
//
//	public OnlineHybridTrainer(Inferencer msolver, Update update,
//			Loss loss, int fsize, int iternum, float c) {
//		super(msolver, update, loss, fsize, iternum, c);
//	}
//
//	public Linear train(InstanceSet trainset, InstanceSet devset) {
//		int numSamples = trainset.size();
//		int count = 0;
//		for (int ii = 0; ii < numSamples; ii++) {
//			Instance inst = trainset.getInstance(ii);
//			int[][] targets = (int[][]) inst.getTarget();
//			for(int i = 0; i < targets.length; i++)	{
//				count += targets[i].length;
//			}
//		}
//
//		System.out.println("Training Number: "+numSamples);
//		System.out.println("Chars Number: " + count);
//
//		float hisErrRate = Float.MAX_VALUE;
//
//		long beginTime, endTime;
//		long beginTimeIter, endTimeIter;
//		int iter = 0;
//		int frac = numSamples / 10;
//
//		float[] averageWeights = null;
//		if (method == TrainMethod.Average || method == TrainMethod.FastAverage) {
//			averageWeights = new float[weights.length];
//		}
//
//		beginTime = System.currentTimeMillis();
//
//		if (shuffle)
//			trainset.shuffle(random);
//
//		while (iter++ < iternum) {
//			if (!simpleOutput) {
//				System.out.print("iter:");
//				System.out.print(iter + "\t");
//			}
//			float err = 0;
//			float errtot = 0;
//			int progress = frac;
//
//			beginTimeIter = System.currentTimeMillis();
//
//			float[] innerWeights = null;
//			if (method == TrainMethod.Average) {
//				innerWeights = Arrays.copyOf(weights, weights.length);
//			}
//
//			for (int ii = 0; ii < numSamples; ii++) {
//				Instance inst = trainset.getInstance(ii);
//				List pred = (List) inferencer.getBest(inst, 1);
//				float l = loss.calc((int[][]) pred.get(0), (int[][]) inst.getTarget());
//				if (l > 0)	{
//					err += l;
//					errtot++;
//					update.update(inst, weights, pred.get(0), c);
//				}
//				if (method == TrainMethod.Average) {
//					for (int i = 0; i < weights.length; i++) {
//						innerWeights[i] += weights[i];
//					}
//				}
//
//				if (DEBUG && l > 0) {
//					pred = (List) inferencer.getBest(inst, 1);
//					l = loss.calc((int[]) pred.get(0), (int[]) inst.getTarget());
//				}
//
//				if (!simpleOutput && ii % progress == 0) {
//					System.out.print('.');
//					progress += frac;
//				}
//			}
//
//			float curErrRate = err / count;
//
//			endTimeIter = System.currentTimeMillis();
//
//			if (!simpleOutput) {
//				System.out.println("\ttime:" + (endTimeIter - beginTimeIter)
//						/ 1000.0 + "s");
//				System.out.print("Train:");
//				System.out.print("\tTag acc:");
//			}
//			System.out.print(1 - curErrRate);
//			if (!simpleOutput) {
//				System.out.print("\tSentence acc:");
//				System.out.print(1 - errtot / numSamples);
//				System.out.println();
//			}
//			
//			System.out.print("Weight Numbers: "+MyArrays.countNoneZero(weights));
//			if (innerOptimized) {
//				int[] idx = MyArrays.getTop(weights.clone(), threshold, false);
//				MyArrays.set(weights, idx, 0.0f);
//				System.out.print("	After Optimized: " + MyArrays.countNoneZero(weights));
//			}
//			System.out.println();
//			
//			if (devset != null) {
//				evaluate(devset);
//			}
//			
//			if (method == TrainMethod.Average) {
//				for (int i = 0; i < innerWeights.length; i++) {
//					averageWeights[i] += innerWeights[i] / numSamples;
//				}
//			} else if (method == TrainMethod.FastAverage) {
//				for (int i = 0; i < weights.length; i++) {
//					averageWeights[i] += weights[i];
//				}
//			}
//			
//			if (interim) {
//				Linear p = new Linear(inferencer,
//						trainset.getAlphabetFactory());
//				try {
//					p.saveTo("tmp.model");
//				} catch (IOException e) {
//					System.err.println("write model error!");
//				}
//			}
//
//		}
//
//		if (method == TrainMethod.Average || method == TrainMethod.FastAverage) {
//			for (int i = 0; i < averageWeights.length; i++) {
//				averageWeights[i] /= iternum;
//			}
//			weights = null;
//			weights = averageWeights;
//			inferencer.setWeights(weights);
//		}
//
//		System.out.print("Weight Numbers: "+MyArrays.countNoneZero(weights));
//		if (finalOptimized) {
//			int[] idx = MyArrays.getTop(weights.clone(), threshold, false);
//			MyArrays.set(weights, idx, 0.0f);
//			System.out.print("	After Optimized: " + MyArrays.countNoneZero(weights));
//		}
//		System.out.println();
//
//		endTime = System.currentTimeMillis();
//		System.out.println("time escape:" + (endTime - beginTime) / 1000.0
//				+ "s");
//		Linear p = new Linear(inferencer, trainset.getAlphabetFactory());
//		return p;
//	}
//
//	public void evaluate(InstanceSet devset) {
//		float err = 0;
//		float errtot = 0;
//		int total = 0;
//		for (int i = 0; i < devset.size(); i++) {
//			Instance inst = devset.getInstance(i);
//			total += ((int[]) inst.getTarget()).length;
//			List pred = (List) inferencer.getBest(inst, 1);
//			float l = loss.calc(pred.get(0), inst.getTarget());
//			if (l > 0) {
//				errtot += 1.0;
//				err += l;
//			}
//
//		}
//		if (!simpleOutput) {
//			System.out.print("Test:\t");
//			System.out.print(total - err);
//			System.out.print('/');
//			System.out.print(total);
//			System.out.print("\tTag acc:");
//		} else {
//			System.out.print('\t');
//		}
//		System.out.print(1 - err / total);
//		if (!simpleOutput) {
//			System.out.print("\tSentence acc:");
//			System.out.println(1 - errtot / devset.size());
//
//		}
//		System.out.println();
//	}
//}