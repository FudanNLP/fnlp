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

package org.fnlp.app.tc;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.LabelParser.Type;
import org.fnlp.ml.classifier.Predict;
import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.NGram;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2IndexArray;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.util.exception.LoadModelException;

/**
 * 文本分类简单封装
 * @author xpqiu
 *
 */

public class TextClassifier {


	private Linear pclassifier;
	private Pipe prePipe = null;

	public TextClassifier(String modelFile) throws LoadModelException {
		load(modelFile);
	}
	public TextClassifier() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 训练
	 * 	@param reader 
	 * @param modelFile
	 * @throws Exception
	 */
	public void train(Reader reader, String modelFile) throws Exception {
		/**
		 * 分类模型
		 */
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();

		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {1,2});
		//将字符特征转换成字典索引
		Pipe indexpp = new StringArray2IndexArray(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());		

		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,indexpp});
		
		SeriesPipes  pp2 = new SeriesPipes(new Pipe[]{prePipe, ngrampp,targetpp,indexpp});
		
		InstanceSet instset = new InstanceSet(pp2,af);



		//读入数据，并进行数据处理
		instset.loadThruStagePipes(reader);

		/**
		 * 建立分类器
		 */		
		OnlineTrainer trainer = new OnlineTrainer(af,100);
		trainer.c = 0.01f;
		pclassifier = trainer.train(instset);
		pp.removeTargetPipe();
		pclassifier.setPipe(pp);
		af.setStopIncrement(true);

		//将分类器保存到模型文件
		pclassifier.saveTo(modelFile);	
	}
	/**
	 * 从模型文件读入分类器
	 * @param modelFile
	 * @throws LoadModelException 
	 */
	public void load(String modelFile) throws LoadModelException{
		pclassifier =Linear.loadFrom(modelFile);
	}

	public TPredict<String> classify(String str){
		Pipe p = pclassifier.getPipe();
		Instance inst = new Instance(str);
		try {
			//特征转换
			if(prePipe!=null)
				prePipe.addThruPipe(inst);
			p.addThruPipe(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		TPredict<String> res = pclassifier.classify(inst,Type.STRING);
		return res;

	}
	public Pipe getPrePipe() {
		return prePipe;
	}
	public void setPrePipe(Pipe p) {
		this.prePipe = p;
	}

}