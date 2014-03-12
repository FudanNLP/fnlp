package org.fnlp.wsytry;

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

import org.fnlp.corpus.transform.tree.RelationalTree;
import org.fnlp.data.reader.SequenceReader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.classifier.struct.inf.MultiCorpusViterbi;
import org.fnlp.ml.classifier.struct.inf.HigherOrderViterbi;
import org.fnlp.ml.classifier.struct.update.LinearViterbiPAUpdate;
import org.fnlp.ml.classifier.struct.update.MultiCorpusViterbiPAUpdate;
import org.fnlp.ml.classifier.struct.update.HigherOrderViterbiPAUpdate;
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
import org.fnlp.nlp.pipe.seq.AddCharRange;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.SplitDataAndTarget;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 序列标注器训练和测试程序
 * 
 * 未完成!!
 * 
 * @author sywu
 * 
 */

public class MultiCorpusClusterTagger {

	Linear cl;
	String corpus, corpus2, train, train2;
	String testfile = null;
	String output = null;
	String templateFile;
	private String model;
	private int iterNum;
	private float c;
	private boolean useLoss = true;
	private String delimiter = "\\s+|\\t+";
	private boolean interim = false;
	private AlphabetFactory factory;
	private Pipe featurePipe;
	private TempletGroup templets;
	private String newmodel;

	public MultiCorpusClusterTagger() {
	}

	public void setFile(String templateFile, String train, String model) {
		this.templateFile = templateFile;
		this.train = train;
		this.model = model;
	}

