package org.fnlp.train.tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
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
import org.fnlp.ml.loss.struct.HammingLoss;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.HashFeatureAlphabet;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.alphabet.AlphabetFactory.Type;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.templet.BaseTemplet;
import org.fnlp.nlp.pipe.seq.templet.CharClassTemplet;
import org.fnlp.nlp.pipe.seq.templet.CharInStringTemplet;
import org.fnlp.nlp.pipe.seq.templet.StringTypeTemplet;
import org.fnlp.nlp.pipe.seq.templet.Templet;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
import org.fnlp.ontology.CharClassDictionary;

/**
 * 序列标注器训练和测试程序
 * 
 * @author xpqiu
 * 
 */
public class POSTrain {

	Linear cl;
	String train;
	String testfile = null;
	String output = null;
	String templateFile;
	private String model;
	private int iterNum;
	private float c;
	private AlphabetFactory factory;
	private Pipe featurePipe;
	private TempletGroup templets;

	public POSTrain() {
	}

	public void setFile(String templateFile, String train, String model) {
		this.templateFile = templateFile;
		this.train = train;
		this.model = model;
	}

	/**
	 * 序列标注训练和测试主程序
	 * 训练： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger -train template train model 
	 * 测试： java -classpath fudannlp.jar edu.fudan.nlp.tag.Tagger model test [result]
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options opt = new Options();

		opt.addOption("iter", true, "iterative num, default 50");
		opt.addOption("c", true, "parameters C in PA algorithm, default 0.8");


		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.err.println("Parameters format error");
			return;
		}


		POSTrain tagger = new POSTrain();
		tagger.iterNum = Integer.parseInt(cl.getOptionValue("iter", "50"));
		tagger.c = Float.parseFloat(cl.getOptionValue("c", "0.8"));

		String[] arg = cl.getArgs();

		tagger.templateFile = arg[0];
		tagger.train = arg[1];
		tagger.model = arg[2];
		System.out.println("Training model ...");
		tagger.train();


		System.gc();

	}



	/**
	 * @throws Exception
	 */
	public Pipe createProcessor() throws Exception {

		templets = new TempletGroup();
		templets.load(templateFile);
		for(Templet templet:templets){
			((BaseTemplet) templet).minLen = 5;
		}
		// Dictionary d = new Dictionary();
		// d.loadWithWeigth("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt",
		// "AV");
		// templets.add(new DictionaryTemplet(d, gid++, -1, 0));
		// templets.add(new DictionaryTemplet(d, gid++, 0, 1));
		// templets.add(new DictionaryTemplet(d, gid++, -1,0, 1));
		// templets.add(new DictionaryTemplet(d, gid++, -2,-1,0, 1));

		templets.add(new CharInStringTemplet(templets.gid++,-1,1));
		templets.add(new CharInStringTemplet(templets.gid++,0,1));
		templets.add(new CharInStringTemplet(templets.gid++,-2,2));
		templets.add(new StringTypeTemplet(templets.gid++));

		CharClassDictionary dsurname = new CharClassDictionary();
		dsurname.load("../data/knowledge/百家姓.txt", "姓");
		templets.add(new CharClassTemplet(templets.gid++, new CharClassDictionary[]{dsurname}));

		if (cl != null)
			factory = cl.getAlphabetFactory();
		else
			factory = AlphabetFactory.buildFactory();
		
		//TODO: 修改字典类型
		AlphabetFactory.defaultFeatureType = Type.String;
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

	/**
	 * 训练
	 * @throws Exception
	 */
	public void train() throws Exception {

		System.out.print("Loading training data ...");
		long beginTime = System.currentTimeMillis();

		Pipe pipe = createProcessor();

		InstanceSet trainSet = new InstanceSet(pipe, factory);

		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		// 训练集
		trainSet.loadThruStagePipes(new SequenceReader(train, true));

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
		
		if(features instanceof HashFeatureAlphabet)
			((HashFeatureAlphabet) features).countConflict();

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

		OnlineTrainer trainer;


		trainer = new OnlineTrainer(inference, update, loss,
				features.size(), iterNum, c);

		trainer.innerOptimized = false;
		trainer.finalOptimized = false;

		cl = trainer.train(trainSet, null);

		//		ModelAnalysis ma = new ModelAnalysis(cl);
		//		ma.removeZero();

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
}
