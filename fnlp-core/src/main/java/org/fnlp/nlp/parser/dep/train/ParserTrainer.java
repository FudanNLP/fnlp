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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.LinearMax;
import org.fnlp.ml.classifier.linear.update.LinearMaxPAUpdate;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.feature.SFGenerator;
import org.fnlp.ml.loss.ZeroOneLoss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.alphabet.StringFeatureAlphabet;
import org.fnlp.ml.types.alphabet.AlphabetFactory.Type;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.dep.ParsingState;
import org.fnlp.nlp.parser.dep.ParsingState.Action;
import org.fnlp.nlp.parser.dep.reader.CoNLLReader;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * 句法分析器训练类
 * 
 * @version Feb 16, 2009
 */
public class ParserTrainer {

	String modelfile;
	Charset charset;
	File fp;
	AlphabetFactory factory;

	/**
	 * 构造函数 
	 * @param data
	 *            训练文件的目录
	 */
	public ParserTrainer(String data) {
		this(data, "UTF-8");
		factory = AlphabetFactory.buildFactory();
	}

	/**
	 * 构造函数
	 * 
	 * @param dataPath
	 *            训练文件的目录
	 * @param charset
	 *            文件编码
	 */
	public ParserTrainer(String dataPath, String charset) {
		this.modelfile = dataPath;
		this.charset = Charset.forName(charset);
	}

