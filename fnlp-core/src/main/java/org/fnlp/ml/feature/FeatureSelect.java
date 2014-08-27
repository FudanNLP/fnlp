package org.fnlp.ml.feature;

import gnu.trove.iterator.TIntFloatIterator;

import java.util.ArrayList;
import java.util.Arrays;

import org.fnlp.ml.classifier.bayes.Heap;
import org.fnlp.ml.classifier.bayes.ItemFrequency;
import org.fnlp.ml.types.sv.HashSparseVector;
/**
 * 特征选择 保存选择后的特征 
 * @author sywu
 *
 */
public class FeatureSelect {
	private boolean isUseful[];
	int size;
	public FeatureSelect(int size){
		this.size=size;
		isUseful=new boolean[size];
		Arrays.fill(isUseful, true);
	}
	/**
	 * 特征选择 卡方法 保留特征为每个类别前percent的最大卡方值特征的并集
	 * @param tf 词频类
	 * @param percent 保留百分比
	 */
	public void fS_CS(ItemFrequency tf,float percent){featureSelectionChiSquare(tf,percent);}
	public void featureSelectionChiSquare(ItemFrequency tf,float percent){
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
	/**
	 * 特征选择 卡方法 
	 * 对特征对每个类别求卡方，保留最大项，保留结果为前percent的特征
	 * @param tf 词频类
	 * @param percent 保留百分比
	 */
	public void fS_CS_Max(ItemFrequency tf,float percent){featureSelectionChiSquareMax(tf,percent);}
	public void featureSelectionChiSquareMax(ItemFrequency tf,float percent){
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
	/**
	 * 特征选择 信息增益 
	 * @param tf 词频类
	 * @param percent 保留百分比
	 */
	public void fS_IG(ItemFrequency tf,float percent){featureSelectionInformationGain(tf,percent);}
	public void featureSelectionInformationGain(ItemFrequency tf,float percent){
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
		Arrays.fill(isUseful, true);
	}
	public HashSparseVector select(HashSparseVector vec){
		HashSparseVector sv=new HashSparseVector();		
		TIntFloatIterator it=vec.data.iterator();
		while(it.hasNext()){
			it.advance();
			if(isUseful[it.key()])
				sv.put(it.key(), it.value());
		}
		return sv;
	}
}
