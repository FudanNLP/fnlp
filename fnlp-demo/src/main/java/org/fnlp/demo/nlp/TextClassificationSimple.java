package org.fnlp.demo.nlp;

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