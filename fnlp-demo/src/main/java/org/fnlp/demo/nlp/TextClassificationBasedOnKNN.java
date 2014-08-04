package org.fnlp.demo.nlp;

import gnu.trove.iterator.TIterator;

import java.io.File;

import org.fnlp.data.reader.FileReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.bayes.BayesClassifier;
import org.fnlp.ml.classifier.bayes.BayesTrainer;
import org.fnlp.ml.classifier.bayes.ItemFrequency;
import org.fnlp.ml.classifier.knn.KNN;
import org.fnlp.ml.classifier.knn.KNNClassifier;
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
import org.fnlp.nlp.similarity.SparseVectorSimilarity;

public class TextClassificationBasedOnKNN {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "../example-data/text-classification/";

	/**
	 * 模型文件
	 */
	private static String modelFile = "../example-data/text-classification/modelKNN.gz";

	public static void main(String[] args) throws Exception {
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
		
		System.out.println("\n=======================Message 1========================");
		System.out.println("featureSize: "+af.getFeatureSize());
		System.out.println("labelSize: "+af.getLabelSize());
		System.out.println("instsetSize: "+instset.size());

		pp.removeTargetPipe();
		af.setStopIncrement(true);
		
		SparseVectorSimilarity sim=new SparseVectorSimilarity();
		KNNClassifier knn=new KNNClassifier(trainset, pp, sim, af, 7);		

//		knn.saveTo(modelFile);	
//		knn = null;
//
//		knn =KNNClassifier.loadFrom(modelFile);
		
		/**
		 * 测试
		 */
		System.out.println("类别 : 文本内容");
		System.out.println("===================");
		int count=0;
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			
			Integer gold = (Integer) data.getTarget();
			Predict<String> pres=(Predict<String>) knn.classify(data, Type.STRING, 3);
			String pred_label=pres.getLabel();
			String gold_label = knn.getLabel(gold);
			
			if(pred_label.equals(gold_label)){
				System.out.println(pred_label+" : "+testset.getInstance(i).getSource());
				count++;
			}
			else
				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getSource());
			for(int j=0;j<3;j++)
				System.out.println(pres.getLabel(j)+":"+pres.getScore(j));
		}
		System.out.println("Precision:"+((float)count/testset.size())+"("+count+"/"+testset.size()+")");


	}

}
