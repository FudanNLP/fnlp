package org.fnlp.demo.nlp.tc;

import gnu.trove.iterator.TIterator;

import java.io.File;

import org.fnlp.data.reader.FileReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.Predict;
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

public class TextClassificationBasedOnBayes2 {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "../example-data/text-classification/";

	/**
	 * 模型文件
	 */
	private static String modelFile = "../example-data/text-classification/modelBayes2.gz";

	public static void main(String[] args) throws Exception {
		
		BayesClassifier bayes;
		bayes =BayesClassifier.loadFrom(modelFile);
		
		/**
		 * 分类器使用
		 */
		String str = "韦德：不拿冠军就是失败 詹皇：没拿也不意味失败";
		System.out.println("============\n分类："+ str);
		Pipe p = bayes.getPipe();
		Instance inst = new Instance(str);
		try {
			//特征转换
			p.addThruPipe(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = bayes.getStringLabel(inst);
		System.out.println("xxx");	
		System.out.println("类别："+ res);	
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();
		
		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {1,2});
		//分词
//		CWSTagger tag = new CWSTagger("../models/seg.m");
//		Pipe segpp=new CNPipe(tag);
		//将字符特征转换成字典索引
		Pipe indexpp = new StringArray2IndexArray(af);	
		Pipe sparsepp=new StringArray2SV(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());	
		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,sparsepp});
		
		InstanceSet instset = new InstanceSet(pp,af);
		
		//用不同的Reader读取相应格式的文件
		Reader reader = new FileReader(trainDataPath,"UTF-8",".data");
		
		//读入数据，并进行数据处理
		instset.loadThruStagePipes(reader);
		//将数据集分为训练是和测试集
		float percent = 0.8f;
		InstanceSet[] splitsets = instset.split(percent);
		
		InstanceSet trainset = splitsets[0];
		InstanceSet testset = splitsets[1];	

		/**
		 * 测试
		 */
		System.out.println("类别 : 文本内容");
		System.out.println("===================");
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			
			Integer gold = (Integer) data.getTarget();
			Predict<String> pres=bayes.classify(data, Type.STRING, 3);
			String pred_label=pres.getLabel();
//			String pred_label = bayes.getStringLabel(data);
			String gold_label = bayes.getLabel(gold);
			
			if(pred_label.equals(gold_label))
				System.out.println(pred_label+" : "+testset.getInstance(i).getSource());
			else
				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getSource());
			for(int j=0;j<3;j++)
				System.out.println(pres.getLabel(j)+":"+pres.getScore(j));
		}
	}

}