	/**
	 * 序列标注训练和测试主程序
	 *
	 * 训练： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger -train template corpus train corpus2 train2 model 
	 * 测试： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger corpus model test [result]
	 * 
	 * corpus为数据集名字比如CTB/PKU等
	 * train为加入了corpus信息的训练文件, 比如 "石 B-NN_CTB"
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("iter", true, "iterative num, default 50");
		opt.addOption("c", true, "parameters C in PA algorithm, default 0.8");
		opt.addOption("train", false,
				"switch to training mode(Default: test model");
		opt.addOption("retrain", false,
				"switch to retraining mode(Default: test model");
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
			MultiCorpusClusterTagger tagger = new MultiCorpusClusterTagger();
			tagger.iterNum = Integer.parseInt(cl.getOptionValue("iter", "50"));
			tagger.c = Float.parseFloat(cl.getOptionValue("c", "0.8"));
			tagger.useLoss = cl.hasOption("margin");
			tagger.interim = cl.hasOption("interim");
			/*
			tagger.corpus = "CTB";
			tagger.model = "./tmp/multi-corpus data/test.mode";
			tagger.testfile = "./tmp/multi-corpus data/ctb_train_short";
			tagger.test();
			//*/
			//*
			tagger.templateFile = "./tmp/multi-corpus data/conlleval.pl";
			tagger.corpus = "CTB";
			tagger.train = "./tmp/multi-corpus data/ctb_train_short";
			tagger.corpus2 = "PKU";
			tagger.train2 = "./tmp/multi-corpus data/pku_train_short";
			tagger.model = "./tmp/multi-corpus data/test.mode";
			//tagger.testfile = "./tmp/multi-corpus data/ctb_train_short";
			System.out.println("Training model ...");
			tagger.train();
			//*/
			/*
			HelpFormatter f = new HelpFormatter();
			f.printHelp(
					"Tagger:\n"
							+ "tagger [option] -train templet_file train_file model_file [test_file];\n"
							+ "tagger [option] -retrain train_file model_file newmodel_file [test_file];\n"
							+ "tagger [option] model_file test_file output_file\n",
							opt);
							*/
			return;
		}
		MultiCorpusClusterTagger tagger = new MultiCorpusClusterTagger();
		tagger.iterNum = Integer.parseInt(cl.getOptionValue("iter", "50"));
		tagger.c = Float.parseFloat(cl.getOptionValue("c", "0.8"));
		tagger.useLoss = cl.hasOption("margin");
		tagger.interim = cl.hasOption("interim");

		String[] arg = cl.getArgs();
		if(cl.hasOption("train") && arg.length == 6) {
			tagger.templateFile = arg[0];
			tagger.corpus = arg[1];
			tagger.train = arg[2];
			tagger.corpus2 = arg[3];
			tagger.train2 = arg[4];
			tagger.model = arg[5];
			System.out.println("Training model ...");
			tagger.train();
		} else if (arg.length == 4) {
			tagger.corpus = arg[0];
			tagger.model = arg[1];
			tagger.testfile = arg[2];
			tagger.output = arg[3];
			tagger.test();
		} else if (arg.length == 3) {
			tagger.corpus = arg[0];
			tagger.model = arg[1];
			tagger.testfile = arg[2];
			tagger.test();
//		} else if (cl.hasOption("train") && arg.length == 3) {
//			tagger.templateFile = arg[0];
//			tagger.train = arg[1];
//			tagger.model = arg[2];
//			System.out.println("Training model ...");
//			tagger.train();
//		} else if (cl.hasOption("train") && arg.length == 4) {
//			tagger.templateFile = arg[0];
//			tagger.train = arg[1];
//			tagger.model = arg[2];
//			tagger.testfile = arg[3];
//			System.out.println("Training model ...");
//			tagger.train();
//		} else if (cl.hasOption("train") && arg.length == 5) {
//			tagger.templateFile = arg[0];
//			tagger.train = arg[1];
//			tagger.model = arg[2];
//			tagger.testfile = arg[3];
//			System.out.println("Training model ...");
//			tagger.train();
//			System.gc();
//			tagger.output = arg[4];
//			tagger.test();
//		} else if (cl.hasOption("retrain") && arg.length == 3) {
//			tagger.train = arg[0];
//			tagger.model = arg[1];
//			tagger.newmodel = arg[2];
//			System.out.println("Re-Training model ...");
//			tagger.train(true);
//		} else if (cl.hasOption("retrain") && arg.length == 4) {
//			tagger.train = arg[0];
//			tagger.model = arg[1];
//			tagger.newmodel = arg[2];
//			tagger.testfile = arg[3];
//			System.out.println("Re-Training model ...");
//			tagger.train(true);
//		} else if (cl.hasOption("retrain") && arg.length == 5) {
//			tagger.train = arg[0];
//			tagger.model = arg[1];
//			tagger.newmodel = arg[2];
//			tagger.testfile = arg[3];
//			System.out.println("Re-Training model ...");
//			tagger.train(true);
//			System.gc();
//			tagger.output = arg[4];
//			tagger.test();
//		} else if (arg.length == 3) {
//			tagger.model = arg[0];
//			tagger.testfile = arg[1];
//			tagger.output = arg[2];
//			tagger.test();
//		} else if (arg.length == 2) {
//			tagger.model = arg[0];
//			tagger.testfile = arg[1];
//			tagger.test();
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
//			templets.load_pro(templateFile);
			
			// Dictionary d = new Dictionary();
			// d.loadWithWeigth("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt",
			// "AV");
			// templets.add(new DictionaryTemplet(d, gid++, -1, 0));
			// templets.add(new DictionaryTemplet(d, gid++, 0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -1,0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -2,-1,0, 1));
//
//			templets.add(new CharRangeTemplet(templets.gid++,new
//					int[]{0}));
//			templets.add(new CharRangeTemplet(templets.gid++,new
//					int[]{-1,0}));
//			templets.add(new CharRangeTemplet(templets.gid++,new
//					int[]{0,1}));
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
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();
		featurePipe = new Sequence2FeatureSequence(templets, features, labels);

		Pipe pipe = new SeriesPipes(new Pipe[] { new Target2Label(labels), featurePipe });
		return pipe;
	}

	public void train() throws Exception{
		train(false);
	}

	/**
	 * 训练
	 * @param b 是否增量训练
	 * @throws Exception
	 */
	public void train(boolean b) throws Exception {

		System.out.print("Loading training data ...\n");
		long beginTime = System.currentTimeMillis();

		if(b)
			loadFrom(model);
		Pipe pipe = createProcessor(b);

		System.out.println("    +Loading templateFile complete ...");

		InstanceSet trainSet = new InstanceSet(pipe, factory);

		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		if(b){
			features.setStopIncrement(false);
			labels.setStopIncrement(false);
		}

		// 训练集
		System.out.println("    +Loading trainSet ...");
		trainSet.loadThruStagePipesForMultiCorpus(
				new SequenceReader[] {new SequenceReader(train, true), new SequenceReader(train2, true)},
				new String[]{corpus, corpus2});
		System.out.println("    +Loading trainSet complete ...");

		long endTime = System.currentTimeMillis();
		System.out.println(" done!");
		System.out.println("Time escape: " + (endTime - beginTime) / 1000 + "s");
		System.out.println();

		// 输出
		System.out.println("Training Number: " + trainSet.size());

		System.out.println("Label Number: " + labels.size()); // 标签个数
		System.out.println("Feature Number: " + features.size()); // 特征个数
		System.out.println();
		
		// 冻结特征集
		features.setStopIncrement(true);
		labels.setStopIncrement(true);
		
		RelationalTree.setIndex(labels);//

		InstanceSet testSet = null;
		// /////////////////
		if (testfile != null) {
			System.out.println("testfile!=null ");
			boolean hasTarget;
			if (false) {// 如果test data没有标注
				hasTarget = false;
				pipe = featurePipe;
			} else {
				hasTarget = true;
			}

			// 测试集
			testSet = new InstanceSet(pipe);

			testSet.loadThruStagePipes(new SequenceReader(testfile, hasTarget, "utf8"));
			System.out.println("Test Number: " + testSet.size()); // 样本个数
		}


		/**
		 * 
		 * 更新参数的准则
		 */
		Update update;
		// viterbi解码
		System.out.println("decoding viterbi: ");
		Inferencer inference;
		boolean standard = true;
		HammingLoss loss = new HammingLoss();
		if (standard) {
			inference = new MultiCorpusViterbi(templets, labels.size(), labels, new String[]{corpus, corpus2});
			update = new MultiCorpusViterbiPAUpdate((MultiCorpusViterbi) inference, loss);
		} else {
			inference = new HigherOrderViterbi(templets, labels.size());
			update = new HigherOrderViterbiPAUpdate(templets, labels.size(), true);
		}

		OnlineTrainer trainer;

		if(b){
			trainer = new OnlineTrainer(cl, update, loss, features.size(),iterNum, c);
		}else{

			trainer = new OnlineTrainer(inference, update, loss, features.size(), iterNum, c);
		}
		trainer.innerOptimized = false;
		trainer.finalOptimized = false;
		
//		for(int i = 0; i < trainSet.size(); i++) {
//			System.out.println(trainSet.get(i).getClasue());
//		}

		cl = trainer.train(trainSet, testSet);

//		ModelAnalysis ma = new ModelAnalysis(cl);
//		ma.removeZero();

		if(b)
			saveTo(newmodel);
		else
			saveTo(model);

	}



	private void test() throws Exception {
		if (cl == null)
			loadFrom(model);

		long starttime = System.currentTimeMillis();
		// 将样本通过Pipe抽取特征
		Pipe pipe = createProcessor(true);

		// 测试集
		InstanceSet testSet = new InstanceSet(pipe);

		if(corpus == null)
			testSet.loadThruStagePipes(new SequenceReader(testfile,true,"utf8"));
		else
			testSet.loadThruStagePipesForMultiCorpus(new SequenceReader[]{new SequenceReader(testfile,true,"utf8")}, new String[]{corpus});
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
		LabelAlphabet labels = cl.getAlphabetFactory().DefaultLabelAlphabet();
		
		RelationalTree.setIndex(labels);//
		
		for (int i = 0; i < testSet.size(); i++) {
			Instance carrier = testSet.get(i);
			int[] pred = (int[]) cl.classify(carrier).getLabel(0);
			if (acc) {
				len += pred.length;
				float e = loss.calc(carrier.getTarget(), pred);
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
}
