package org.fnlp.ml.classifier.knn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.LabelParser;
import org.fnlp.ml.classifier.LinkedPredict;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.similarity.ISimilarity;
import org.fnlp.util.exception.LoadModelException;

public class KNNClassifier extends AbstractClassifier implements Serializable{
	private int k;
	private ISimilarity sim;
	/**
	 * 特征转换器
	 */
	protected Pipe pipe;
	/**
	 * KNN模型
	 */
	protected InstanceSet prototypes;
	private boolean useScore = true; 
	/**
	 * 特征字典
	 */
	protected AlphabetFactory factory;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4598555886865405727L;

	public KNNClassifier(InstanceSet instset, Pipe p, ISimilarity sim, AlphabetFactory factory,int k) {
		prototypes = instset;
		this.pipe = p;
		this.sim = sim;
		this.factory=factory;
		this.k = k;
		int count1 =0,count2=0;
		int total = prototypes.size();
		System.out.println("实例数量："+total);
		for(int i=0;i<total;i++){
			Instance inst = prototypes.get(i);
			TPredict pred = classify(inst, 1);
			if(pred.getLabel(0).equals(inst.getTarget()))
				count1++;
			prototypes.remove(i);
			TPredict pred2 = classify(inst, 1);
			if(pred2.getLabel(0).equals(inst.getTarget()))
				count2++;
			prototypes.add(i, inst);
		}
		System.out.println("Leave-zero-out正确率："+count1*1.0f/total);
		System.out.println("Leave-one-out正确率："+count2*1.0f/total);
		// TODO Auto-generated constructor stub
	}
	public Predict classify(Instance instance, int n){
		VotePredict<Integer> predK=new VotePredict<Integer>(k);

		System.out.println(instance.getSource());
		System.out.println(instance.getData());
		for(int i = 0; i < prototypes.size(); i++){
			Instance curInst = prototypes.get(i);
			float score;
			try {
				score = sim.calc(instance.getData(), curInst.getData());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			predK.add((Integer) curInst.getTarget(), score);
		}
		Predict<Integer> predN=predK.getNLabels(n);
		return predN;
	}	
	@Override
	public TPredict classify(Instance instance, Type type, int n) {
		// TODO Auto-generated method stub
		Predict res = (Predict) classify(instance, n);
		return LabelParser.parse(res,factory.DefaultLabelAlphabet(),type);
	}
	public void saveTo(String file) throws IOException {
		File f = new File(file);
		File path = f.getParentFile();
		if(!path.exists()){
			path.mkdirs();
		}
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new BufferedOutputStream(new FileOutputStream(file))));
		out.writeObject(this);
		out.close();
	}
	/**
	 *  从文件读入分类器
	 * @param file
	 * @return
	 * @throws LoadModelException
	 */
	public static KNNClassifier loadFrom(String file) throws LoadModelException{
		KNNClassifier cl = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(file))));
			cl = (KNNClassifier) in.readObject();
			in.close();
		} catch (Exception e) {
			throw new LoadModelException(e,file);
		}
		return cl;
	}	
	/**
	 * 得到类标签
	 * @param idx 类标签对应的索引
	 * @return
	 */
	public String getLabel(int idx) {
		return factory.DefaultLabelAlphabet().lookupString(idx);
	}
	public ISimilarity getSim() {
		return sim;
	}
	public void setSim(ISimilarity sim) {
		this.sim = sim;
	}
	public void setFactory(AlphabetFactory factory){
		this.factory=factory;
	}
	public AlphabetFactory getFactory(){
		return factory;
	}
} 