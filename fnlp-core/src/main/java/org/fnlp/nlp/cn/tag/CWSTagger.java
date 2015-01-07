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

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.classifier.struct.inf.ConstraintViterbi;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.types.Dictionary;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.Sentenizer;
import org.fnlp.nlp.cn.tag.format.FormatCWS;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.seq.DictLabel;
import org.fnlp.nlp.pipe.seq.String2Sequence;
import org.fnlp.util.MyCollection;
import org.fnlp.util.exception.LoadModelException;

import gnu.trove.set.hash.THashSet;

/**
 * 中文分词器
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.0
 */
public class CWSTagger extends AbstractTagger {
	// 考虑不同CWStagger可能使用不同dict，所以不使用静态
	private DictLabel dictPipe = null;
	private Pipe oldfeaturePipe=null;
	/**
	 * 是否对英文单词进行预处理
	 */
	private boolean isEnFilter = false;

	/**
	 * 是否对英文单词进行预处理，将连续的英文字母看成一个单词
	 * @param b
	 */
	public void setEnFilter(boolean b){
		isEnFilter = b;
		prePipe = new String2Sequence(isEnFilter);
	}

	/**
	 * 构造函数，使用LinearViterbi解码
	 * @param str 模型文件名
	 * @throws LoadModelException
	 */
	public CWSTagger(String str) throws LoadModelException {
		super(str);
		prePipe = new String2Sequence(isEnFilter);

		//		DynamicViterbi dv = new DynamicViterbi(
		//				(LinearViterbi) cl.getInferencer(), 
		//				cl.getAlphabetFactory().buildLabelAlphabet("labels"), 
		//				cl.getAlphabetFactory().buildFeatureAlphabet("features"),
		//				false);
		//		dv.setDynamicTemplets(DynamicTagger.getDynamicTemplet("example-data/structure/template_dynamic"));
		//		cl.setInferencer(dv);
	}

	public CWSTagger(CWSTagger tagger){
		super(tagger);
		prePipe = tagger.prePipe;
	}

	private void initDict(Dictionary dict) {
		
	
			dictPipe = new DictLabel(dict, labels);

		oldfeaturePipe = featurePipe;
		featurePipe = new SeriesPipes(new Pipe[] { dictPipe, featurePipe });

		LinearViterbi dv = new ConstraintViterbi(
				(LinearViterbi) getClassifier().getInferencer());
		getClassifier().setInferencer(dv);
	}

	/**
	 * 构造函数，使用ConstraintViterbi解码
	 * @param str 模型文件名
	 * @param dict 外部词典资源
	 * @throws Exception
	 */
	public CWSTagger(String str, Dictionary dict) throws Exception {
		this(str);
		initDict(dict);
	}

	/**
	 * 设置词典
	 * @param dict 词典
	 */
	public void setDictionary(Dictionary dict) {
		removeDictionary();
		initDict(dict);
	}
	/**
	 * 设置词典
	 * @param newset
	 */
	public void setDictionary(THashSet<String> newset) {
		if(newset.size()==0)
			return;
		ArrayList<String> al = new ArrayList<String>();
		MyCollection.TSet2List(newset, al);
		Dictionary dict = new Dictionary();
		dict.addSegDict(al);
		setDictionary(dict);

	}

	/**
	 * 移除词典
	 */
	public void removeDictionary()	{
		if(oldfeaturePipe != null){
			featurePipe = oldfeaturePipe;
		}
		LinearViterbi dv = new LinearViterbi(
				(LinearViterbi) getClassifier().getInferencer());
		getClassifier().setInferencer(dv);

		dictPipe = null;
		oldfeaturePipe = null;
	}

	@Override
	public String tag(String src) {
		if(src==null||src.length()==0)
			return src;
		String[] sents = Sentenizer.split(src);
		String tag = "";
		try {
			for (int i = 0; i < sents.length; i++) {
				Instance inst = new Instance(sents[i]);
				String[] preds = _tag(inst);
				String s = FormatCWS.toString(inst, preds,delim);
				tag += s;
				if (i < sents.length - 1)
					tag += delim;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tag;
	}

	/**
	 * 先进行断句，得到每句的分词结果，返回List[]数组
	 * @param src 字符串
	 * @return String[][] 多个句子数组
	 */
	public String[][] tag2DoubleArray(String src) {
		if(src==null||src.length()==0)
			return null;
		String[] sents = Sentenizer.split(src);
		String[][] words = new String[sents.length][];
		for(int i=0;i<sents.length;i++){
			words[i] = tag2Array(sents[i]);
		}		
		return words;
	}

	/**
	 * 得到分词结果 List，不进行断句
	 * @param src 字符串
	 * @return ArrayList&lt;String&gt; 词数组，每个元素为一个词
	 */
	public ArrayList<String> tag2List(String src) {
		if(src==null||src.length()==0)
			return null;
		ArrayList<String> res =null;
		try {
			Instance inst = new Instance(src);
			String[] preds = _tag(inst);
			res  = FormatCWS.toList(inst, preds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	/**
	 * 得到分词结果 String[]，不进行断句
	 * @param src 字符串
	 * @return String[] 词数组，每个元素为一个词
	 */
	public String[] tag2Array(String src) {
		ArrayList<String> words = tag2List(src);
		return (String[]) words.toArray(new String[words.size()]);
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
					"SEG:\n"
							+ "java org.fnlp.tag.CWSTagger -f model_file input_file output_file;\n"
							+ "java org.fnlp.tag.CWSTagger -s model_file string_to_segement",
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
		CWSTagger seg = new CWSTagger(modelFile);
		if (cl.hasOption("f")) {
			String s = seg.tagFile(input);
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(
					output), "utf8");
			w.write(s);
			w.close();
		} else {
			String s = seg.tag(input);
			System.out.println(s);
		}
	}


}