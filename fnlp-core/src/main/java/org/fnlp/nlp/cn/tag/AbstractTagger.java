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

package org.fnlp.nlp.cn.tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
import org.fnlp.util.exception.LoadModelException;

/**
 * 分词训练
 *
 */

public abstract class AbstractTagger {

	private Linear cl;
	protected Pipe prePipe=null;
	protected Pipe featurePipe;
	public AlphabetFactory factory;
	protected TempletGroup templets;
	protected LabelAlphabet labels;
	/**
	 * 词之间间隔标记，缺省为空格。
	 */
	protected String delim = " ";

	/**
	 * 抽象标注器构造函数
	 * @param file 模型文件
	 * @throws LoadModelException
	 */
	public AbstractTagger(String file) throws LoadModelException	{
		loadFrom(file);
		if(getClassifier()==null){
			throw new LoadModelException("模型为空");
		}

		factory = getClassifier().getAlphabetFactory();
		labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();
		featurePipe = new Sequence2FeatureSequence(templets, features,
				labels);
	}
	
	/**
	 * 抽象标注器构造函数，不是复制，仅仅是重构
	 * @param tagger 另一个标注器
	 */
	public AbstractTagger(AbstractTagger tagger){
		//modify for parallel
		Linear originClassifier = tagger.getClassifier();
		Linear newClassifier = new Linear(originClassifier.getInferencer(),originClassifier.getAlphabetFactory());
		setClassifier(newClassifier);
		
		factory = getClassifier().getAlphabetFactory();
		labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();
		featurePipe = new Sequence2FeatureSequence(tagger.templets, features,
				labels);
	}

	public AbstractTagger() {
	}
	

	/**
	 * 序列标注方法
	 * @param src 输入句子
	 * @return
	 */
	public abstract Object tag(String src);

	protected String[] _tag(Instance  inst)	{
		doProcess(inst);
		TPredict pred = getClassifier().classify(inst,Type.SEQ);
		if (pred == null)
			return new String[0];
		return (String[]) pred.getLabel(0);
	}

	/**
	 * 序列标注方法，输入输出为文件
	 * @param input  输入文件 UTF8编码
	 * @param output 输出文件 UTF8编码
	 */
	public void tagFile(String input,String output,String sep){
		String s = tagFile(input,"\n");
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(
					output), "utf-8");
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(s);
			bw.close();
		} catch (Exception e) {
			System.out.println("写输出文件错误");
			e.printStackTrace();
		}
	}

	/**
	 * 序列标注方法，输入为文件
	 * @param input 输入文件 UTF8编码
	 * @return 标注结果
	 */
	public String tagFile(String input) {
		return tagFile(input," ");
	}
	/**
	 * 序列标注方法，输入为文件
	 * @param input 输入文件 UTF8编码
	 * @return 标注结果
	 */
	public String tagFile(String input,String sep) {
		StringBuilder res = new StringBuilder();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					input), "utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			while (str != null) {
				String s = (String) tag(str);
				res.append(s);
				res.append("\n");
				str = lbin.readLine();
			}
			lbin.close();
			return res.toString();
		} catch (IOException e) {
			System.out.println("读输入文件错误");
			e.printStackTrace();
		}
		return "";

	}

	/**
	 * 数据处理方法，将数据从字符串的形式转化成向量形式
	 * @param carrier 样本实例
	 */
	public void doProcess(Instance carrier)	{
		try {
			if(prePipe!=null)
				prePipe.addThruPipe(carrier);
			carrier.setSource(carrier.getData());
			featurePipe.addThruPipe(carrier);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveTo(String modelfile) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(modelfile))));
		out.writeObject(templets);
		out.writeObject(getClassifier());
		out.close();
	}

	public void loadFrom(String modelfile) throws LoadModelException{
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(modelfile))));
			templets = (TempletGroup) in.readObject();
			setClassifier((Linear) in.readObject());
			in.close();
		} catch (Exception e) {
			throw new LoadModelException(e,modelfile);
		}
	}

	public Linear getClassifier() {
		return cl;
	}

	public void setClassifier(Linear cl) {
		this.cl = cl;
	}
}
