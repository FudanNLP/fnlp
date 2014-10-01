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

package org.fnlp.ml.classifier.struct;

import java.io.IOException;
import java.util.Arrays;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.loss.Loss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.util.MyArrays;

public class OnlineStructTrainer extends OnlineTrainer {

	public OnlineStructTrainer(Inferencer msolver, Update update,
			Loss loss, int fsize, int iternum, float c) {
		super(msolver, update, loss, fsize, iternum, c);
	}

	public Linear train(InstanceSet trainset, InstanceSet devset) {
		int numSamples = trainset.size();
		int updateTimes = 1;

		System.out.println("Training Number: " + numSamples);

		float hisErrRate = Float.MAX_VALUE;

		long beginTime, endTime;
		long beginTimeIter, endTimeIter;
		int iter = 0;
		int frac = numSamples / 10;

		float[] averageWeights = null;
		if (method == TrainMethod.Average || method == TrainMethod.FastAverage) {
			averageWeights = new float[weights.length];
		}

		beginTime = System.currentTimeMillis();

		if (shuffle)
			trainset.shuffle();

		while (iter++ < iternum) {
			if (!simpleOutput) {
				System.out.print("iter:");
				System.out.print(iter + "\t");
			}
			float err = 0;
			float errtot = 0;
			int cnt = 0;
			int cnttot = 0;
			int progress = frac;

			beginTimeIter = System.currentTimeMillis();

			for (int ii = 0; ii < numSamples; ii++) {
				Instance inst = trainset.getInstance(ii);
				float l = inst.length();
				float dl = Float.MAX_VALUE;
				do {
					dl = l;
					Predict pred = (Predict) inferencer.getBest(inst);
					l = loss.calc(pred.getLabel(0), inst.getTarget());
					if (l > 0) {
						update.update(inst, weights, assistWeights, updateTimes, 
								pred.getLabel(0), c);
						if (DEBUG) {
							pred = (Predict) inferencer.getBest(inst);
							float nl = loss.calc(pred.getLabel(0), inst.getTarget());
						}
					}
					updateTimes++;
					dl -= l;
				} while (l != 0 && Math.abs(dl) > 0);
				
				cnt += inst.length();
				cnttot++;
				if (l > 0)	{
					err += l;
					errtot++;
				}

				if (!simpleOutput && progress != 0 && ii % progress == 0) {
					System.out.print('.');
					progress += frac;
				}
			}

			float curErrRate = err / cnt;

			endTimeIter = System.currentTimeMillis();

			if (!simpleOutput) {
				System.out.println("\ttime:" + (endTimeIter - beginTimeIter)
						/ 1000.0 + "s");
				System.out.print("Train:");
				System.out.print("\tTag acc:");
			}
			System.out.print(1 - curErrRate);
			if (!simpleOutput) {
				System.out.print("\tSentence acc:");
				System.out.println(1 - errtot / cnttot);
			}
			if (devset != null) {
				evaluate(devset);
			}
			System.out.println();
			
			hisErrRate = curErrRate;
			if (interim) {
				Linear p = new Linear(inferencer, trainset.getAlphabetFactory());
				try {
					p.saveTo("tmp.model");
				} catch (IOException e) {
					System.err.println("write model error!");
				}
			}
		}

		if (method == TrainMethod.Average || method == TrainMethod.FastAverage) {
			for (int i = 0; i < averageWeights.length; i++) {
				averageWeights[i] = weights[i] - assistWeights[i] / updateTimes;
			}
			weights = null;
			weights = averageWeights;
			inferencer.setWeights(weights);
		}

		System.out.print("Weight Numbers: " + MyArrays.countNoneZero(weights));
		if (finalOptimized) {
			int[] idx = MyArrays.getTop(weights.clone(), threshold, false);
			System.out.print("Opt: weight numbers: "
					+ MyArrays.countNoneZero(weights));
			MyArrays.set(weights, idx, 0.0f);
			System.out.println(" -> " + MyArrays.countNoneZero(weights));
		}
		System.out.println();

		endTime = System.currentTimeMillis();
		System.out.println("time escape:" + (endTime - beginTime) / 1000.0
				+ "s");
		Linear p = new Linear(inferencer, trainset.getAlphabetFactory());
		return p;
	}
}