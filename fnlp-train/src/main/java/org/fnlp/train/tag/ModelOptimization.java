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

package org.fnlp.train.tag;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.util.Arrays;

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.alphabet.HashFeatureAlphabet;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
import org.fnlp.nlp.tag.ModelIO;
import org.fnlp.util.MyArrays;
import org.fnlp.util.MyStrings;
import org.fnlp.util.exception.LoadModelException;

/**
 * 优化模型文件，去掉无用的特征
 * 权重向量为float[]
 * @since FudanNLP 1.0
 * @author xpqiu
 * 
 */
public class ModelOptimization {

	/**
	 * 方差的下限，大于该值的特征保留
	 */
	float varsthresh = 0f;

	public ModelOptimization(float th) {
		varsthresh = th;
	}

	public ModelOptimization() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 统计信息，计算删除非0特征后，权重的长度
	 * 
	 * @throws IOException
	 */
	public void removeZero(Linear cl) {
		StringFeatureAlphabet feature = (StringFeatureAlphabet) cl.getAlphabetFactory().DefaultFeatureAlphabet();
		float[] weights = cl.getWeights();		
		int c = MyArrays.countNoneZero(weights);
		System.out.println("\n优化前");	
		System.out.println("字典索引个数"+feature.keysize());
		System.out.println("字典大小"+cl.getAlphabetFactory().DefaultFeatureAlphabet().size());
		System.out.println("权重长度"+weights.length);
		System.out.println("非零权重"+c);	
		boolean freeze = false;
		if (feature.isStopIncrement()) {
			feature.setStopIncrement(false);
			freeze = true;
		}


		TIntObjectHashMap<String> index = new TIntObjectHashMap<String>();
		TObjectIntIterator<String> it = feature.iterator();
		while (it.hasNext()) {
			it.advance();
			String value = it.key();
			int key = it.value();
			index.put(key, value);
		}
		int[] idx = index.keys();
		Arrays.sort(idx);
		int length = weights.length;
		IFeatureAlphabet newfeat = new StringFeatureAlphabet();
		cl.getAlphabetFactory().setDefaultFeatureAlphabet(newfeat);
		TFloatArrayList ww = new TFloatArrayList();
		float[] vars = new float[idx.length];
		float[] entropy = new float[idx.length];
		for (int i = 0; i < idx.length; i++) {
			int base = idx[i]; //一个特征段起始位置
			int end; //一个特征段结束位置
			if (i < idx.length - 1)
				end = idx[i + 1]; //对应下一个特征段起始位置
			else
				end  = length; //或者整个结束位置
			int interv = end - base;   //一个特征段长度
			float[] sw = new float[interv];
			for (int j = 0; j < interv; j++) {
				sw[j] = weights[base+j];
			}
			//计算方差
//			System.out.println(MyStrings.toString(sw, " "));
			vars[i] = MyArrays.viarance(sw);
			MyArrays.normalize(sw);
			MyArrays.normalize2Prop(sw);
			entropy[i] = MyArrays.entropy(sw);
			int[] maxe = new int[sw.length];
			for(int iii=0;iii<maxe.length;iii++){
				maxe[iii]=1;
			}
			float maxen = MyArrays.entropy(maxe);
			if (i==0||vars[i]>varsthresh&&entropy[i]<maxen*0.999) {
				String str = index.get(base);
				int id = newfeat.lookupIndex(str, interv);
				for (int j = 0; j < interv; j++) {
					ww.insert(id + j, weights[base + j]);
				}
			}else{
//								System.out.print(".");	
			}
		}
		System.out.println("方差均值："+MyArrays.average(vars));
		System.out.println("方差非零个数："+MyArrays.countNoneZero(vars));
		System.out.println("方差直方图："+MyStrings.toString(MyArrays.histogram(vars, 10)));
//		MyArrays.normalize2Prop(entropy);
		System.out.println("熵均值："+MyArrays.average(entropy));
		System.out.println("熵非零个数："+MyArrays.countNoneZero(entropy));
		System.out.println("熵直方图："+MyStrings.toString(MyArrays.histogram(entropy, 10)));
		
		newfeat.setStopIncrement(freeze);
		cl.setWeights(ww.toArray());

		float[] www = cl.getWeights();
		c = MyArrays.countNoneZero(www);

		System.out.println("\n优化后");	
		System.out.println("字典索引个数"+cl.getAlphabetFactory().DefaultFeatureAlphabet().keysize());
		System.out.println("字典大小"+cl.getAlphabetFactory().DefaultFeatureAlphabet().size());
		System.out.println("权重长度"+www.length);
		System.out.println("非零权重"+c);	

		index.clear();
		ww.clear();
	}

