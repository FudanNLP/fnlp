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

import org.fnlp.app.tc.TextClassifier;

import org.fnlp.data.reader.FileReader;
import org.fnlp.data.reader.Reader;

/**
 * 文本分类示例
 * @author xpqiu
 *
 */

public class TextClassificationSimple {

	/**
	 * 训练数据路径
	 */
	private static String trainDataPath = "../example-data/text-classification/";

	/**
	 * 模型文件
	 */
	private static String modelFile = "../example-data/text-classification/model.gz";

	public static void main(String[] args) throws Exception {

		
		TextClassifier tc = new TextClassifier();
		System.out.println("训练模型");
		//用不同的Reader读取相应格式的文件
		//比如，(1)每个目录表示一个类别，每个目录下的每篇文档表示一个样本，可以用DocumentReader
		//(2)每个文件表示一个类别，每个文件下的每一行表示一个样本，可以用FileReader
		//(3)LIBSVM训练文件格式，可以用svmFileReader
		//(4)所有数据在一个文件，格式为： 类别 ＋ “分隔符” ＋ 数据，可以用SimpleFileReader
		//(5)自定义Reader，继承Reader接口
		Reader reader = new FileReader(trainDataPath,"UTF-8",".data");
		tc.train(reader, modelFile);
		/**
		 * 分类器使用
		 */
		String str = "韦德：不拿冠军就是失败 詹皇：没拿也不意味失败";
		String label = (String) tc.classify(str).getLabel(0);
		System.out.println("类别："+ label);
		
		System.out.println("读入模型");
		tc = null;
		tc = new TextClassifier(modelFile);
		label = (String) tc.classify(str).getLabel(0);
		System.out.println("预测类别："+ label);
		
		
		//清除模型文件
		(new File(modelFile)).deleteOnExit();
		System.exit(0);
	}
}