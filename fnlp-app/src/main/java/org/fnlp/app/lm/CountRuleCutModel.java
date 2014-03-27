/**
*  This file is part of FNLP (formerly FudanNLP).
*  
*  FNLP is free software: you can redistribute it and/or modify
*  it under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  FNLP is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
*  
*  Copyright 2009-2014 www.fnlp.org. All rights reserved. 
*/

package org.fnlp.app.lm;

import java.io.IOException;
import java.util.ArrayList;

import gnu.trove.iterator.TObjectFloatIterator;

public class CountRuleCutModel extends GTModel {
	static final float c = 1;;
	static final int[] entropy = {0, 3, 3, 3};
	
	/**
	 * 次数剪切：舍掉次数小于k次的词对，压缩模型
	 * @param k
	 */
	
	public void setComp() {
		comp = c;
	}
	
	public void countCut() {
		setComp();
		for (int j = 1; j <= ngram; ++j) {
//			System.out.println("strCountMapArray[" + j +"].size  " + strCountMapArray[j].size());
			System.out.println("total[" + j + "]  " + totalarray[j]);
		}
		ArrayList<String> removelist = new ArrayList<String>();
		for (int i = 1; i <= ngram; ++i) {
			TObjectFloatIterator<String> it = strCountMapArray[i].iterator();
			while (it.hasNext()) {
				it.advance();
				String s = it.key();
				if (it.value() <= comp) {
					removelist.add(it.key());
					totalarray[s.length()] = totalarray[s.length()] - (int)it.value();
			    	countNumberMapArray[(int)it.value()].adjustValue(it.value(), -1);
				}
			}
			for (String s : removelist) {
				strCountMap.remove(s);
				strCountMapArray[s.length()].remove(s);
			}
			System.out.println("removelist.size  " + removelist.size());
			}
		
		chartype = strCountMapArray[1].size();
		total = 0;
		for (int j = 1; j <= ngram; ++j) {
			total += totalarray[j];
		}
	}
	
	/**
	 * 规则剪枝
	 * 压缩公式：W(wi-1wi) = C(wi-1wi) * [logP(wi-1|wi)-logP`(wi-1|wi)]
	 * 若W(wi-1wi)足够小，则wi-1wi可以删除
	 */
	public void ruleCut() {
		int tarray1 = totalarray[1];
		ArrayList<String> removelist = new ArrayList<String>();
		for (int i = 1; i <= ngram; ++i) {
			TObjectFloatIterator<String> it = strCountMapArray[i].iterator();
			while (it.hasNext()) {
				it.advance();
				String str = it.key();
				float count = it.value(); 
				double en;
				if (i == 1) {
					en = count * (Math.log(getP(str)) - Math.log(count/tarray1));
			    	
				}
				else {
				    en = count * (Math.log(getP(str)) - Math.log(count/strCountMapArray[i-1].get(str.substring(1, i))));				    
				}
			    if (Math.abs(en) <= entropy[i]) {
			    	removelist.add(str);
			    	totalarray[str.length()] = totalarray[str.length()] - (int)count;
			    	countNumberMapArray[i].adjustValue(count, -1);
			    }
			}
			for (String s : removelist) {
				strCountMap.remove(s);
				strCountMapArray[s.length()].remove(s);
			}
		}
		chartype = strCountMapArray[1].size();
		total = 0;
		for (int j = 1; j <= ngram; ++j) 
			total += totalarray[j];	
	}
	
	public void decideE() {
		int tarray1 = totalarray[1];
		for (int i = 1; i <= ngram; ++i) {
			int[][] less = new int[ngram+1][10];
			TObjectFloatIterator<String> it = strCountMapArray[i].iterator();
			while (it.hasNext()) {
				it.advance();
				String str = it.key();
				float count = it.value(); 
				double en;
				if (i == 1) {
					en = 100000 * count * (Math.log(getP(str)) - Math.log(count/tarray1));
//					System.out.println(str + "   " + count +  "   " + Math.log(getP(str)) + "  " + Math.log(count/tarray1) + "  " + en);
				}
				else {
				    en = 100000 * count * (Math.log(getP(str)) - Math.log(count/strCountMapArray[i-1].get(str.substring(1, i))));
//				    System.out.println(str + "   " + count +  "   " + Math.log(getP(str)) + "  " + Math.log(count/strCountMapArray[i-1].get(str.substring(1, str.length()))) + "  " + en);
				}
//				System.out.println(str + "   " + en);
				for(int k=0; k<less[i].length; k++){
					if(Math.abs(en) < k)
						less[i][k]++;
				}
			}
		    for(int k=0; k<less[i].length; k++){
				System.out.println("得分小于"+ k + "的比例 " + less[i][k] / (double)strCountMapArray[i].size());
			}
		}
	}
	
	public void adjustUnseenCount() {
		float[] seencount = new float[ngram+1];
		float[] unseencount = new float[ngram+1];
		TObjectFloatIterator<String> it = strCountMap.iterator();
		while (it.hasNext()) {
			it.advance();
			seencount[it.key().length()] += it.value();
		}
		for (float s : seencount) 
			System.out.println(s);
		for (int j = 1; j <= ngram; ++j) {
			unseencount[j] = totalarray[j] - seencount[j]; 
			System.out.println("unseencount[" + j + "]  " + unseencount[j]);
			String s = Integer.toString(j) + "unseen";
			strCountMap.put(s, (float) (unseencount[j] / (Math.pow(n, j)-strCountMapArray[j].size())));
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
		countCut();
//		ruleCut();
		decideE();
		adjustUnseenCount();
		save(saveModelFile);
		System.out.println("build ok");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		//生成NGramPOIModel模型文件，保存到train/GoodTuringModel.m
		CountRuleCutModel model = new CountRuleCutModel();
		model.build(3, "tmp/poi.dic","tmp/countrulecut.m");
//		model.decide_P("tmp/poi.dic","tmp/countrulecut.m");
		model.load("tmp/countrulecut.m");
//		TObjectFloatIterator<String> it = model.strCountMap.iterator();
//		while (it.hasNext()) {
//			it.advance();
//			if (it.value() <= 0)
//				System.out.println(it.key() + "  " + it.value());
////			System.out.println(it.key() + "  " + it.value() + "  " + model.compute(it.key()));
//			
//		}
		System.out.println("perplexity:" + model.computePerplexity("tmp/poi.dic"));
//		System.out.println("人民广场  " + gtmodel.compute("人民广场"));
//		System.out.println("人民广场   " + gtmodel.isPOI("人民广场"));
//		System.out.println("人民广场  " + gtmodel.compute("人民广场"));
//		System.out.println("去人民广场   " + gtmodel.isPOI("去人民广场"));
	}
}