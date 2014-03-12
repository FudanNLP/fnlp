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

package org.fnlp.nlp.cn.tag;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.cn.Sentenizer;
import org.fnlp.nlp.cn.tag.format.Seq2ArrayWithTag;
import org.fnlp.nlp.cn.tag.format.Seq2StrWithTag;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;

/**
 * 用交叉标签的词性标注器
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.0
 */
public class POSTaggerX extends AbstractTagger {

	public POSTaggerX(String str) throws Exception {
		super(str);
		
//		DynamicViterbi dv = new DynamicViterbi(
//				(LinearViterbi) cl.getInferencer(), 
//				cl.getAlphabetFactory().buildLabelAlphabet("labels"), 
//				cl.getAlphabetFactory().buildFeatureAlphabet("features"),
//				false);
//		dv.setDynamicTemplets(DynamicTagger.getDynamicTemplet("example-data/structure/template_dynamic"));
//		cl.setInferencer(dv);
	}

	public String[][] tag2Array(String src) {
		ArrayList words = new ArrayList<String>();
		ArrayList pos = new ArrayList<String>();
		String[] s = Sentenizer.split(src);
		try {
			for (int i = 0; i < s.length; i++) {
				Instance inst = new Instance(s[i]);
				doProcess(inst);
				int[] pred = (int[]) getClassifier().classify(inst).getLabel(0);
				String[] target = labels.lookupString(pred);
				List[] res = Seq2ArrayWithTag.format(inst, target);
				words.addAll(res[0]);
				pos.addAll(res[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[][] tag = new String[2][];
		tag[0] = (String[]) words.toArray(new String[words.size()]);
		tag[1] = (String[]) pos.toArray(new String[pos.size()]);
		return tag;
	}

	@Override
	public String tag(String src) {
		String[] sents = Sentenizer.split(src);
		String tag = "";
		try {
			for (int i = 0; i < sents.length; i++) {
				Instance inst = new Instance(sents[i]);
				String[] preds = _tag(inst);
				String s = Seq2StrWithTag.format(inst, preds);
				tag += s;
				if (i < sents.length - 1)
					tag += delim;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	public static void main(String[] args) throws Exception {
		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("f", false, "segment file. Default string mode.");
		opt.addOption("s", false, "segment string");
		BasicParser parser = new BasicParser();
		CommandLine cl = parser.parse(opt, args);

		if (args.length == 0 || cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp(
					"Tagger:\n"
							+ "java edu.fudan.nlp.tag.POSTagger -f model_file input_file output_file;\n"
							+ "java edu.fudan.nlp.tag.POSTagger -s model_file string_to_segement",
					opt);
			return;
		}
		String[] arg = cl.getArgs();
		String modelFile;
		String input;
		String output = null;
		if (cl.hasOption("f") && arg.length == 3) {
			modelFile = arg[0];
			input = arg[1];
			output = arg[2];
		} else if (arg.length == 2) {
			modelFile = arg[0];
			input = arg[1];
		} else {
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}
		POSTaggerX pos = new POSTaggerX(modelFile);
		if (cl.hasOption("f")) {
			String s = pos.tagFile(input);
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					output), "utf8");
			w.write(s);
			w.close();
		} else {
			String s = pos.tag(input);
			System.out.println(s);
		}
	}

}