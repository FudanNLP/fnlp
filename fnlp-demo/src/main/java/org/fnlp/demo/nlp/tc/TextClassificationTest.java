package org.fnlp.demo.nlp.tc;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.bayes.BayesClassifier;
import org.fnlp.ml.classifier.bayes.BayesTrainer;
import org.fnlp.ml.classifier.knn.KNNClassifier;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.eval.Evaluation;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.NGram;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2IndexArray;
import org.fnlp.nlp.pipe.StringArray2SV;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.similarity.SparseVectorSimilarity;

public class TextClassificationTest {

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
	private static String knnModelFile = dataPath+"modelKnn.gz";
	private static String linearModelFile = dataPath+"modelLinear.gz";
	//private static String modelFile = "D:/Documents/dataset/SogouC.reduced/modelBayes.gz";

	public static void main(String[] args) throws Exception {
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
//				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getTempData());
//				for(int j=0;j<3;j++)
//					System.out.println(pres.getLabel(j)+":"+pres.getScore(j));
			}
		}
		int bayesCount=count;
		System.out.println("..Testing Bayes complete!");
		System.out.println("Bayes Precision:"+((float)bayesCount/testset.size())+"("+bayesCount+"/"+testset.size()+")");


		/**
		 * Knn
		 */
		System.out.print("\nKnn\n");
		//建立字典管理器
		AlphabetFactory af2 = AlphabetFactory.buildFactory();
		//使用n元特征
		ngrampp = new NGram(new int[] {2,3});
		//将字符特征转换成字典索引;	
		sparsepp=new StringArray2SV(af2);
		//将目标值对应的索引号作为类别
		targetpp = new Target2Label(af2.DefaultLabelAlphabet());	
		//建立pipe组合
		pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,sparsepp});

		System.out.print("Init dataset...");
		trainset.setAlphabetFactory(af2);	
		trainset.setPipes(pp);	
		testset.setAlphabetFactory(af2);	
		testset.setPipes(pp);			
		for(int i=0;i<trainset.size();i++){
			Instance inst=trainset.get(i);
			inst.setData(inst.getSource());
			int target_id=Integer.parseInt(inst.getTarget().toString());
			inst.setTarget(af.DefaultLabelAlphabet().lookupString(target_id));
			pp.addThruPipe(inst);
		}		
		for(int i=0;i<testset.size();i++){
			Instance inst=testset.get(i);
			inst.setData(inst.getSource());
			int target_id=Integer.parseInt(inst.getTarget().toString());
			inst.setTarget(af.DefaultLabelAlphabet().lookupString(target_id));
			pp.addThruPipe(inst);
		}

		System.out.print("complete!\n");
		System.out.print("Training Knn...\n");
		SparseVectorSimilarity sim=new SparseVectorSimilarity();
		pp.removeTargetPipe();
		KNNClassifier knn=new KNNClassifier(trainset, pp, sim, af2, 7);	
		af2.setStopIncrement(true);	
		System.out.print("..Training compelte!\n");
		System.out.print("Saving model...\n");
		knn.saveTo(knnModelFile);	
		knn = null;
		System.out.print("..Saving model compelte!\n");

		
		System.out.print("Loading model...\n");
		knn =KNNClassifier.loadFrom(knnModelFile);
		System.out.print("..Loading model compelte!\n");
		System.out.println("Testing Knn...\n");
		count=0;
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			Integer gold = (Integer) data.getTarget();
			Predict<String> pres=(Predict<String>) knn.classify(data, Type.STRING, 3);
			String pred_label=pres.getLabel();
			String gold_label = knn.getLabel(gold);
			
			if(pred_label.equals(gold_label)){
				//System.out.println(pred_label+" : "+testsetknn.getInstance(i).getTempData());
				count++;
			}
			else{
//				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getTempData());
//				for(int j=0;j<3;j++)
//					System.out.println(pres.getLabel(j)+":"+pres.getScore(j));
			}
		}
		int knnCount=count;
		System.out.println("..Testing Knn Complete");
		System.out.println("Bayes Precision:"+((float)bayesCount/testset.size())+"("+bayesCount+"/"+testset.size()+")");
		System.out.println("Knn Precision:"+((float)knnCount/testset.size())+"("+knnCount+"/"+testset.size()+")");
		
		//建立字典管理器
		AlphabetFactory af3 = AlphabetFactory.buildFactory();
		//使用n元特征
		ngrampp = new NGram(new int[] {2,3 });
		//将字符特征转换成字典索引
		Pipe indexpp = new StringArray2IndexArray(af3);
		//将目标值对应的索引号作为类别
		targetpp = new Target2Label(af3.DefaultLabelAlphabet());		
		
		//建立pipe组合
		pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,indexpp});
		
		trainset.setAlphabetFactory(af3);	
		trainset.setPipes(pp);	
		testset.setAlphabetFactory(af3);	
		testset.setPipes(pp);
		for(int i=0;i<trainset.size();i++){
			Instance inst=trainset.get(i);
			inst.setData(inst.getSource());
			int target_id=Integer.parseInt(inst.getTarget().toString());
			inst.setTarget(af.DefaultLabelAlphabet().lookupString(target_id));
			pp.addThruPipe(inst);
		}		
		for(int i=0;i<testset.size();i++){
			Instance inst=testset.get(i);
			inst.setData(inst.getSource());
			int target_id=Integer.parseInt(inst.getTarget().toString());
			inst.setTarget(af.DefaultLabelAlphabet().lookupString(target_id));
			pp.addThruPipe(inst);
		}			
		
		/**
		 * 建立分类器
		 */		
		OnlineTrainer trainer3 = new OnlineTrainer(af3);
		Linear pclassifier = trainer3.train(trainset);
		pp.removeTargetPipe();
		pclassifier.setPipe(pp);
		af.setStopIncrement(true);
		
		//将分类器保存到模型文件
		pclassifier.saveTo(linearModelFile);	
		pclassifier = null;
		
		//从模型文件读入分类器
		Linear cl =Linear.loadFrom(linearModelFile);
		
		//性能评测
		Evaluation eval = new Evaluation(testset);
		eval.eval(cl,1);
		/**
		 * 测试
		 */
		System.out.println("类别 : 文本内容");
		System.out.println("===================");
		count=0;
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			
			Integer gold = (Integer) data.getTarget();
			String pred_label = cl.getStringLabel(data);
			String gold_label = cl.getLabel(gold);
			
			if(pred_label.equals(gold_label)){
				//System.out.println(pred_label+" : "+testsetliner.getInstance(i).getSource());
				count++;
			}
			else{
//				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getTempData());
			}
		}
		int linearCount=count;
		System.out.println("结果");
		System.out.println("labelSize: "+af.getLabelSize());
		System.out.println("instsetSize: "+instset.size());
		System.out.println("Bayes Precision:"+((float)bayesCount/testset.size())+"("+bayesCount+"/"+testset.size()+")");
		System.out.println("Knn Precision:"+((float)knnCount/testset.size())+"("+knnCount+"/"+testset.size()+")");
		System.out.println("Linear Precision:"+((float)linearCount/testset.size())+"("+linearCount+"/"+testset.size()+")");	
	}

}
