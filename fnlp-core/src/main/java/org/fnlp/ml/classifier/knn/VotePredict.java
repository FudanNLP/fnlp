package org.fnlp.ml.classifier.knn;

import java.util.HashMap;
import java.util.Map;

import org.fnlp.ml.classifier.Predict;

public class VotePredict<T> extends Predict<T>  {
	
	public VotePredict(int k){
		super(k);
	}
	public T getLabel() {
		T label=labels[0];
		int count=0;
		Map<T, Integer> labelCount = new HashMap<T, Integer>();
		for(int pos=0;pos<n;pos++){
			if(labels[pos]==null)
				continue;
			int tempCount;
			if(labelCount.containsKey(labels[pos]))
				tempCount=labelCount.get(labels[pos]);			
			else 
				tempCount=0;
			
			tempCount++;
			labelCount.put(labels[pos], tempCount);
			if(tempCount>count){
				count=tempCount;
				label=labels[pos];
			}
		}
		return label;
	}
	public Predict getNLabels(int labels_num){
		Predict<T> pred=new Predict<T>(labels_num);
		
		Map<T, Integer> labelCount = new HashMap<T, Integer>();
		for(int i=0;i<n;i++){
			if(labels[i]==null)
				continue;
			int tempCount;
			if(labelCount.containsKey(labels[i]))
				tempCount=labelCount.get(labels[i]);			
			else 
				tempCount=0;
			
			tempCount++;
			labelCount.put(labels[i], tempCount);
		}

		for(int i=0;i<n;i++){
			if(labelCount.containsKey(labels[i])){
				pred.add(labels[i], labelCount.get(labels[i]));
				labelCount.remove(labels[i]);
			}
		}
		return pred;
	}
}
