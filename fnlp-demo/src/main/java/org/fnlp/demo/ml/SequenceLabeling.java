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

import org.fnlp.data.reader.SequenceReader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.classifier.struct.update.LinearViterbiPAUpdate;
import org.fnlp.ml.loss.struct.HammingLoss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.cn.tag.format.SimpleFormatter;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 序列标注器训练程序
 * 这是{@link org.fnlp.nlp.tag.Tagger}的简化版
 *  
 * @author xpqiu
 * 
 */
public class SequenceLabeling {


	/**
	 * 入口
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		
		String train = "../example-data/sequence/train.txt";
		String testfile = "../example-data/sequence/test.txt";
		String templateFile="../example-data/sequence/template";
		AlphabetFactory factory;
		Pipe featurePipe;
		TempletGroup templets;

		templets = new TempletGroup();
		templets.load(templateFile);
		factory = AlphabetFactory.buildFactory();

		/**
		 * 标签字典。转为0、1、2、...
		 */
		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		/**
		 * 特征字典
		 */
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();
		// 将样本通过Pipe抽取特征
		
		featurePipe = new Sequence2FeatureSequence(templets, features, labels);

		Pipe pipe = new SeriesPipes(new Pipe[] { new Target2Label(labels), featurePipe });


		System.out.print("读入训练数据 ...");
		InstanceSet trainSet = new InstanceSet(pipe, factory);

		// 训练集
		trainSet.loadThruStagePipes(new SequenceReader(train, true, "utf8"));
		System.out.println("训练样本个数 " + trainSet.size());
		System.out.println("标签个数: " + labels.size()); // 
		System.out.println("特征个数" + features.size()); 

		// 冻结特征集
		features.setStopIncrement(true);
		labels.setStopIncrement(true);


		// viterbi解码
		HammingLoss loss = new HammingLoss();
		Inferencer inference = new LinearViterbi(templets, labels.size());
		Update update = new LinearViterbiPAUpdate((LinearViterbi) inference, loss);


		OnlineTrainer trainer = new OnlineTrainer(inference, update, loss,
				factory, 50,0.1f);

		Linear cl = trainer.train(trainSet);


		// test data没有标注
		Pipe tpipe = featurePipe;
		// 测试集
		InstanceSet testSet = new InstanceSet(tpipe);

		testSet.loadThruPipes(new SequenceReader(testfile, false, "utf8"));
		System.out.println("测试样本个数: " + testSet.size()); // 
		String[][] labelsSet = new String[testSet.size()][];
		for (int i = 0; i < testSet.size(); i++) {
			Instance carrier = testSet.get(i);
			int[] pred = (int[]) cl.classify(carrier).getLabel(0);
			labelsSet[i] = labels.lookupString(pred);
		}
		
		String s = SimpleFormatter.format(testSet, labelsSet);
		System.out.println(s);
		System.out.println("Done");
	}

}