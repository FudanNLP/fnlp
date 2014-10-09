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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

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
import org.fnlp.nlp.parser.Sentence;
import org.fnlp.nlp.parser.Target;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.nlp.parser.dep.JointParsingState;
import org.fnlp.nlp.parser.dep.reader.FNLPReader;

/**
 * 句法分析器训练类
 * 
 */
public class JointParerTrainer{
	String modelfile;
	Charset charset;
	File fp;
	AlphabetFactory factory;

	/**
	 * 构造函数 
	 * @param data
	 *            训练文件的目录
	 * @throws Exception
	 */
	public JointParerTrainer(String data) {
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
	 * @throws Exception
	 */
	public JointParerTrainer(String dataPath, String charset) {
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
	 * @return 
	 * @throws Exception
	 */
	private InstanceSet buildInstanceList(String file) throws IOException {

		System.out.print("生成训练数据 ...");

		FNLPReader reader = new FNLPReader(file);
		FNLPReader preReader = new FNLPReader(file);
		InstanceSet instset = new InstanceSet();
		
		LabelAlphabet la = factory.DefaultLabelAlphabet();
		IFeatureAlphabet fa = factory.DefaultFeatureAlphabet();
		int count = 0;
		
		//preReader为了把ysize定下来
		la.lookupIndex("S");
		while(preReader.hasNext()){
			Sentence sent = (Sentence) preReader.next();
			Target targets = (Target)sent.getTarget();
			for(int i=0; i<sent.length(); i++){
				String label;
				if(targets.getHead(i) != -1){
					if(targets.getHead(i) < i){
						label = "L" + targets.getDepClass(i);
					}
					//else if(targets.getHead(i) > i){
					else{
						label = "R" + targets.getDepClass(i);
					}
					la.lookupIndex(label);
				}
			}
		}
		int ysize = la.size();
		la.setStopIncrement(true);
				
		while (reader.hasNext()) {
			Sentence sent = (Sentence) reader.next();
			//	int[] heads = (int[]) instance.getTarget();
			String depClass = null;
			Target targets = (Target)sent.getTarget();
			JointParsingState state = new JointParsingState(sent);
			
			while (!state.isFinalState()) {
				// 左右焦点词在句子中的位置
				int[] lr = state.getFocusIndices();

				ArrayList<String> features = state.getFeatures();
				JointParsingState.Action action = getAction(lr[0], lr[1],
						targets);
				switch (action) {
				case LEFT:
					depClass = targets.getDepClass(lr[1]);
					break;
				case RIGHT:
					depClass = targets.getDepClass(lr[0]);
					break;
				default:

				}
				state.next(action,depClass);
				if (action == JointParsingState.Action.LEFT)
					targets.setHeads(lr[1],-1);
				if (action == JointParsingState.Action.RIGHT)
					targets.setHeads(lr[0],-1);
				String label = "";
				switch (action) {
				case LEFT:
					label += "L"+sent.getDepClass(lr[1]);		
					break;
				case RIGHT:
					label+="R"+sent.getDepClass(lr[0]);
					break;
				default:
					label = "S";					
				}
				int id = la.lookupIndex(label);				
				Instance inst = new Instance();
				inst.setTarget(id);
				int[] idx = JointParser.addFeature(fa, features, ysize);
				inst.setData(idx);
				instset.add(inst);
			}
			count++;
//			System.out.println(count);
		}
		
		instset.setAlphabetFactory(factory);
		System.out.printf("共生成实例:%d个\n", count);
		return instset;
	}


	/**
	 * 模型训练函数
	 * 
	 * @param dataFile
	 *            训练文件
	 * @param maxite
	 *            最大迭代次数
	 * @throws IOException
	 * @throws Exception
	 */
	public void train(String dataFile, int maxite, float c) throws IOException {
		
		InstanceSet instset =  buildInstanceList(dataFile);
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		SFGenerator generator = new SFGenerator();
		int fsize = features.size();
		
		LabelAlphabet la = factory.DefaultLabelAlphabet();
		int ysize = la.size();
		System.out.printf("开始训练");
		LinearMax solver = new LinearMax(generator, ysize);
		ZeroOneLoss loss = new ZeroOneLoss();
		Update update = new LinearMaxPAUpdate(loss);
		OnlineTrainer trainer = new OnlineTrainer(solver, update, loss,
				fsize, maxite, c);
		Linear models = trainer.train(instset, null);
		instset = null;
		solver = null;
		loss = null;
		trainer = null;
		System.out.println();
		factory.setStopIncrement(true);
		models.saveTo(modelfile);

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
	private JointParsingState.Action getAction(int l, int r, Target targets) {
		if (targets.getHead(l) == r && modifierNumOf(l, targets) == 0)
			return JointParsingState.Action.RIGHT;
		if (targets.getHead(r) == l && modifierNumOf(r, targets) == 0)
			return JointParsingState.Action.LEFT;
		return JointParsingState.Action.SHIFT;
	}

	private int modifierNumOf(int h, Target target) {
		int n = 0;
		for (int i = 0; i < target.size(); i++)
			if (target.getHead(i) == h)
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
		//		args = new String[2];
		//		args[0] = "./tmp/malt.train";
		//		args[1] = "./tmp/Malt2Model.gz";
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

		JointParerTrainer trainer = new JointParerTrainer(modelfile);
		trainer.train(datafile, maxite, c);
	}

}