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

package org.fnlp.train.tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.data.reader.SequenceReader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.classifier.struct.inf.HigherOrderViterbi;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.classifier.struct.update.HigherOrderViterbiPAUpdate;
import org.fnlp.ml.classifier.struct.update.LinearViterbiPAUpdate;
import org.fnlp.ml.loss.Loss;
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
 * 
 * @author xpqiu
 * 
 */
public class addedTagger {

	Linear cl;
	String train;
	String testfile = null;
	String output = null;
	String templateFile;
	private String model;
	private int iterNum = 50;
	private float c1 = 1;
	private float c2 = 0.1f;
	private boolean useLoss = true;
	private String delimiter = "\\s+|\\t+";
	private boolean interim = false;
	private AlphabetFactory factory;
	private Pipe prePipe;
	private Pipe featurePipe;
	private TempletGroup templets;

	public addedTagger() {
	}

	public void setFile(String templateFile, String train, String model) {
		this.templateFile = templateFile;
		this.train = train;
		this.model = model;
	}
	
	public void setFile(String templateFile, String train, String model, String test) {
		this.templateFile = templateFile;
		this.train = train;
		this.model = model;
		this.testfile = test;
	}
	
	public void setUseLoss(boolean use){
		this.useLoss = use;
	}
	
	public void setIter(int num){
		this.iterNum = num;
	}

	/**
	 * 训练： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger -train template train model 
	 * 测试： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger model test [result]
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("iter", true, "iterative num, default 50");
		opt.addOption("c1", true, "parameters 1, default 1");
		opt.addOption("c2", true, "parameters 2, default 0.1");
		opt.addOption("train", false,
				"switch to training mode(Default: test model");
		opt.addOption("labelwise", false,
				"switch to labelwise mode(Default: viterbi model");
		opt.addOption("margin", false, "use hamming loss as margin threshold");
		opt.addOption("interim", false, "save interim model file");

		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.err.println("Parameters format error");
			return;
		}

		if (args.length == 0 || cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp(
					"Tagger:\n"
							+ "tagger [option] -train templet_file train_file model_file [test_file];\n"
							+ "tagger [option] model_file test_file output_file\n",
							opt);
			return;
		}
		addedTagger tagger = new addedTagger();
		tagger.iterNum = Integer.parseInt(cl.getOptionValue("iter", "50"));
		tagger.c1 = Float.parseFloat(cl.getOptionValue("c1", "1"));
		tagger.c2 = Float.parseFloat(cl.getOptionValue("c2", "0.1"));
		tagger.useLoss = cl.hasOption("margin");
		tagger.interim = cl.hasOption("interim");

		String[] arg = cl.getArgs();
		if (cl.hasOption("train") && arg.length == 3) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			System.out.println("Training model ...");
			tagger.train();
		} else if (cl.hasOption("train") && arg.length == 4) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Training model ...");
			tagger.train();
		} else if (cl.hasOption("train") && arg.length == 5) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Training model ...");
			tagger.train();
			System.gc();
			tagger.output = arg[4];
			tagger.test();
		} else if (arg.length == 3) {
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.output = arg[2];
			tagger.test();
		} else if (arg.length == 2) {
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.test();
		} else {
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}

		System.gc();

	}

	/**
	 * @throws Exception
	 */
	public Pipe createProcessor(boolean flag) throws Exception {
		if (!flag) {
			templets = new TempletGroup();
			templets.load(templateFile);

			// Dictionary d = new Dictionary();
			// d.loadWithWeigth("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt",
			// "AV");
			// templets.add(new DictionaryTemplet(d, gid++, -1, 0));
			// templets.add(new DictionaryTemplet(d, gid++, 0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -1,0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -2,-1,0, 1));
			// templates.add(new CharRangeTemplet(templates.gid++,new
			// int[]{0}));
			// templates.add(new CharRangeTemplet(templates.gid++,new
			// int[]{-1,0}));
			// templates.add(new CharRangeTemplet(templates.gid++,new
			// int[]{-1,0,1}));
		}

		if (cl != null)
			factory = cl.getAlphabetFactory();
		else
			factory = AlphabetFactory.buildFactory();

		/**
		 * 标签转为0、1、2、...
		 */
		LabelAlphabet labels = factory.DefaultLabelAlphabet();

		// 将样本通过Pipe抽取特征
		// 这里用的重建特征，而Label不需要重建
		// 测试时不需要重建特征
		IFeatureAlphabet features = null;
		if(cl != null)
			features = factory.DefaultFeatureAlphabet();
		else
			features = factory.rebuildFeatureAlphabet("feature");
		featurePipe = new Sequence2FeatureSequence(templets, features, labels);

		Pipe pipe = new SeriesPipes(new Pipe[] { prePipe,
				new Target2Label(labels), featurePipe });
		return pipe;
	}

