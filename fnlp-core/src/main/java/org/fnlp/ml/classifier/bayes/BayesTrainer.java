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
			Instance inst=trainset.get(i);
			oneStepCount(inst,tf);
		}
		BayesClassifier classifier=new BayesClassifier();
		classifier.setTf(tf);
		classifier.setFactory(af);
		return classifier;
	}
	private boolean oneStepCount(Instance inst,ItemFrequency tf) {
		if(inst==null)
			return false;
		int[] type;
		Object t=inst.getTarget();
		if(t instanceof Integer){
			type=new int[1];
			type[0]=Integer.parseInt(t.toString());
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
