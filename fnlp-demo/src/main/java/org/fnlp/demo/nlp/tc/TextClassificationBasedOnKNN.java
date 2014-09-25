package org.fnlp.demo.nlp.tc;

import gnu.trove.iterator.TIterator;

import java.io.File;
import java.sql.Time;

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
import org.fnlp.ml.feature.FeatureSelect;
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
//	private static String dataPath="C:/dataset/SogouC/";
//	private static String trainDataPath = dataPath+"ClassFile/";
//	private static String dataPath="C:/dataset/SogouC.mini/";
//	private static String trainDataPath = dataPath+"Sample/";
	//private static String dataPath="D:/Documents/dataset/SogouC.reduced/";
	//private static String trainDataPath = dataPath+"Reduced/";
	private static String dataPath="D:/Documents/dataset/SogouC.mini/";
	private static String trainDataPath = dataPath+"Sample/";

	/**
	 * 模型文件
	 */
	private static String knnModelFile = dataPath+"modelKnn.gz";

	public static void main(String[] args) throws Exception {
		//分词
		Pipe removepp=new RemoveWords();
		CWSTagger tag = new CWSTagger("../models/seg.m");
		Pipe segpp=new CNPipe(tag);
		Pipe s2spp=new Strings2StringArray();
		
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();
		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {2,3});
		//将字符特征转换成字典索引;	
		Pipe sparsepp=new StringArray2SV(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());	
		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{removepp,segpp,s2spp,targetpp,sparsepp});

		/**
		 * Knn
		 */
		System.out.print("\nKnn\n");
		System.out.print("\nReading data......\n");
		long time_mark=System.currentTimeMillis();
		InstanceSet instset = new InstanceSet(pp,af);	
		Reader reader = new MyDocumentReader(trainDataPath,"gbk");
		instset.loadThruStagePipes(reader);
		System.out.print("..Reading data complete "+(System.currentTimeMillis()-time_mark)+"(ms)\n");
		
		//将数据集分为训练是和测试集
		System.out.print("Sspliting....");
		float percent = 0.9f;
		InstanceSet[] splitsets = instset.split(percent);
		
		InstanceSet trainset = splitsets[0];
		InstanceSet testset = splitsets[1];	
		System.out.print("..Spliting complete!\n");
		
		System.out.print("Training Knn...\n");
		time_mark=System.currentTimeMillis();
		SparseVectorSimilarity sim=new SparseVectorSimilarity();
		pp.removeTargetPipe();
		KNNClassifier knn=new KNNClassifier(trainset, pp, sim, af, 9);	
		af.setStopIncrement(true);	
		
		ItemFrequency tf=new ItemFrequency(trainset);
		FeatureSelect fs=new FeatureSelect(tf.getFeatureSize());
		long time_train=System.currentTimeMillis()-time_mark;
		
		System.out.print("..Training compelte!\n");
		System.out.print("Saving model...\n");
		knn.saveTo(knnModelFile);	
		knn = null;
		System.out.print("..Saving model compelte!\n");

		
		System.out.print("Loading model...\n");
		knn =KNNClassifier.loadFrom(knnModelFile);
		System.out.print("..Loading model compelte!\n");
		System.out.println("Testing Knn...\n");
		int count=0;
		fs.fS_CS(tf, 0.1f);
		knn.setFs(fs);
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
		System.out.println("Knn Precision:"+((float)knnCount/testset.size())+"("+knnCount+"/"+testset.size()+")");
		knn.noFeatureSelection();
		int flag=0;
		long time_sum=0,time_times=0;
		float[] percents_cs=new float[]{1.0f,0.9f,0.8f,0.7f,0.5f,0.3f,0.2f,0.1f};
		int[] counts_cs=new int[10];
		for(int test=0;test<percents_cs.length;test++){
			long time_st=System.currentTimeMillis();
			System.out.println("Testing Bayes"+percents_cs[test]+"...");
			if(test!=0){
				fs.fS_CS(tf, percents_cs[test]);
				knn.setFs(fs);
			}
			count=0;
			for(int i=0;i<testset.size();i++){
				Instance data = testset.getInstance(i);
				Integer gold = (Integer) data.getTarget();
				Predict<String> pres=(Predict<String>)knn.classify(data, Type.STRING, 3);
				String pred_label=pres.getLabel();
				String gold_label = knn.getLabel(gold);
				
				if(pred_label.equals(gold_label)){
					count++;
				}
				else{
				}
			}
			counts_cs[test]=count;
			long time_ed=System.currentTimeMillis();
			time_sum+=time_ed-time_st;
			time_times++;
			System.out.println("Knn Precision("+percents_cs[test]+"):"
			+((float)count/testset.size())+"("+count+"/"+testset.size()+")"+"  "+(time_ed-time_st)+"ms");
		}
		
		knn.noFeatureSelection();
		float[] percents_csmax=new float[]{1.0f,0.9f,0.8f,0.7f,0.5f,0.3f,0.2f,0.1f};
		int[] counts_csmax=new int[10];
		for(int test=0;test<percents_csmax.length;test++){
			long time_st=System.currentTimeMillis();
			System.out.println("Testing Bayes"+percents_csmax[test]+"...");
			if(test!=0){
				fs.fS_CS_Max(tf, percents_cs[test]);
				knn.setFs(fs);
			}
			count=0;
			for(int i=0;i<testset.size();i++){
				Instance data = testset.getInstance(i);
				Integer gold = (Integer) data.getTarget();
				Predict<String> pres=(Predict<String>)knn.classify(data, Type.STRING, 3);
				String pred_label=pres.getLabel();
				String gold_label = knn.getLabel(gold);
				
				if(pred_label.equals(gold_label)){
					count++;
				}
				else{
				}
			}
			counts_csmax[test]=count;
			long time_ed=System.currentTimeMillis();
			time_sum+=time_ed-time_st;
			time_times++;
			System.out.println("Knn Precision("+percents_csmax[test]+"):"
			+((float)count/testset.size())+"("+count+"/"+testset.size()+")"+"  "+(time_ed-time_st)+"ms");
		}
		knn.noFeatureSelection();
		float[] percents_ig=new float[]{1.0f,0.9f,0.8f,0.7f,0.5f,0.3f,0.2f,0.1f};
		int[] counts_ig=new int[10];
		for(int test=0;test<percents_ig.length;test++){
			long time_st=System.currentTimeMillis();
			System.out.println("Testing Bayes"+percents_ig[test]+"...");
			if(test!=0){
				fs.fS_IG(tf, percents_cs[test]);
				knn.setFs(fs);
			}
			count=0;
			for(int i=0;i<testset.size();i++){
				Instance data = testset.getInstance(i);
				Integer gold = (Integer) data.getTarget();
				Predict<String> pres=(Predict<String>)knn.classify(data, Type.STRING, 3);
				String pred_label=pres.getLabel();
				String gold_label = knn.getLabel(gold);
				
				if(pred_label.equals(gold_label)){
					count++;
				}
				else{
				}
			}
			counts_ig[test]=count;

			long time_ed=System.currentTimeMillis();
			time_sum+=time_ed-time_st;
			time_times++;
			System.out.println("Knn Precision("+percents_ig[test]+"):"
			+((float)count/testset.size())+"("+count+"/"+testset.size()+")"+"  "+(time_ed-time_st)+"ms");
		}
		
		System.out.println("..Testing Bayes complete!");
		for(int i=0;i<percents_cs.length;i++)
			System.out.println("Knn Precision CS("+percents_cs[i]+"):"
		+((float)counts_cs[i]/testset.size())+"("+counts_cs[i]+"/"+testset.size()+")");
		
		for(int i=0;i<percents_csmax.length;i++)
			System.out.println("Knn Precision CS_Max("+percents_csmax[i]+"):"
		+((float)counts_csmax[i]/testset.size())+"("+counts_csmax[i]+"/"+testset.size()+")");
		
		for(int i=0;i<percents_ig.length;i++)
			System.out.println("Knn Precision IG("+percents_ig[i]+"):"
		+((float)counts_ig[i]/testset.size())+"("+counts_ig[i]+"/"+testset.size()+")");

		System.out.println("\nTrain time: "+time_train+"(ms) for "
				+trainset.size()+" train instances\n");
		if(time_times>0)
			System.out.println("Ave Test time: "+time_sum/time_times+"(ms) for "
					+testset.size()+" test instances\n");
	}

}
