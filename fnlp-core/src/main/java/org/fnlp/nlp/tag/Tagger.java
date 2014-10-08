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

package org.fnlp.nlp.tag;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 序列标注器训练和测试程序
 * 
 * @author xpqiu
 * 
 */
public class Tagger extends AbstractTagger{

	
	/**
	 * 序列标注训练和测试主程序
	 * 训练： java -classpath fnlp-core.jar org.fnlp.nlp.tag.Tagger -train template train model 
	 * 测试： java -classpath fnlp-core.jar org.fnlp.nlp.tag.Tagger [-haslabel] model test [result]
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("iter", true, "iterative num, default 50");
		opt.addOption("c", true, "parameters C in PA algorithm, default 0.8");
		opt.addOption("train", false,
				"switch to training mode(Default: test model");
		opt.addOption("retrain", false,
				"switch to retraining mode(Default: test model");
		opt.addOption("margin", false, "use hamming loss as margin threshold");
		opt.addOption("interim", false, "save interim model file");
		opt.addOption("haslabel", false, "test file has includes label or not");

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
							+ "tagger [option] -train templet_file train_file model_file [test_file];\n"
							+ "tagger [option] -retrain train_file model_file newmodel_file [test_file];\n"
							+ "tagger [option] -label model_file test_file output_file\n",
							opt);
			return;
		}
		Tagger tagger = new Tagger();
		tagger.iterNum = Integer.parseInt(cl.getOptionValue("iter", "50"));
		tagger.c = Float.parseFloat(cl.getOptionValue("c", "0.8"));
		tagger.useLoss = cl.hasOption("margin");
		tagger.interim = cl.hasOption("interim");
		tagger.hasLabel = cl.hasOption("haslabel");

		String[] arg = cl.getArgs();
		if (cl.hasOption("train") && arg.length == 3) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			System.out.println("Training model ...");
			tagger.train();
		} else if (cl.hasOption("train") && arg.length == 4) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Training model ...");
			tagger.train();
		} else if (cl.hasOption("train") && arg.length == 5) {
			tagger.templateFile = arg[0];
			tagger.train = arg[1];
			tagger.model = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Training model ...");
			tagger.train();
			System.gc();
			tagger.output = arg[4];
			tagger.test();
		} else if (cl.hasOption("retrain") && arg.length == 3) {
			tagger.train = arg[0];
			tagger.model = arg[1];
			tagger.newmodel = arg[2];
			System.out.println("Re-Training model ...");
			tagger.loadFrom(tagger.model);
			tagger.train();
		} else if (cl.hasOption("retrain") && arg.length == 4) {
			tagger.train = arg[0];
			tagger.model = arg[1];
			tagger.newmodel = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Re-Training model ...");
			tagger.loadFrom(tagger.model);
			tagger.train();
		} else if (cl.hasOption("retrain") && arg.length == 5) {
			tagger.train = arg[0];
			tagger.model = arg[1];
			tagger.newmodel = arg[2];
			tagger.testfile = arg[3];
			System.out.println("Re-Training model ...");
			tagger.loadFrom(tagger.model);
			tagger.train();
			System.gc();
			tagger.output = arg[4];
			tagger.test();
		} else if (arg.length == 3) {
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.output = arg[2];
			tagger.test();
		} else if (arg.length == 2) {
			tagger.model = arg[0];
			tagger.testfile = arg[1];
			tagger.test();
		} else {
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}

		System.gc();

	}



	@Override
	public Pipe createProcessor() throws Exception {

		if (cl != null){
			factory = cl.getAlphabetFactory();
		}
		else{
			factory = AlphabetFactory.buildFactory();
			templets = new TempletGroup();
			templets.load(templateFile);
		}

		//类别集合
		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		// 特征集合
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();

		featurePipe = new Sequence2FeatureSequence(templets, features, labels);

		Pipe pipe = new SeriesPipes(new Pipe[] { new Target2Label(labels), featurePipe });

		return pipe;
	}
	
	
}