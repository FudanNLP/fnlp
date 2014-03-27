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

public class CountCutModel extends NGramModel{
	/**
	 * 删除出现次数小于k1的单元
	 */
	private float[] comp = {0, 1, 2, 2};
	
	/**
	 * 次数剪切：舍掉次数小于k次的词对，压缩模型
	 * @param k
	 */
	public void countCut() {
		System.out.println("count cut ... ");
		System.out.println("comp  " + comp);
		for (int j = 1; j <= ngram; ++j) {
			System.out.println("total[" + j + "]  " + totalarray[j]);
		}
		ArrayList<String> removelist = new ArrayList<String>();
		for (int i = 1; i<= ngram; ++i) {
			TObjectFloatIterator<String> it = strCountMapArray[i].iterator();
			while (it.hasNext()) {
				it.advance();
				String s = it.key();
				if (it.value() <= comp[i]) {
					removelist.add(it.key());
					totalarray[s.length()] = totalarray[s.length()] - (int)it.value();
				}
			}
			for (String s : removelist) {
				strCountMapArray[i].remove(s);
			}
			System.out.println("removelist.size  " + removelist.size());
		}
		chartype = strCountMapArray[1].size();
		total = 0;
		for (int j = 1; j <= ngram; ++j)
			total += totalarray[j];
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
		countCut();
		buildStrCountMap();
		System.out.println("stringCountMap.size()  " + strCountMap.size());
		save(saveModelFile);
		System.out.println("build ok");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		CountCutModel model = new CountCutModel();
//		model.build(3, "tmp/poi.dic","tmp/countcut.m");
		model.load("tmp/countcut.m");
//		model.decide_P("tmp/poi.dic","tmp/countcut.m");
//		System.out.println("perplexity:" + model.computePerplexity("tmp/poi.dic"));
		System.out.println("人民广场  " + model.compute("人民广场"));
		System.out.println("人民广场   " + model.contains("人民广场"));
		System.out.println("去人民广场   " + model.contains("去人民广场"));
		System.out.println("去人民广场   " + model.compute("去人民广场"));
		System.out.println("去   " + model.compute("去"));
	}

}