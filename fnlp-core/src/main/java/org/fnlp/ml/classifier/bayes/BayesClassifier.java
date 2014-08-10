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
import java.util.ArrayList;
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
import org.junit.Ignore;

public class BayesClassifier extends AbstractClassifier implements Serializable{
	protected AlphabetFactory factory;
	protected ItemFrequency tf;
	protected Pipe pipe;
	protected boolean isUseful[];

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
			int feature=it.key();
			if(isUseful==null||isUseful[feature])
				for(int type=0;type<typeSize;type++){
					float itemF=tf.getItemFrequency(feature, type);
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
	public void fS_CS(float percent){featureSelectionChiSquare(percent);}
	public void featureSelectionChiSquare(float percent){
		int feaSize=tf.getFeatureSize();
		int typeSize=tf.getTypeSize();
		isUseful=new boolean[feaSize];
		Arrays.fill(isUseful, false);
		for(int j=0;j<typeSize;j++){
			Heap<Integer> heap=new Heap<Integer>((int)(feaSize*percent),true);
			for(int i=0;i<feaSize;i++){
				double A,B,C,D,AC,AB,N;
				N=tf.getTotal();
				A=tf.getItemFrequency(i, j);
				AB=tf.getFeatureFrequency(i);
				AC=tf.getTypeFrequency(j);
				B=AB-A;
				C=AC-A;
				D=N-AB-C;
				double score=(A*D-B*C)*(A*D-B*C)/AB/(C+D);
				heap.insert(score, i);
			}
			ArrayList<Integer> data=heap.getData();
			for(int i=1;i<data.size();i++)
				isUseful[data.get(i)]=true;
		}
		int total=0;
		for(int i=0;i<feaSize;i++){
			if(isUseful[i])
				total++;
		}
		System.out.println("Feature Selection"+total+"/"+feaSize);
	}
	public void fS_CS_Max(float percent){featureSelectionChiSquareMax(percent);}
	public void featureSelectionChiSquareMax(float percent){
		int feaSize=tf.getFeatureSize();
		int typeSize=tf.getTypeSize();
		isUseful=new boolean[feaSize];
		Arrays.fill(isUseful, false);
		Heap<Integer> heap=new Heap<Integer>((int)(feaSize*percent),true);
		for(int i=0;i<feaSize;i++){
			double max=0;
			for(int j=0;j<typeSize;j++){
				double A,B,C,D,AC,AB,N;
				N=tf.getTotal();
				A=tf.getItemFrequency(i, j);
				AB=tf.getFeatureFrequency(i);
				AC=tf.getTypeFrequency(j);
				B=AB-A;
				C=AC-A;
				D=N-AB-C;
				double score=(A*D-B*C)*(A*D-B*C)/AB/(C+D)/AC/(B+D);
				if(score>max)
					max=score;
			}

			heap.insert(max, i);
		}
		ArrayList<Integer> data=heap.getData();
		for(int i=1;i<data.size();i++)
			isUseful[data.get(i)]=true;
		int total=0;
		for(int i=0;i<feaSize;i++){
			if(isUseful[i])
				total++;
		}
		System.out.println("Feature Selection"+total+"/"+feaSize);
	}
	public void fS_IG(float percent){featureSelectionInformationGain(percent);}
	public void featureSelectionInformationGain(float percent){
		int feaSize=tf.getFeatureSize();
		int typeSize=tf.getTypeSize();
		isUseful=new boolean[feaSize];
		Arrays.fill(isUseful, false);
		Heap<Integer> heap=new Heap<Integer>((int)(feaSize*percent),true);
		for(int i=0;i<feaSize;i++){
			double ig=0;
			for(int j=0;j<typeSize;j++){
				double A,B,C,D,AC,AB,N;
				N=tf.getTotal();
				A=tf.getItemFrequency(i, j);
				AB=tf.getFeatureFrequency(i);
				AC=tf.getTypeFrequency(j);
				B=AB-A;
				C=AC-A;
				D=N-AB-C;
				ig+=-AC/N*Math.log(AC/N);
				ig+=AB/N*A/N*Math.log(A/N);
				ig+=(1-AB/N)*C/N*Math.log(C/N);;
			}
			heap.insert(ig, i);
		}
		ArrayList<Integer> data=heap.getData();
		for(int i=1;i<data.size();i++)
			isUseful[data.get(i)]=true;
		int total=0;
		for(int i=0;i<feaSize;i++){
			if(isUseful[i])
				total++;
		}
		System.out.println("Feature Selection"+total+"/"+feaSize);
	}
	public void noFeatureSelection(){
		isUseful=null;
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
