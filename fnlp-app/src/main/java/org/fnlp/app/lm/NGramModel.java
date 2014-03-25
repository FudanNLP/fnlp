package org.fnlp.app.lm;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.hash.TObjectFloatHashMap;

import java.io.IOException;
import java.util.ArrayList;

public class NGramModel extends LModel {
	private static double p = -6.0;
	
	public void buildStrCountMap() {
		System.out.println("total " + total);
		for (int k=1; k<=ngram; ++k){
			System.out.println("total[" + k + "] " + totalarray[k]);
		}
		strCountMap = new TObjectFloatHashMap<String>(); 
		for (int i=1; i<=ngram; ++i) {
			strCountMap.putAll(strCountMapArray[i]);
		}
		
	}
	
	/**
	 * 计算 P(A1|A2.。。AN)<br>
	 * 比如 P(A|BC) P(A|B) P(A)<br>
	 * P(A|BC) = Count(ABC) / Count(BC)<br>
	 * 如果 Count(ABC) = 0, 则P(A|BC) = 0.5 * P(A|B)<br>
	 * P(A|B) = Count(AB) / Count(B), 如果 Count(AB) = 0, P(A|B) = 0.5 * P(A)<br>
	 * P(A) = (Count(A) + 0.5) / (Sum(Count(*)) + 0.5*Num(Distinct(*)))<br>
	 * @param s
	 * @return
	 */
	public double getP(String s) {
		if(s.length() == 1) {
			if (strCountMap.contains(s)) 
				return (strCountMap.get(s) + 0.5) / (totalarray[1] + 0.5 * chartype);
			else 
				return 0.5 / (totalarray[1] + 0.5 * chartype);
			}
		else {
			if(strCountMap.contains(s)) 
				return (double) strCountMap.get(s) / strCountMap.get(s.substring(1, s.length())); 
			else 
				return 0.5 * getP(s.substring(0, s.length() - 1));
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
		buildStrCountMap();
		save(saveModelFile);
		System.out.println("build ok");
	}
	
	/*
	 * 观察decideP的概率分布，设置P的值，认为计算得分在 P ~ 0 之间认为符合模型
	 */
	public void setProbabilityField() {
		probabilityField = p;
	}
		
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//生成NGramPOIModel模型文件，保存到train/NGram.m
		NGramModel model = new NGramModel();
		model.build(3, "tmp/poi.dic","tmp/NGram.m");
		model.load("tmp/NGram.m");
		model.decide_P("tmp/poi.dic","tmp/NGram.m");
		System.out.println("perplexity:" + model.computePerplexity("tmp/poi.dic"));
//		System.out.println("人民广场  " + model.compute("人民广场"));
//		System.out.println("人民广场   " + model.isPOI("人民广场"));
//		System.out.println("人民广场  " + model.compute("去人民广场"));
//		System.out.println("去人民广场   " + model.isPOI("去人民广场"));
	}
}
