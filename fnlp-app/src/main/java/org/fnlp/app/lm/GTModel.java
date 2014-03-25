package org.fnlp.app.lm;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.hash.TFloatIntHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;

import java.io.IOException;

public class GTModel extends LModel {
//	/**
//	 *(r,Nr)<br>
//	 * r:次数<br>
//	 * Nr:出现次数为r的词对个数
//	 */
//	private TFloatIntHashMap countNumberMap = new TFloatIntHashMap();
//	private TreeMap<Float, Integer> countNumberMap = new TreeMap<Float, Integer>();
	/**
	 * 次数大于threshold，次数为原来的次数，不参与平滑
	 */
	private static double p = -6.5;
	protected static final int threshold = 8;
	protected static final int n = 10000;
	
	/**
	 * 删除出现次数小于comp的单元
	 */
	static float comp = 0;
	
//	private TreeMap<Float, Integer>[] countNumberMapArray;
	protected TFloatIntHashMap[] countNumberMapArray;
	
	/**
	 * 创建map:(frequency,frequency of frequency)
	 */
	public void buildCountNumberMap() {
		countNumberMapArray = new TFloatIntHashMap[ngram+1];
		for (int j=1; j<=ngram; ++j)
			countNumberMapArray[j] = new TFloatIntHashMap();
		for (int i=1; i<=ngram; ++i) {
//			countNumberMapArray[i] = new TreeMap<Float, Integer>();
			TObjectFloatIterator<String> iterator = strCountMapArray[i].iterator();
			while (iterator.hasNext()) {
				iterator.advance();
				float count = iterator.value();
				if (count <= threshold+1) {
					countNumberMapArray[i].adjustOrPutValue(count, 1, 1);
//					if (!countNumberMapArray[i].containsKey(count))
//						countNumberMapArray[i].put(count, 1);
//					else 
//						countNumberMapArray[i].put(count, countNumberMapArray[i].get(count)+1);
				}
			}
		}
	}
	
	/**
	 * 获取map：(String,修正后的frequency)
	 */
	public void buildStrCountMap() {
		for (int k=1; k<=ngram; ++k){
			System.out.println("total[" + k + "] " + totalarray[k]);
		}
		strCountMap = new TObjectFloatHashMap<String>(); 
//		System.out.println("1、countNumberMap.get(144130.0) " + countNumberMap.get(144130.0f));
//		System.out.println("2、stringCountMap.get(京) " + stringCountMap.get("京"));
		for (int i=1; i<=ngram; ++i) {
			float v = 1.0f;
			while (countNumberMapArray[i].get(v) == 0.0) 
				v += 1;
			TObjectFloatIterator<String> iterator = strCountMapArray[i].iterator();
			while (iterator.hasNext()) {
				iterator.advance();
				String str = iterator.key();
				float count = iterator.value();
				if (count <= threshold) {
					float adjustedCount = ((count+1)*countNumberMapArray[i].get(count+1)/countNumberMapArray[i].get(count) - count*(threshold+1)*countNumberMapArray[i].get(threshold+1)/countNumberMapArray[i].get(v)) 
							/ (1-(threshold+1)*countNumberMapArray[i].get(threshold+1)/countNumberMapArray[i].get(v));
//					strCountMapArray[i].put(str, adjustedCount);
					strCountMap.put(str, adjustedCount);
//					float m = (count+1)*countNumberMapArray[i].get(count+1)/countNumberMapArray[i].get(count) - count*(threshold+1)*countNumberMapArray[i].get(threshold+1)/countNumberMapArray[i].get(comp+1);
//					float n = 1-(threshold+1)*countNumberMapArray[i].get(threshold+1)/countNumberMapArray[i].get(comp+1);
//					stringCountMap.put(str, m / n);
//					stringCountMap.put(str, adjustedCount);
//					if (countNumberMapArray[i].higherKey(count) != null){
//						float higercount = countNumberMapArray[i].higherKey(count);
//						float adjustedCount = higercount * countNumberMapArray[i].get(higercount) / countNumberMapArray[i].get(count);
//						strCountMapArray[i].put(str, adjustedCount);
//						System.out.println(i + "  " + str + "  " + count + "  " + adjustedCount);
//					}
				}
				else 
					strCountMap.put(str, count);
			}
//			stringCountMap.putAll(strCountMapArray[i]);
		}
	}
	
	public void adjustUnseenCount() {
		for (int j=1; j<=ngram; ++j) {
			String s = Integer.toString(j) + "unseen";
			strCountMap.put(s, (float) (countNumberMapArray[j].get(comp+1) / (Math.pow(n, j)-strCountMapArray[j].size())));
		}
	}
	
	/**
	 * 读入训练数据文件，构造model
	 * @param ngram元语言模型
	 * @param inputFile 训练数据文件
	 * @param saveModelFile 模型保存文件
	 * @throws IOException
	 */
	public void build(int ngram, String saveModelFile,String... inputFile) throws IOException {
		System.out.println("build ...");
		buildStrCountMapArray(ngram, inputFile);
		buildCountNumberMap();
		buildStrCountMap();
		adjustUnseenCount();
		save(saveModelFile);
		System.out.println("build ok");
	}
	
	public double getP(String str) {
		double p;
		if(str.length() == 1) {
			if (strCountMap.containsKey(str))
				return strCountMap.get(str) / totalarray[1];
			else 
				return strCountMap.get("1unseen") / totalarray[1];
		}
		else {
			double m, n;
			if (strCountMap.containsKey(str)) 
				m = strCountMap.get(str);
			else 
				m = strCountMap.get(str.length() + "unseen");
			if (strCountMap.containsKey(str.substring(1, str.length()))) 
				n = strCountMap.get(str.substring(1, str.length()));
			else 
				n = strCountMap.get(str.length()-1 + "unseen");
			return m / n;  
		}
	}
	
	/*
	 * 观察decideP的概率分布，设置P的值，认为计算得分在 P ~ 0 之间认为符合模型
	 */
	public void setProbabilityField() {
		probabilityField = p;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//生成NGramPOIModel模型文件，保存到train/GoodTuringModel.m
	GTModel gtmodel = new GTModel();
	gtmodel.build(3, "tmp/poi.dic","tmp/GT.m");
//	gtmodel.decide_P("tmp/poi.dic","tmp/GT.m");
//	gtmodel.setProbabilityField(-7.0);
	gtmodel.load("tmp/GT.m");
	System.out.println("perplexity:" + gtmodel.computePerplexity("tmp/poi.dic"));
//	System.out.println("人民广场   " + gtmodel.isPOI("人民广场"));
//	System.out.println("人民广场  " + gtmodel.compute("人民广场"));
//	System.out.println("去人民广场   " + gtmodel.isPOI("去人民广场"));
	}
}
