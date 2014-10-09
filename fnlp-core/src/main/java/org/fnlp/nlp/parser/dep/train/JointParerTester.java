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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.Target;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.nlp.parser.dep.analysis.AnalysisTest;
import org.fnlp.nlp.parser.dep.reader.CoNLLReader;
import org.fnlp.nlp.parser.dep.reader.FNLPReader;
import org.fnlp.nlp.parser.dep.reader.Malt2Reader;
import org.fnlp.util.exception.LoadModelException;

/**
 * 性能测试类
 * 
 * @version  
 */
public class JointParerTester {

	JointParser parser;
	boolean finaltest = true;

	/**
	 * 构造函数
	 * 
	 * @param modelfile
	 *            模型目录
	 * @throws LoadModelException 
	 */
	public JointParerTester(String modelfile) throws LoadModelException {
		parser = new JointParser(modelfile);
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
		int dError = 0;
		int total = 0;
		int errsent = 0;
		int totsent = 0;
		int errroot = 0;

		System.out.print("Beginning the test ... ");
		// 输入
		FNLPReader reader = new FNLPReader (testFile);

		// 输出
		BufferedWriter writer = null;
		if (resultFile != null)
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(resultFile), charset));

		long beginTime = System.currentTimeMillis();
		int count = 0;
		while (reader.hasNext()) {
//			System.out.println(count++);
			Sentence instance = (Sentence) reader.next();
			
			Target targets = (Target) instance.getTarget();

			int[] heads = targets.getHeads();			
			String[] depClass = targets.getRelations();
			Target t = parser.parse2R(instance);			
			String[] dPreds = (String[]) t.getRelations();
			int[] preds = (int[]) t.getHeads();
			int depCurerr = diffDepClas(depClass,dPreds);
			int curerr = diff(heads, preds);
			if(depCurerr != 0 ){
				dError += depCurerr;
			}
			if (curerr != 0) {
				errsent++;
				error += curerr;
			}
			errroot += diffRoot(heads, preds);
			totsent++;
			total += heads.length;

			if (writer != null) {
				writeTo(writer, instance, t);
			}
		}
		if (writer != null)
			writer.close();

		long endTime = System.currentTimeMillis();

		parser = null;

		float time = (endTime - beginTime) / 1000.0f;
		System.out.println("finish! =]");
		System.out.printf("total time:\t%.2f(s)\n", time);
		System.out.printf("average speed:\t%.4f(s/word)\t%.4f(s/sent)",  total
				/ time, totsent / time);
		System.out.println();
		AnalysisTest at = new AnalysisTest();
		at.test(resultFile);
		/*System.out.printf("accuracy(depClass):\t%.8f\ttotal(words):\t%d\n",  1.0-1.0
				* dError / total, total);
		System.out.printf("accuracy(heads):\t%.8f\ttotal(words):\t%d\n",  1.0-1.0
				* error / total, total);
		System.out.printf("accuracy(sents):\t%.8f\ttotal(sents):\t%d\n", 1.0-1.0
				* errsent / totsent, totsent);
		System.out.printf("accuracy(root):\t%.8f\ttotal(root):\t%d\n", 1.0- 1.0
				* errroot / totsent, totsent);*/		
	}

	private void writeTo(BufferedWriter writer, Sentence instance, Target t)
			throws IOException {
		
		StringBuffer buf = new StringBuffer();
	
		String[] words = instance.getWords();
		String[] tags = instance.getTags();
		int[] heads = ((Target) instance.getTarget()).getHeads();
		String[] relations = ((Target) instance.getTarget()).getRelations();
		int[] predheads = t.getHeads();			
		String[] predRel= t.getRelations();
		
		for (int i = 0; i < words.length; i++) {
			buf.append(words[i]);
			buf.append("\t");
			buf.append(tags[i]);
			buf.append("\t");
			buf.append(heads[i]);
			buf.append("\t");
			buf.append(relations[i]);
			buf.append("\t");
			buf.append(predheads[i]);
			buf.append("\t");
			buf.append(predRel[i]);
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
	private int diffDepClas(String[] golds, String[] preds) {
		int ret = 0;

		String[] ref = golds;
		if (golds.length > preds.length)
			ref = preds;
		for (int i = 0; i < ref.length; i++)
			if (!golds[i].equals(preds[i]))
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

		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");

		BasicParser parser = new BasicParser();
		CommandLine cl;
		try {
			cl = parser.parse(opt, args);
		} catch (Exception e) {
			System.err.println("Parameters format error");
			return;
		}

		if (args.length == 0 || cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp(
					"Tagger:\n"
							+ "ParserTester [option] model_file test_file result_file;\n",
					opt);
			return;
		}

		String[] args1 = cl.getArgs();
		String modelfile  = args1[0]; 
		String testfile = args1[1]; 
		String resultfile = args1[2];

		JointParerTester tester = new JointParerTester(modelfile);
		tester.test(testfile, resultfile, "UTF-8");
	}
}