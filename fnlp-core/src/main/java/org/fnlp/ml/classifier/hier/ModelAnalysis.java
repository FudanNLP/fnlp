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

package org.fnlp.ml.classifier.hier;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.util.MyHashSparseArrays;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * 优化模型文件，去掉无用的特征
 * 权重向量为HashSparseVector[]
 * @since FudanNLP 1.0
 * @author xpqiu
 * 
 */
public class ModelAnalysis {

	private Linear cl;
	public AlphabetFactory factory;
	private float thresh = 0;
	HashSparseVector[] weights;
	private IFeatureAlphabet feature;
	private LabelAlphabet label;

	public ModelAnalysis(Linear cl) {
		this.cl = cl;
		this.factory = cl.factory;
		feature = factory.DefaultFeatureAlphabet();
		label = factory.DefaultLabelAlphabet();
		this.weights = cl.weights;
	}

	/**
	 * 统计信息，计算删除非0特征后，权重的长度
	 */
	public void removeZero() {
		boolean freeze = false;
		if (feature.isStopIncrement()) {
			feature.setStopIncrement(false);
			freeze = true;
		}
		TIntObjectHashMap<String> index = (TIntObjectHashMap<String>) feature.toInverseIndexMap();
		
		System.out.println("原字典大小"+index.size());
		System.out.println("原字典大小"+feature.size());
		StringFeatureAlphabet newfeat = new StringFeatureAlphabet();
		cl.factory.setDefaultFeatureAlphabet(newfeat);
		for(int i=0;i<weights.length;i++){
				TIntFloatIterator itt = weights[i].data.iterator();
				HashSparseVector ww = new HashSparseVector();
				while(itt.hasNext()){
					itt.advance();
					float v = itt.value();
					if(Math.abs(v)<1e-3f)
						continue;
					String fea = index.get(itt.key());
					int newidx = newfeat.lookupIndex(fea);
					ww.put(newidx, v);				
			}
			weights[i] = ww;	
		}
		
		newfeat.setStopIncrement(freeze);
		System.out.println("新字典大小"+newfeat.size());		
		System.out.println("新字典大小"+feature.size());		
		index.clear();		
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		String file = "./tmp/model/tree_model.gz";
		Linear cl = Linear.loadFrom(file);

		
		ModelAnalysis ma = new ModelAnalysis(cl);
		ma.getSalientFeatures("./tmp/model/tree_model",100);

//		ma.removeZero();
//		cl.saveTo(file+1);
		System.out.print("Done");
	}

	private void getSalientFeatures(String string, int topn) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(
				new FileOutputStream(string), "UTF-8"));
		TIntObjectHashMap<String> index = (TIntObjectHashMap<String>) feature.toInverseIndexMap();
		for(int i=0;i<weights.length;i++){
			int[] idx = MyHashSparseArrays.sort(weights[i].data);
			pw.println(label.lookupString(i));
			for(int j=0;j<topn;j++){
				pw.print(index.get(idx[j]));
				pw.print("\t");
				pw.println(weights[i].get(idx[j]));
			}
			pw.println();
		}
		pw.close();
	}

}