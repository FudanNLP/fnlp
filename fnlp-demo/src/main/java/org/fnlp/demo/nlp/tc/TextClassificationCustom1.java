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

package org.fnlp.demo.nlp.tc;

import org.fnlp.data.reader.DocumentReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.eval.Evaluation;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.NGram;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2IndexArray;
import org.fnlp.nlp.pipe.Target2Label;

/**
 * 自定义流程的文本分类示例
 * 不使用封装好的org.fnlp.app.tc.TextClassifier类
 * @author xpqiu
 *
 */

public class TextClassificationCustom1 {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "../tmp/db_chn_20141028_1030/";
	private static String testDataPath = "../tmp/db_chn_20141001_1029/";

	/**
	 * 模型文件
	 */
	private static String modelFile = "../tmp/tc-model.m";

	public static void main(String[] args) throws Exception {

		
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();
		
		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {1,2},true);
		//将字符特征转换成字典索引
		Pipe indexpp = new StringArray2IndexArray(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());		
		
		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{ngrampp,indexpp});
		
		//用不同的Reader读取相应格式的文件
		Reader reader = new DocumentReader(trainDataPath);		
		InstanceSet trainset = reader.read();
		
		reader = new DocumentReader(testDataPath);
		InstanceSet testset = reader.read();
		
		targetpp.process(trainset);
		targetpp.process(testset);
		
		pp.process(trainset);
		af.setStopIncrement(true);
		pp.process(testset);		
		
		
		
		/**
		 * 建立分类器
		 */		
		OnlineTrainer trainer = new OnlineTrainer(af,50);
		Linear pclassifier = trainer.train(trainset);
		pp.removeTargetPipe();
		pclassifier.setPipe(pp);
		af.setStopIncrement(true);
		
		//将分类器保存到模型文件
		pclassifier.saveTo(modelFile);	
		pclassifier = null;
		
		//从模型文件读入分类器
		Linear cl =Linear.loadFrom(modelFile);
		
		//性能评测
		Evaluation eval = new Evaluation(testset);
		eval.eval(cl,2);

				
		
	}
}