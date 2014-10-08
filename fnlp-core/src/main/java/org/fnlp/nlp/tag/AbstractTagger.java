package org.fnlp.nlp.tag;

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
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

public abstract class AbstractTagger {
	
	public Linear cl;
	public String train;
	public String testfile = null;
	public String output = null;
	public String templateFile;
	public static boolean standard = true;
	public String model;
	public int iterNum;
	public float c;
	public boolean useLoss = true;
	public String delimiter = "\\s+|\\t+";
	public boolean interim = false;
	public AlphabetFactory factory;
	public Pipe featurePipe;
	public TempletGroup templets;
	public String newmodel;
	public boolean hasLabel;
	protected LabelAlphabet labels;
	protected IFeatureAlphabet features;
	
	protected InstanceSet trainSet =null;
	protected InstanceSet testSet = null;
	
	
	public void setFile(String templateFile, String train, String model) {
		this.templateFile = templateFile;
		this.train = train;
		this.model = model;
	}
	
	/**
	 * 训练
	 * @param b 是否增量训练
	 * @throws Exception
	 */
	public void train() throws Exception {
		
		loadTrainingData();
		/**
		 * 
		 * 更新参数的准则
		 */
		Update update;
		// viterbi解码
		Inferencer inference;

		HammingLoss loss = new HammingLoss();
		if (standard) {
			inference = new LinearViterbi(templets, labels.size());
			update = new LinearViterbiPAUpdate((LinearViterbi) inference, loss);
		} else {
			inference = new HigherOrderViterbi(templets, labels.size());
			update = new HigherOrderViterbiPAUpdate(templets, labels.size(), true);
		}

		OnlineTrainer trainer;

		if(cl!=null){
			trainer = new OnlineTrainer(cl, update, loss, features.size(),iterNum, c);
		}else{
			trainer = new OnlineTrainer(inference, update, loss,
					features.size(), iterNum, c);
		}

		cl = trainer.train(trainSet, testSet);

		if(cl!=null&&newmodel!=null)
			saveTo(newmodel);
		else
			saveTo(model);

	}



	public void test() throws Exception {
		if (cl == null)
			loadFrom(model);

		long starttime = System.currentTimeMillis();
		// 将样本通过Pipe抽取特征
		Pipe pipe = createProcessor();

		// 测试集
		testSet = new InstanceSet(pipe);

		testSet.loadThruStagePipes(new SequenceReader(testfile,hasLabel,"utf8"));
		System.out.println("测试样本个数：\t" + testSet.size()); // 样本个数

		long featuretime = System.currentTimeMillis();


		float error = 0;
		int senError = 0;
		int len = 0;
		Loss loss = new HammingLoss();

		String[][] predictSet = new String[testSet.size()][];
		String[][] goldSet = new String[testSet.size()][];
		LabelAlphabet la = cl.getAlphabetFactory().DefaultLabelAlphabet();
		for (int i = 0; i < testSet.size(); i++) {
			Instance carrier = testSet.get(i);
			int[] pred = (int[]) cl.classify(carrier).getLabel(0);
			if (hasLabel) {
				len += pred.length;
				float e = loss.calc(carrier.getTarget(), pred);
				error += e;
				if(e != 0)
					senError++;

			}
			predictSet[i] = la.lookupString(pred);
			if(hasLabel)
				goldSet[i] = la.lookupString((int[])carrier.getTarget());
		}

		long endtime = System.currentTimeMillis();
		System.out.println("总时间：\t" + (endtime - starttime) / 1000.0);
		System.out.println("抽取特征时间：\t" + (featuretime - starttime) / 1000.0);
		System.out.println("分类时间：\t" + (endtime - featuretime) / 1000.0);

		if (hasLabel) {
			System.out.println("Test Accuracy:\t" + (1 - error / len));
			System.out.println("Sentence Accuracy:\t" + ((double)(testSet.size() - senError) / testSet.size()));
		}

		if (output != null) {
			BufferedWriter prn = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output), "utf8"));
			String s; 
			if(hasLabel)
				s = SimpleFormatter.format(testSet, predictSet, goldSet);
			else
				s = SimpleFormatter.format(testSet, predictSet);
			prn.write(s.trim());
			prn.close();
		}
		System.out.println("Done");
	}

	public void saveTo(String modelfile) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(modelfile))));
		out.writeObject(templets);
		out.writeObject(cl);
		out.close();
	}

	public void loadFrom(String modelfile) throws IOException,
	ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(modelfile))));
		templets = (TempletGroup) in.readObject();
		cl = (Linear) in.readObject();
		in.close();
	}
	
	

	public void loadTrainingData() throws Exception{

		System.out.print("Loading training data ...");
		long beginTime = System.currentTimeMillis();

		Pipe pipe = createProcessor();

		
		trainSet = new InstanceSet(pipe, factory);
		 labels = factory.DefaultLabelAlphabet();
		features = factory.DefaultFeatureAlphabet();
		features.setStopIncrement(false);
		labels.setStopIncrement(false);

		// 训练集
		trainSet.loadThruStagePipes(new SequenceReader(train, true));

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

	}
	
	
	public void loadTestData() throws Exception{
		System.out.print("Loading test data ...");
		Pipe pipe = createProcessor();
		
		
		// /////////////////
		if (testfile != null) {
			boolean hasTarget = true;;
			if (!hasTarget) {// 如果test data没有标注
				pipe = featurePipe;
			} 

			// 测试集
			testSet = new InstanceSet(pipe);
			testSet.loadThruStagePipes(new SequenceReader(testfile, hasTarget, "utf8"));
			System.out.println("Test Number: " + testSet.size()); // 样本个数
		}
	}

	abstract public  Pipe createProcessor() throws Exception;
}
