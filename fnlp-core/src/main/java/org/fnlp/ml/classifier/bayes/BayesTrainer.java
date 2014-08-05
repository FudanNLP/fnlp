package org.fnlp.ml.classifier.bayes;

import gnu.trove.iterator.TIntFloatIterator;

import java.util.List;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.linear.AbstractTrainer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.sv.HashSparseVector;

public class BayesTrainer{

	public AbstractClassifier train(InstanceSet trainset) {
		AlphabetFactory af=trainset.getAlphabetFactory();
		ItemFrequency tf=new ItemFrequency(af.getFeatureSize(),af.getLabelSize());
		int numSamples = trainset.size();
		for(int i=0;i<numSamples;i++){
			System.out.println(i);
			Instance inst=trainset.get(i);
			oneStepCount(inst,tf);
		}
		System.out.println("tf.featureSize="+tf.getFeatureSize());
		for(int i=0;i<100;i++)
			System.out.print(i+","+tf.getFeatureFrequency(i)+(i%10==0?"\n":" "));
		BayesClassifier classifier=new BayesClassifier();
		classifier.setTf(tf);
		classifier.setFactory(af);
		return classifier;
	}
	private boolean oneStepCount(Instance inst,ItemFrequency tf) {
		if(inst==null)
			return false;
		System.out.println(inst.getSource());
		int[] type;
		Object t=inst.getTarget();
		if(t instanceof Integer){
			type=new int[1];
			type[0]=Integer.parseInt(t.toString());
			System.out.println(type[0]);
		}
		else{
			return false;
		}
		
		HashSparseVector data = (HashSparseVector) inst.getData();
		TIntFloatIterator it = data.data.iterator();
		while (it.hasNext()) {
			it.advance();
			int feature=it.key();
			for(int i=0;i<type.length;i++){
				tf.addItemFrequency(feature, type[i], (int)it.value());
			}
		}
		
		return true;
	}
}
