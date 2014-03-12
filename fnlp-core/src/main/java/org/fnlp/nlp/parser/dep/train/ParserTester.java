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

package org.fnlp.nlp.parser.dep.train;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.fnlp.ml.classifier.Predict;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.dep.YamadaParser;
import org.fnlp.nlp.parser.dep.reader.CoNLLReader;

/**
 * 性能测试类
 * 
 * @version Feb 16, 2009
 */
public class ParserTester {

	YamadaParser parser;
	boolean finaltest = true;

	/**
	 * 构造函数
	 * 
	 * @param modelfile
	 *            模型目录
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public ParserTester(String modelfile) throws IOException,
			ClassNotFoundException {
		parser = new YamadaParser(modelfile);
	}

	/**
	 * 测试阶段
	 * 
	 * 对于输入文件的所有句子作依赖文法分析
	 * 
	 * @param testFile
	 *            测试文件
	 * @param resultFile
	 *            结果文件
	 * @throws Exception
	 */
	public void test(String testFile, String resultFile, String charset)
			throws Exception {
		// HashMap<String, HashMap<String, Integer>> featureAlphabetByPos =
		// buildFeatureAlphabet(testFile);
		int error = 0;
		int total = 0;
		int errsent = 0;
		int totsent = 0;
		int errroot = 0;

		System.out.print("Beginning the test ... ");
		// 输入
		CoNLLReader reader = new CoNLLReader(testFile);

		// 输出
		BufferedWriter writer = null;
		if (resultFile != null)
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(resultFile), charset));

		long beginTime = System.currentTimeMillis();
		while (reader.hasNext()) {
			Sentence instance = (Sentence) reader.next();
			// int[] golds = new int[instance.length()];
			int[] heads = (int[]) instance.getTarget();
			// System.arraycopy(heads, 0, golds, 0, golds.length);
			// instance.setSource(heads);

			Predict<int[]> pred = parser.getBest(instance);

			int[] preds = (int[]) pred.getLabel(0);
			int curerr = diff(heads, preds);
			if (curerr != 0) {
				errsent++;
				error += curerr;
			}
			errroot += diffRoot(heads, preds);
			totsent++;
			total += heads.length;

			if (writer != null) {
				writeTo(writer, instance, preds);
			}
		}
		if (writer != null)
			writer.close();

		long endTime = System.currentTimeMillis();

		parser = null;

		float time = (endTime - beginTime) / 1000.0f;
		System.out.println("finish! =]");
		System.out.printf("total time:\t%.2f(s)\n", time);
		System.out.printf("errate(words):\t%.8f\ttotal(words):\t%d\n", 1.0
				* error / total, total);
		System.out.printf("errate(sents):\t%.8f\ttotal(sents):\t%d\n", 1.0
				* errsent / totsent, totsent);
		System.out.printf("errate(root):\t%.8f\ttotal(root):\t%d\n", 1.0
				* errroot / totsent, totsent);
		System.out.printf("average speed:\t%.4f(s/word)\t%.4f(s/sent)", total
				/ time, totsent / time);
	}

	private void writeTo(BufferedWriter writer, Sentence instance, int[] preds)
			throws IOException {
		int[] heads = (int[]) instance.getTarget();
		String[][] source = (String[][]) instance.getSource();
		// String[] words = instance.getWords();
		// String[] tags = instance.getTags();
		StringBuffer buf = new StringBuffer();
		// for (int i = 0; i < heads.length; i++) {
		// writer.append(String.valueOf(i+1));
		// writer.append("\t");
		// writer.append(words[i]);
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\t");
		// writer.append("_");
		// if (tags != null) {
		// writer.append("\t");
		// writer.append(tags[i]);
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\t");
		// writer.append("_");
		// }
		// writer.append("\t");
		// // writer.append(String.valueOf(heads[i]));
		// // writer.append("\t");
		// writer.append(String.valueOf(preds[i]+1));
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\t");
		// writer.append("_");
		// writer.append("\n");
		// }
		// writer.append("\n");
		// writer.flush();
		for (int i = 0; i < source.length; i++) {
			String[] toks = source[i];
			for (int j = 0; j < 8; j++) {
				buf.append(toks[j]);
				buf.append("\t");
			}
			buf.append(String.valueOf(preds[i] + 1));
			for (int j = 9; j < toks.length; j++) {
				buf.append("\t");
				buf.append(toks[j]);
			}
			buf.append("\n");
		}
		writer.write(buf.toString());
		writer.newLine();
		writer.flush();
	}

	/**
	 * 比较函数
	 * 
	 * @param golds
	 *            标准依存关系树
	 * @param preds
	 *            预测的依存关系树
	 * @return 不同的依存关系的数量
	 */
	private int diff(int[] golds, int[] preds) {
		int ret = 0;

		int[] ref = golds;
		if (golds.length > preds.length)
			ref = preds;
		for (int i = 0; i < ref.length; i++)
			if (golds[i] != preds[i])
				ret++;

		return ret;
	}

	private int diffRoot(int[] golds, int[] preds) {
		int ret = 0;
		for (int i = 0; i < golds.length; i++) {
			if (golds[i] == -1) {
				if (preds[i] != -1)
					ret = 1;
				break;
			}
		}
		return ret;
	}

	public static void main(String[] args) throws Exception {

//		args = new String[2];
//		args[0] = "./models/dep.m";
//		args[1] = "./tmp/testForParser";
//		Options opt = new Options();
//
//		opt.addOption("h", false, "Print help for this application");
//
//		BasicParser parser = new BasicParser();
//		CommandLine cl;
//		try {
//			cl = parser.parse(opt, args);
//		} catch (Exception e) {
//			System.err.println("Parameters format error");
//			return;
//		}
//
//		if (args.length == 0 || cl.hasOption('h')) {
//			HelpFormatter f = new HelpFormatter();
//			f.printHelp(
//					"Tagger:\n"
//							+ "ParserTester [option] model_file test_file result_file;\n",
//					opt);
//			return;
//		}
//
//		String modelfile = args[0];
//		String testfile = args[1];
//		String resultfile = null;
//		if (args.length == 3)
//			resultfile = args[2];

		String modelfile = "./models/dep.m";
		String testfile = "./tmp/testForParser";
		String resultfile = "./tmp/result";

		ParserTester tester = new ParserTester(modelfile);
		tester.test(testfile, resultfile, "UTF-8");
	}
}