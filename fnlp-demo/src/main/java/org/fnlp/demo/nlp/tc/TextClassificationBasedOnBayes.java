package org.fnlp.demo.nlp.tc;

import gnu.trove.iterator.TIterator;

import java.io.File;

import org.fnlp.data.reader.FileReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.bayes.BayesClassifier;
import org.fnlp.ml.classifier.bayes.BayesTrainer;
import org.fnlp.ml.classifier.bayes.ItemFrequency;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.eval.Evaluation;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.pipe.NGram;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2IndexArray;
import org.fnlp.nlp.pipe.StringArray2SV;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.pipe.nlp.CNPipe;

public class TextClassificationBasedOnBayes {

	/**
	 * 训练数据路径
	 */
	//private static String dataPath="C:/dataset/SogouC/";
	//private static String trainDataPath = dataPath+"ClassFile/";
	//private static String dataPath="C:/dataset/SogouC.mini/";
	//private static String trainDataPath = dataPath+"Sample/";
	private static String dataPath="D:/Documents/dataset/SogouC.mini/";
	private static String trainDataPath = dataPath+"Sample/";

	/**
	 * 模型文件
	 */
	private static String bayesModelFile = dataPath+"modelBayes.gz";

	public static void main(String[] args) throws Exception {
		//分词
//		CWSTagger tag = new CWSTagger("../models/seg.m");
//		Pipe segpp=new CNPipe(tag);
		/**
		 * Bayes
		 */
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();
		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {2,3});
		//将字符特征转换成字典索引;	
		Pipe sparsepp=new StringArray2SV(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());	
		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,sparsepp});

		System.out.print("\nReading data......\n");
		InstanceSet instset = new InstanceSet(pp,af);	
		Reader reader = new MyDocumentReader(trainDataPath,"gbk");
		instset.loadThruStagePipes(reader);
		System.out.print("..Reading data complete\n");
		
		//将数据集分为训练是和测试集
		System.out.print("Sspliting....");
		float percent = 0.9f;
		InstanceSet[] splitsets = instset.split(percent);
		
		InstanceSet trainset = splitsets[0];
		InstanceSet testset = splitsets[1];	
		System.out.print("..Spliting complete!\n");

		System.out.print("Training...\n");
		BayesTrainer trainer=new BayesTrainer();
		BayesClassifier classifier= (BayesClassifier) trainer.train(trainset);
		pp.removeTargetPipe();
		classifier.setPipe(pp);
		af.setStopIncrement(true);
		System.out.print("..Training complete!\n");
		System.out.print("Saving model...\n");
		classifier.saveTo(bayesModelFile);	
		classifier = null;
		System.out.print("..Saving model complete!\n");
		/**
		 * 测试
		 */
		System.out.print("Loading model...\n");
		BayesClassifier bayes;
		bayes =BayesClassifier.loadFrom(bayesModelFile);
//		bayes =classifier;
		System.out.print("..Loading model complete!\n");
		
		System.out.println("Testing Bayes...");
		int count=0;
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			Integer gold = (Integer) data.getTarget();
			Predict<String> pres=bayes.classify(data, Type.STRING, 3);
			String pred_label=pres.getLabel();
//			String pred_label = bayes.getStringLabel(data);
			String gold_label = bayes.getLabel(gold);
			
			if(pred_label.equals(gold_label)){
				//System.out.println(pred_label+" : "+testsetbayes.getInstance(i).getTempData());
				count++;
			}
			else{
				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getTempData());
				for(int j=0;j<3;j++)
					System.out.println(pres.getLabel(j)+":"+pres.getScore(j));
			}
		}
		int bayesCount=count;
		System.out.println("..Testing Bayes complete!");
		System.out.println("Bayes Precision:"+((float)bayesCount/testset.size())+"("+bayesCount+"/"+testset.size()+")");

	}

}