	/**
	 * 统计信息，计算删除非0特征后，权重的长度
	 * 
	 * @throws IOException
	 */
	public void removeZero1(Linear cl) {
		HashFeatureAlphabet feature = (HashFeatureAlphabet) cl.getAlphabetFactory().DefaultFeatureAlphabet();
		float[] weights = cl.getWeights();
		boolean freeze = false;
		if (feature.isStopIncrement()) {
			feature.setStopIncrement(false);
			freeze = true;
		}

		TIntIntHashMap index = new TIntIntHashMap();
		TIntIntIterator it = feature.iterator();
		while (it.hasNext()) {
			it.advance();
			int value = it.key();
			int key = it.value();
			index.put(key, value);
		}
		int[] idx = index.keys();
		Arrays.sort(idx);
		int length = weights.length;
		HashFeatureAlphabet newfeat = new HashFeatureAlphabet();
		cl.getAlphabetFactory().setDefaultFeatureAlphabet(newfeat);
		TFloatArrayList ww = new TFloatArrayList();
		for (int i = 0; i < idx.length; i++) {
			int base = idx[i]; //一个特征段起始位置
			int end; //一个特征段结束位置
			if (i < idx.length - 1)
				end = idx[i + 1]; //对应下一个特征段起始位置
			else
				end  = length; //或者整个结束位置

			int interv = end - base;   //一个特征段长度
			float[] sw = new float[interv];
			for (int j = 0; j < interv; j++) {
				sw[j] = weights[base+j];
			}
			float var = MyArrays.viarance(sw);
			if (var>varsthresh) {
				int str = index.get(base);
				int id = newfeat.lookupIndex(str, interv);
				for (int j = 0; j < interv; j++) {
					ww.insert(id + j, weights[base + j]);
				}
			}else{
				//				System.out.print(".");	
			}

		}
		newfeat.setStopIncrement(freeze);
		cl.setWeights(ww.toArray());
		index.clear();
		ww.clear();
	}


	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String file;
		float thres = 0f;
		String type;
		if (args.length == 3){
			type = args[0];
			file = args[1];
			thres = Float.valueOf(args[2]);
		}
		else{
			System.out.println("参赛个数不对！");
			return;
		}
		
		ModelOptimization op = new ModelOptimization(thres);
		if(type.equals("Tag"))
			op.optimizeTag(file);
		else if(type.equals("Dep"))
			op.optimizeDep(file);
	}

	public void optimizeTag(String file) throws IOException,
	ClassNotFoundException {
		ModelIO.loadFrom(file);
		Linear cl=ModelIO.cl;
		TempletGroup templates=ModelIO.templets;		
		ModelIO.saveTo(file+".bak", templates, cl);
		removeZero(cl);
		ModelIO.saveTo(file,templates,cl);
		System.out.print("Done");
	}

	public void optimizeDep(String file) throws LoadModelException, IOException {
		Linear cl= Linear.loadFrom(file);	
		cl.saveTo(file+".bak");
		removeZero(cl);
		cl.saveTo(file);
		System.out.print("Done");
	}

}