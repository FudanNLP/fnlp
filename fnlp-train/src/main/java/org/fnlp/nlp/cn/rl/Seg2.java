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

package org.fnlp.nlp.cn.rl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.fnlp.data.reader.SequenceReader;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.struct.inf.HigherOrderViterbi;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.loss.Loss;
import org.fnlp.ml.loss.struct.HammingLoss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.nlp.cn.tag.format.FormatCWS;
import org.fnlp.nlp.cn.tag.format.SimpleFormatter;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.util.MyArrays;
import org.fnlp.util.MyCollection;
import org.fnlp.util.MyStrings;

import gnu.trove.set.hash.THashSet;

public class Seg2 {

	static CWSTagger seg;
	private static HashSet<String> dict;
	static Linear cl;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		seg = new CWSTagger("./models/seg.m");
		cl = seg.getClassifier();
		int ysize = cl.getAlphabetFactory().getLabelSize();
		LinearViterbi vit = (LinearViterbi) cl.getInferencer();
		System.out.println(cl.getAlphabetFactory().getFeatureSize());
		HigherOrderViterbi inferencer = new HigherOrderViterbi(vit.getTemplets(), ysize);
		inferencer.setWeights(vit.getWeights());
		cl.setInferencer(inferencer);


		dict = MyCollection.loadSet("./data/FNLPDATA/all.dict", true);

		String file = "../iFinder/百度知道20130625.txt";
		BufferedReader bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf8"));
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./tmp/c.txt"), "UTF-8"));
		String line = null;	
		int i=0;
		while ((line = bfr.readLine()) != null) {
			System.out.println(i++);

			if(line.length()==0)
				continue;
			String s = test(line);

			if(s!=null){
				bout.write(s);
				bout.newLine();
			}
			bout.flush();
		}
		bout.close();
		bfr.close();
		System.out.println("Done!");
	}

	private int findoov(String s) {
		String[] ss = s.split("\\s+");
		int oov=0;
		for(String sss:ss){
			if(!dict.contains(sss))
				oov++;
		}
		return oov;
	}

	private static String test(String line) throws Exception{
//		System.out.println(line);

		Instance inst = new Instance(line);
		seg.doProcess(inst);


		Loss loss = new HammingLoss();


		LabelAlphabet la = cl.getAlphabetFactory().DefaultLabelAlphabet();

		Predict xx = cl.classify(inst,5);
		double[] scores=new double[5];
		String[] sentenceout=new String[5];
		int[] pred;
		String[][] labelsSet = new String[xx.size()][];
		ArrayList<String> res = null;
		for(int j=0;j<xx.size();j++){
			pred = (int[]) xx.getLabel(j);
			float uuu = xx.getScore(j);
			labelsSet [j] = la.lookupString(pred);
			res = FormatCWS.toList(inst, labelsSet [j]);
//			System.out.println(res);
		}

		
		double ent =  averageEntropy(labelsSet);
//		System.out.println(ent);
//		System.out.println();
		if(ent==0)
			return MyStrings.toString(res, " ");
		return null;
	}

	public static double averageEntropy(String[][] labelsSet){
		int length=labelsSet.length;
		double sum=0.0;
		for(int i=0;i<labelsSet[0].length;i++){
			int [] tagcount=new int[4];//0-3分别代表B,M,E,S

			for(int j=0;j<labelsSet.length;j++){
				if(labelsSet[j][i].equals("B"))
					tagcount[0]++;
				else if(labelsSet[j][i].equals("M"))
					tagcount[1]++;
				else if(labelsSet[j][i].equals("E"))
					tagcount[2]++;
				else if(labelsSet[j][i].equals("S"))
					tagcount[3]++;
			}

			sum=sum+MyArrays.entropy(tagcount);

		}
		return sum/length;
	}

}