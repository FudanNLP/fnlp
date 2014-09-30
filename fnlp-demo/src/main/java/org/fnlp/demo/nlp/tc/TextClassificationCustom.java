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

import java.io.File;

import org.fnlp.data.reader.FileReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.eval.Evaluation;
import org.fnlp.ml.types.Instance;
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

public class TextClassificationCustom {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "../example-data/text-classification/";

	/**
	 * 模型文件
	 */
	private static String modelFile = "../example-data/text-classification/model.gz";

	public static void main(String[] args) throws Exception {

		
		//建立字典管理器
		AlphabetFactory af = AlphabetFactory.buildFactory();
		
		//使用n元特征
		Pipe ngrampp = new NGram(new int[] {2,3 });
		//将字符特征转换成字典索引
		Pipe indexpp = new StringArray2IndexArray(af);
		//将目标值对应的索引号作为类别
		Pipe targetpp = new Target2Label(af.DefaultLabelAlphabet());		
		
		//建立pipe组合
		SeriesPipes pp = new SeriesPipes(new Pipe[]{ngrampp,targetpp,indexpp});
		
		InstanceSet instset = new InstanceSet(pp,af);
		
		//用不同的Reader读取相应格式的文件
		Reader reader = new FileReader(trainDataPath,"UTF-8",".data");
		
		//读入数据，并进行数据处理
		instset.loadThruStagePipes(reader);
				
		float percent = 0.8f;
		
		//将数据集分为训练是和测试集
		InstanceSet[] splitsets = instset.split(percent);
		
		InstanceSet trainset = splitsets[0];
		InstanceSet testset = splitsets[1];	
		
		/**
		 * 建立分类器
		 */		
		OnlineTrainer trainer = new OnlineTrainer(af);
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
		eval.eval(cl,1);

		/**
		 * 测试
		 */
		System.out.println("类别 : 文本内容");
		System.out.println("===================");
		for(int i=0;i<testset.size();i++){
			Instance data = testset.getInstance(i);
			
			Integer gold = (Integer) data.getTarget();
			String pred_label = cl.getStringLabel(data);
			String gold_label = cl.getLabel(gold);
			
			if(pred_label.equals(gold_label))
				System.out.println(pred_label+" : "+testset.getInstance(i).getSource());
			else
				System.err.println(gold_label+"->"+pred_label+" : "+testset.getInstance(i).getSource());
		}
		
		
		/**
		 * 分类器使用
		 */
		String str = "韦德：不拿冠军就是失败 詹皇：没拿也不意味失败";
		System.out.println("============\n分类："+ str);
		Pipe p = cl.getPipe();
		Instance inst = new Instance(str);
		try {
			//特征转换
			p.addThruPipe(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String res = cl.getStringLabel(inst);
		System.out.println("类别："+ res);
		//清除模型文件
		(new File(modelFile)).deleteOnExit();
		System.exit(0);
	}
}