	public void train() throws Exception {

		System.out.print("Loading training data ...");
		long beginTime = System.currentTimeMillis();

		Pipe pipe = createProcessor(false);
		InstanceSet trainSet = new InstanceSet(pipe, factory);

		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		// 训练集
		trainSet.loadThruStagePipes(new SequenceReader(train,true, "utf8"));

		long endTime = System.currentTimeMillis();
		System.out.println(" done!");
		System.out
		.println("Time escape: " + (endTime - beginTime) / 1000 + "s");
		System.out.println();

		// 输出
		System.out.println("Training Number: " + trainSet.size());

		System.out.println("Label Number: " + labels.size()); // 标签个数
		System.out.println("Feature Number: " + features.size()); // 特征个数

		// 冻结特征集
		features.setStopIncrement(true);
		labels.setStopIncrement(true);

		InstanceSet testSet = null;
		// /////////////////
		if (testfile != null) {

			Pipe tpipe;
			if (false) {// 如果test data没有标注
				tpipe = new SeriesPipes(new Pipe[] { featurePipe });
			} else {
				tpipe = pipe;
			}

			// 测试集
			testSet = new InstanceSet(tpipe);

			testSet.loadThruStagePipes(new SequenceReader(testfile, true, "utf8"));
			System.out.println("Test Number: " + testSet.size()); // 样本个数
		}

		/**
		 * 
		 * 更新参数的准则
		 */
		Update update;
		// viterbi解码
		Inferencer inference;
		boolean standard = true;
		HammingLoss loss = new HammingLoss();
		if (standard) {
			inference = new LinearViterbi(templets, labels.size());
			update = new LinearViterbiPAUpdate((LinearViterbi) inference, loss);
		} else {
			inference = new HigherOrderViterbi(templets, labels.size());
			update = new HigherOrderViterbiPAUpdate(templets, labels.size(), true);
		}

		OnlineTrainer trainer = new OnlineTrainer(inference, update, loss,
				factory, iterNum, c1);
		
		trainer.innerOptimized = false;
		trainer.finalOptimized = true;

		cl = trainer.train(trainSet, testSet);

//		ModelAnalysis.removeZero(cl);

		saveTo(model);

	}

