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

package org.fnlp.demo.ml;


import java.io.File;

import org.fnlp.data.reader.SimpleFileReader;
import org.fnlp.ml.classifier.hier.Linear;
import org.fnlp.ml.classifier.hier.PATrainer;
import org.fnlp.ml.classifier.hier.inf.MultiLinearMax;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.feature.BaseGenerator;
import org.fnlp.ml.loss.ZeroOneLoss;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2SV;
import org.fnlp.nlp.pipe.Target2Label;

/**
 * 层次分类器使用示例
 * 
 * @author xpqiu
 * 
 */
public class HierClassifierUsage2 {
	static InstanceSet train;
	static InstanceSet test;
	static AlphabetFactory factory = AlphabetFactory.buildFactory();
	static LabelAlphabet al = factory.DefaultLabelAlphabet();
	static IFeatureAlphabet af = factory.DefaultFeatureAlphabet();
	static String path = null;

	public static void main(String[] args) throws Exception {

		
		long start = System.currentTimeMillis();

		path = "./example-data/data-classification.txt";

		Pipe lpipe = new Target2Label(al);
		Pipe fpipe = new StringArray2SV(factory, true);
		//构造转换器组
		Pipe pipe = new SeriesPipes(new Pipe[]{lpipe,fpipe});
		
		//构建训练集
		train = new InstanceSet(pipe, factory);
		SimpleFileReader reader = new SimpleFileReader (path,true);
		train.loadThruStagePipes(reader);
		al.setStopIncrement(true);
		
		//构建测试集
		test = new InstanceSet(pipe, factory);		
		reader = new SimpleFileReader (path,true);
		test.loadThruStagePipes(reader);	

		System.out.println("Train Number: " + train.size());
		System.out.println("Test Number: " + test.size());
		System.out.println("Class Number: " + al.size());

		float c = 1.0f;
		int round = 20;
		
		BaseGenerator featureGen = new BaseGenerator();
		ZeroOneLoss loss = new ZeroOneLoss();
		Inferencer msolver = new MultiLinearMax(featureGen, al, null,2);

		PATrainer trainer = new PATrainer(msolver, featureGen, loss, round,c, null);
		Linear pclassifier = trainer.train(train, null);
		
		String modelFile = path+".m.gz";
		pclassifier.saveTo(modelFile);

		long end = System.currentTimeMillis();
		System.out.println("Total Time: " + (end - start));
		System.out.println("End!");
		(new File(modelFile)).deleteOnExit();
		System.exit(0);
	}
}