	/**
	 * 生成训练实例
	 * 
	 * 以Yamada分析算法，从单个文件中生成特征以及训练样本
	 * 
	 * @param file
	 *            单个训练文件
	 * @throws Exception
	 */
	private void buildInstanceList(String file) throws IOException {

		System.out.print("generating training instances ...");

		CoNLLReader reader = new CoNLLReader(file);

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fp), charset));
		
		LabelAlphabet postagAlphabet = factory.buildLabelAlphabet("postag");

		int count = 0;
		while (reader.hasNext()) {

			Sentence instance = (Sentence) reader.next();
			int[] heads = (int[]) instance.getTarget();
			ParsingState state = new ParsingState(instance,factory);
			while (!state.isFinalState()) {
				// 左右焦点词在句子中的位置
				int[] lr = state.getFocusIndices();

				HashSparseVector features = state.getFeatures();
				ParsingState.Action action = getAction(lr[0], lr[1],
						heads);
				state.next(action);
				if (action == ParsingState.Action.LEFT)
					heads[lr[1]] = -1;
				if (action == ParsingState.Action.RIGHT)
					heads[lr[0]] = -1;

				// writer.write(String.valueOf(instance.postags[lr[0]]));
				String pos = instance.getTagAt(lr[0]);
				postagAlphabet.lookupIndex(pos);
				writer.write(pos);
				writer.write(" ");
				switch (action) {
				case LEFT:
					writer.write("L");
					break;
				case RIGHT:
					writer.write("R");
					break;
				default:
					writer.write("S");
				}
				writer.write(" ");
				int[] idx = features.indices();
				Arrays.sort(idx);
				for (int i = 0; i < idx.length; i++) {
					writer.write(String.valueOf(idx[i]));
					writer.write(" ");
				}
				writer.newLine();

			}
			writer.write('\n');
			writer.flush();
			count++;
		}
		writer.close();

		System.out.println(" ... finished");
		System.out.printf("%d instances have benn loaded.\n\n", count);
	}

	/**
	 * 模型训练函数
	 * 
	 * @param dataFile
	 *            训练文件
	 * @param maxite
	 *            最大迭代次数
	 * @throws IOException
	 */
	public void train(String dataFile, int maxite, float c) throws IOException {

		fp = File.createTempFile("train-features", null, new File("./tmp/"));

		buildInstanceList(dataFile);

		LabelAlphabet postagAlphabet = factory.buildLabelAlphabet("postag");


		SFGenerator generator = new SFGenerator();
		Linear[] models = new Linear[postagAlphabet.size()];

		for (int i = 0; i < postagAlphabet.size(); i++) {
			String pos = postagAlphabet.lookupString(i);
			InstanceSet instset = readInstanceSet(pos);
			LabelAlphabet alphabet = factory.buildLabelAlphabet(pos);
			int ysize = alphabet.size();
			System.out.printf("Training with data: %s\n", pos);
			System.out.printf("Number of labels: %d\n", ysize);
			LinearMax solver = new LinearMax(generator, ysize);
			ZeroOneLoss loss = new ZeroOneLoss();
			Update update = new LinearMaxPAUpdate(loss);
			OnlineTrainer trainer = new OnlineTrainer(solver, update, loss,
					factory, maxite, c);
			models[i] = trainer.train(instset, null);
			instset = null;
			solver = null;
			loss = null;
			trainer = null;
			System.out.println();
		}
		factory.setStopIncrement(true);
		saveModels(modelfile, models,factory);

		fp.delete();
		fp = null;
	}

	

	/**
	 * 读取样本
	 * 
	 * 根据词性读取样本文件中的样本
	 * 
	 * @param pos
	 *            词性
	 * @return 样本集
	 * @throws Exception
	 */
	private InstanceSet readInstanceSet(String pos) throws IOException {

		InstanceSet instset = new InstanceSet();

		LabelAlphabet labelAlphabet = factory.buildLabelAlphabet(pos);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(fp), charset));

		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$"))
				continue;
			if (line.startsWith(pos + " ")) {
				List<String> tokens = Arrays.asList(line.split("\\s+"));

				int[] data = new int[tokens.size() - 2];
				for (int i = 0; i < data.length; i++) {
					data[i] = Integer.parseInt(tokens.get(i + 2));
				}
				Instance inst = new Instance(data);
				inst.setTarget(labelAlphabet.lookupIndex(tokens.get(1)));

				instset.add(inst);
			}
		}

		in.close();

		labelAlphabet.setStopIncrement(true);
		instset.setAlphabetFactory(factory);

		return instset;
	}

	/**
	 * 保存模型
	 * 
	 * 以序列化的方式保存模型
	 * 
	 * @param models
	 *            模型参数
	 * @param factory
	 * @throws IOException
	 */
	public static void saveModels(String modelfile, Linear[] models, AlphabetFactory factory) throws IOException {

		ObjectOutputStream outstream = new ObjectOutputStream(
				new GZIPOutputStream(new FileOutputStream(modelfile)));
		outstream.writeObject(factory);
		outstream.writeObject(models);
		outstream.close();
	}

	/**
	 * 根据已有的依赖关系，得到焦点词之间的应采取的动作
	 * 
	 * 
	 * @param l
	 *            左焦点词在句子中是第l个词
	 * @param r
	 *            右焦点词在句子中是第r个词
	 * @param heads
	 *            中心评词
	 * @return 动作
	 */
	private ParsingState.Action getAction(int l, int r, int[] heads) {
		if (heads[l] == r && modifierNumOf(l, heads) == 0)
			return ParsingState.Action.RIGHT;
		if (heads[r] == l && modifierNumOf(r, heads) == 0)
			return ParsingState.Action.LEFT;
		return ParsingState.Action.SHIFT;
	}

	private int modifierNumOf(int h, int[] heads) {
		int n = 0;
		for (int i = 0; i < heads.length; i++)
			if (heads[i] == h)
				n++;
		return n;
	}

	/**
	 * 主文件
	 * 
	 * @param args
	 *            ： 训练文件；模型文件；循环次数
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		args = new String[2];
		args[0] = "./tmp/CoNLL2009-ST-Chinese-train.txt";
		args[1] = "./tmp/modelConll.gz";
		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("iter", true, "iterative num, default 50");
		opt.addOption("c", true, "parameters 1, default 1");

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
							+ "ParserTrainer [option] train_file model_file;\n",
					opt);
			return;
		}
		args = cl.getArgs();
		String datafile = args[0];
		String modelfile = args[1];
		int maxite = Integer.parseInt(cl.getOptionValue("iter", "50"));
		float c = Float.parseFloat(cl.getOptionValue("c", "1"));
		
		ParserTrainer trainer = new ParserTrainer(modelfile);
		trainer.train(datafile, maxite, c);
	}

}