	protected void saveTo(String modelfile) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(modelfile))));
		out.writeObject(templets);
		out.writeObject(cl);
		out.close();
	}

	protected void loadFrom(String modelfile) throws IOException,
	ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(modelfile))));
		templets = (TempletGroup) in.readObject();
		cl = (Linear) in.readObject();
		in.close();
	}

	private void test() throws Exception {
		if (cl == null)
			loadFrom(model);

		long starttime = System.currentTimeMillis();
		// 将样本通过Pipe抽取特征
		Pipe pipe = createProcessor(true);

		// 测试集
		InstanceSet testSet = new InstanceSet(pipe);

		testSet.loadThruStagePipes(new SequenceReader(testfile, true, "utf8"));
		System.out.println("Test Number: " + testSet.size()); // 样本个数

		long featuretime = System.currentTimeMillis();

		boolean acc = true;
		double error = 0;
		int senError = 0;
		int len = 0;
		boolean hasENG = false;
		int ENG_all = 0, ENG_right = 0;
		Loss loss = new HammingLoss();

		String[][] labelsSet = new String[testSet.size()][];
		String[][] targetSet = new String[testSet.size()][];
		LabelAlphabet labels = cl.getAlphabetFactory().buildLabelAlphabet(
				"labels");
		for (int i = 0; i < testSet.size(); i++) {
			Instance carrier = testSet.get(i);
			int[] pred = (int[]) cl.classify(carrier).getLabel(0);
			if (acc) {
				len += pred.length;
				double e = loss.calc(carrier.getTarget(), pred);
				error += e;
				if(e != 0)
					senError++;
				//测试中英混杂语料
				if(hasENG) {
					String[][] origin = (String[][])carrier.getSource();
					int[] target = (int[])carrier.getTarget();
					for(int j = 0; j < target.length; j++) {
						if(origin[j][0].contains("ENG")) {
							ENG_all++;
							if(target[j] == pred[j])
								ENG_right++;
						}
					}
				}
			}
			labelsSet[i] = labels.lookupString(pred);
			targetSet[i] = labels.lookupString((int[])carrier.getTarget());
		}

		long endtime = System.currentTimeMillis();
		System.out.println("totaltime\t" + (endtime - starttime) / 1000.0);
		System.out.println("feature\t" + (featuretime - starttime) / 1000.0);
		System.out.println("predict\t" + (endtime - featuretime) / 1000.0);
		
		if (acc) {
			System.out.println("Test Accuracy:\t" + (1 - error / len));
			System.out.println("Sentence Accuracy:\t" + ((double)(testSet.size() - senError) / testSet.size()));
			if(hasENG)
				System.out.println("ENG Accuracy:\t" + ((double)ENG_right / ENG_all));
		}

		if (output != null) {
			BufferedWriter prn = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output), "utf8"));
			String s = SimpleFormatter.format(testSet, labelsSet, targetSet);
			prn.write(s.trim());
			prn.close();
		}
		System.out.println("Done");
	}
	
	public float result() throws Exception{
		if (cl == null)
			loadFrom(model);

		long starttime = System.currentTimeMillis();
		// 将样本通过Pipe抽取特征
		Pipe pipe = createProcessor(true);

		// 测试集
		InstanceSet testSet = new InstanceSet(pipe);

		testSet.loadThruStagePipes(new SequenceReader(testfile, true, "utf8"));
		System.out.println("Test Number: " + testSet.size()); // 样本个数

		long featuretime = System.currentTimeMillis();

		boolean acc = true;
		float error = 0;
		int senError = 0;
		int len = 0;
		boolean hasENG = false;
		int ENG_all = 0, ENG_right = 0;
		Loss loss = new HammingLoss();

		String[][] labelsSet = new String[testSet.size()][];
		String[][] targetSet = new String[testSet.size()][];
		LabelAlphabet labels = cl.getAlphabetFactory().buildLabelAlphabet(
				"labels");
		for (int i = 0; i < testSet.size(); i++) {
			Instance carrier = testSet.get(i);
			int[] pred = (int[]) cl.classify(carrier).getLabel(0);
			if (acc) {
				len += pred.length;
				double e = loss.calc(carrier.getTarget(), pred);
				error += e;
				if(e != 0)
					senError++;
				//测试中英混杂语料
				if(hasENG) {
					String[][] origin = (String[][])carrier.getSource();
					int[] target = (int[])carrier.getTarget();
					for(int j = 0; j < target.length; j++) {
						if(origin[j][0].contains("ENG")) {
							ENG_all++;
							if(target[j] == pred[j])
								ENG_right++;
						}
					}
				}
			}
			labelsSet[i] = labels.lookupString(pred);
			targetSet[i] = labels.lookupString((int[])carrier.getTarget());
		}

		long endtime = System.currentTimeMillis();
		System.out.println("totaltime\t" + (endtime - starttime) / 1000.0);
		System.out.println("feature\t" + (featuretime - starttime) / 1000.0);
		System.out.println("predict\t" + (endtime - featuretime) / 1000.0);
		
		if (acc) {
			System.out.println("Test Accuracy:\t" + (1 - error / len));
			System.out.println("Sentence Accuracy:\t" + ((double)(testSet.size() - senError) / testSet.size()));
			if(hasENG)
				System.out.println("ENG Accuracy:\t" + ((double)ENG_right / ENG_all));
		}

		if (output != null) {
			BufferedWriter prn = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output), "utf8"));
			String s = SimpleFormatter.format(testSet, labelsSet, targetSet);
			prn.write(s.trim());
			prn.close();
		}
		System.out.println("Done");
		return (1 - error / len);
	}

}