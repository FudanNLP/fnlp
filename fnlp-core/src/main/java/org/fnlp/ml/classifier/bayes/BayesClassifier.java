package org.fnlp.ml.classifier.bayes;

import gnu.trove.iterator.TIntFloatIterator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.LabelParser;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.util.exception.LoadModelException;

public class BayesClassifier extends AbstractClassifier implements Serializable{
	protected AlphabetFactory factory;
	protected ItemFrequency tf;
	protected Pipe pipe;

	@Override
	public Predict classify(Instance instance, int n) {
		// TODO Auto-generated method stub

		int typeSize=tf.getTypeSize();
		float[] score=new float[typeSize];
		Arrays.fill(score, 0.0f);
		
		Object obj=instance.getData();
		if(!(obj instanceof HashSparseVector)){
			System.out.println("error 输入类型非HashSparseVector！");
			return null;
		}
		HashSparseVector data = (HashSparseVector) obj;
		TIntFloatIterator it = data.data.iterator();
		float feaSize=tf.getFeatureSize();
		while (it.hasNext()) {
			it.advance();
			if(it.key()==0)
				continue;
			for(int type=0;type<typeSize;type++){
				//System.out.println(score[type]+" "+tf.getItemFrequency(it.key(), type)+" "+tf.getTypeFrequency(type)+" "+it.value());
				//score[type]+=Math.log(Math.pow((tf.getItemFrequency(it.key(), type)+1.0)/(tf.getTypeFrequency(type)+featureSize),it.value()));
				float itemF=tf.getItemFrequency(it.key(), type);
				float typeF=tf.getTypeFrequency(type);
				score[type]+=it.value()*Math.log((itemF+1.0)/(typeF+feaSize));
			}
		}
		
		Predict<Integer> res=new Predict<Integer>(n);
		for(int type=0;type<typeSize;type++)
			res.add(type, score[type]);
		
		return res;
	}

	@Override
	public Predict classify(Instance instance, Type type, int n) {
		// TODO Auto-generated method stub
		Predict res = (Predict) classify(instance, n);
		return LabelParser.parse(res,factory.DefaultLabelAlphabet(),type);
	}
	/**
	 * 得到类标签
	 * @param idx 类标签对应的索引
	 * @return
	 */
	public String getLabel(int idx) {
		return factory.DefaultLabelAlphabet().lookupString(idx);
	}

	/**
	 * 将分类器保存到文件
	 * @param file
	 * @throws IOException
	 */
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
	public static BayesClassifier loadFrom(String file) throws LoadModelException{
		BayesClassifier cl = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(file))));
			cl = (BayesClassifier) in.readObject();
			in.close();
		} catch (Exception e) {
			throw new LoadModelException(e,file);
		}
		return cl;
	}
	public ItemFrequency getTf() {
		return tf;
	}

	public void setTf(ItemFrequency tf) {
		this.tf = tf;
	}
	public Pipe getPipe() {
		return pipe;
	}

	public void setPipe(Pipe pipe) {
		this.pipe = pipe;
	}

	public void setFactory(AlphabetFactory factory){
		this.factory=factory;
	}
	public AlphabetFactory getFactory(){
		return factory;
	}
}
