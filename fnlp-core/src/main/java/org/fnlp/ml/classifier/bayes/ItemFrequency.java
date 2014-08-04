package org.fnlp.ml.classifier.bayes;

import java.io.Serializable;

/**
 * 特征频数统计结构 
 * 用于统计特征、类别、特征-类别的出现频数，可以用于词频统计
 * @author sywu
 * @version 1.0
 */
public final class ItemFrequency implements Serializable{
	private int size,featureSize,typeSize;
	private int[] itemFrequency;
	private int[] typeFrequency;
	private int[] featureFrequency;
	
	public ItemFrequency(int featureSize,int typeSize){
		this.setFeatureSize(featureSize);
		this.setTypeSize(typeSize);
		size=featureSize*(typeSize);
		itemFrequency=new int[size];
		typeFrequency=new int[typeSize];
		featureFrequency=new int[featureSize];
	}
	public int getTypeSize() {
		return typeSize;
	}
	public void setTypeSize(int typeSize) {
		this.typeSize = typeSize;
	}
	public int getFeatureSize() {
		return featureSize;
	}
	public void setFeatureSize(int featureSize) {
		this.featureSize = featureSize;
	}


	public int getFeatureFrequency(int index){
		return featureFrequency[index];
	}
	public int getTypeFrequency(int index){
		return typeFrequency[index];
	}
	public int getItemFrequency(int index){
		return itemFrequency[index];
	}
	public int getItemFrequency(int feature,int type){
		return itemFrequency[feature+type*featureSize];
	}
	public void setItemFrequency(int index,int frequency){
		setItemFrequency(index%featureSize,index/featureSize,index,frequency);
	}
	public void setItemFrequency(int feature,int type,int frequency){
		setItemFrequency(feature,type,feature+type*featureSize,frequency);
	}
	private void setItemFrequency(int feature,int type,int index,int frequency){//index=feature+type*featureSize;
		int diff=frequency-itemFrequency[index];
		featureFrequency[feature]+=diff;
		typeFrequency[type]+=diff;
		itemFrequency[index]=frequency;
	}
	public void addItemFrequency(int index,int diff){
		addItemFrequency(index%featureSize,index/featureSize,index,diff);
	}
	public void addItemFrequency(int feature,int type,int diff){
		addItemFrequency(feature,type,feature+type*featureSize,diff);
	}
	private void addItemFrequency(int feature,int type,int index,int diff){//index=feature+type*featureSize;
		featureFrequency[feature]+=diff;
		typeFrequency[type]+=diff;
		itemFrequency[index]+=diff;
	}
}
