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

package org.fnlp.ml.classifier.linear;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.LabelParser;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.util.exception.LoadModelException;

/**
 * 线性分类器
 * 
 * @author xpqiu
 * 
 */
public class Linear extends AbstractClassifier implements Serializable	{

	private static final long serialVersionUID = -2626247109469506636L;

	protected Inferencer inferencer;
	
	protected Pipe pipe;

	public Linear(Inferencer inferencer, AlphabetFactory factory) {
		this.inferencer = inferencer;
		this.factory = factory;
	}

	public Linear() {		
	}

	public Predict classify(Instance instance, int n) {
		return (Predict) inferencer.getBest(instance, n);
	}
	
	@Override
	public Predict classify(Instance instance, Type t, int n) {
		Predict res = (Predict) inferencer.getBest(instance, n);
		return LabelParser.parse(res,factory.DefaultLabelAlphabet(),t);
	}
	
	/**
	 * 得到类标签
	 * @param idx 类标签对应的索引
	 * @return
	 */
	public String getLabel(int idx) {
		return factory.DefaultLabelAlphabet().lookupString(idx);
	}

	/**
	 * 将分类器保存到文件
	 * @param file
	 * @throws IOException
	 */
	public void saveTo(String file) throws IOException {
		File f = new File(file);
		File path = f.getParentFile();
		if(!path.exists()){
			path.mkdirs();
		}
		
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new BufferedOutputStream(new FileOutputStream(file))));
		out.writeObject(this);
		out.close();
	}
	/**
	 *  从文件读入分类器
	 * @param file
	 * @return
	 * @throws LoadModelException
	 */
	public static Linear loadFrom(String file) throws LoadModelException{
		Linear cl = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(file))));
			cl = (Linear) in.readObject();
			in.close();
		} catch (Exception e) {
			throw new LoadModelException(e,file);
		}
		return cl;
	}

	public Inferencer getInferencer() {
		return inferencer;
	}
	
	public void setInferencer(Inferencer inferencer)	{
		this.inferencer = inferencer;
	}

	

	public void setWeights(float[] weights) {
		inferencer.setWeights(weights);
	}

	public float[] getWeights() {
		return inferencer.getWeights();
	}

	public void setPipe(Pipe pipe) {
		this.pipe = pipe;		
	}
	public Pipe getPipe() {
		return pipe;		
	}
	


}