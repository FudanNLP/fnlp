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

package org.fnlp.nlp.parser.dep.train;

import java.io.IOException;
import java.util.Arrays;

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.ml.types.alphabet.AlphabetFactory.Type;
import org.fnlp.nlp.parser.dep.YamadaParser;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

public class YamadaOptimization {

	private static void refineModels(Linear[] models, AlphabetFactory factory) {
		LabelAlphabet postags = factory.buildLabelAlphabet("postag");
		int posize = postags.size();
		float[][] nweights = new float[posize][];
		TFloatArrayList[] ww = new TFloatArrayList[posize];
		for (int i = 0; i < posize; i++) {
			nweights[i] = models[i].getWeights();
			ww[i] = new TFloatArrayList();
		}
		int length = nweights[0].length;

		StringFeatureAlphabet features = (StringFeatureAlphabet) factory.DefaultFeatureAlphabet(Type.String);
		TIntObjectHashMap<String> index = new TIntObjectHashMap<String>();
		TObjectIntIterator<String> it = features.iterator();
		while (it.hasNext()) {
			it.advance();
			String value = it.key();
			int key = it.value();
			index.put(key, value);
		}
		int[] idx = index.keys();
		Arrays.sort(idx);

		IFeatureAlphabet nfeatures = factory.rebuildFeatureAlphabet(factory.DefalutFeatureName);

		for (int i = 0; i < idx.length; i++) {
			int base = idx[i];
			int end = length;
			if (i < idx.length - 1)
				end = idx[i + 1];
			boolean del = true;
			for (int l = 0; l < posize; l++) {
				for (int j = base; j < end; j++) {
					if (nweights[l][j] != 0) {
						del = false;
						break;
					}
				}
			}
			int interv = end - base;
			if (!del) {
				String str = index.get(base);
				int id = nfeatures.lookupIndex(str, interv);
				for (int l = 0; l < posize; l++) {
					for (int j = 0; j < interv; j++) {
						ww[l].insert(id + j, nweights[l][base + j]);
					}
				}
			}
		}
		index.clear();
		for(int l = 0; l < posize; l++)	{
			models[l].setWeights(ww[l].toArray());
			ww[l].clear();
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String modelfile = "./models/dep.m";
		YamadaParser parser = new YamadaParser(modelfile);
		AlphabetFactory factory = parser.factory;
		Linear[] models = parser.models;
		refineModels(models,factory);

		ParserTrainer.saveModels(modelfile+1,models,factory);
		System.out.println("Done");
	}

}