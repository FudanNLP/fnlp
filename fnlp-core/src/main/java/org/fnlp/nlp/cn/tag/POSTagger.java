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
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.classifier.struct.inf.ConstraintViterbi;
import org.fnlp.ml.classifier.struct.inf.LinearViterbi;
import org.fnlp.ml.types.Dictionary;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.seq.DictPOSLabel;
import org.fnlp.util.exception.LoadModelException;

/**
 * 词性标注器
 * 先分词，再做词性标注
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class POSTagger extends AbstractTagger {

	private DictPOSLabel dictPipe = null;
	private Pipe oldfeaturePipe = null;
	/**
	 * 分词模型
	 */
	public CWSTagger cws;

	/**
	 * 构造函数
	 * @param cwsmodel 分词模型文件
	 * @param str 词性模型文件
	 * @throws LoadModelException
	 */
	public POSTagger(String cwsmodel, String str) throws LoadModelException {
		super(str);
		cws = new CWSTagger(cwsmodel);
	}
	
	/**
	 * 构造函数
	 * @param cwsmodel 分词模型文件
	 * @param posmodel 词性模型文件
	 */
	public POSTagger(CWSTagger cwsmodel, POSTagger posmodel) throws LoadModelException {
		super(posmodel);
		prePipe = posmodel.prePipe;
		cws = new CWSTagger(cwsmodel);
	}

	//分词词典也被dict指定
	public POSTagger(String cwsmodel, String str, Dictionary dict) throws LoadModelException {
		super(str);
		cws = new CWSTagger(cwsmodel);
		setDictionary(dict, true);	
	}


	/**
	 * 构造函数
	 * @param str 词性模型文件
	 * @throws LoadModelException
	 */
	public POSTagger(String str) throws LoadModelException {
		super(str);
		System.out.println("只能处理分好词的句子");
	}

	/**
	 * 不建立分词模型，只能处理分好词的句子
	 * @param str
	 * @param dict
	 * @throws LoadModelException
	 */
	public POSTagger(String str, Dictionary dict) throws LoadModelException   {
		super(str);
		setDictionary(dict, false);
	}

	/**
	 * 构造函数
	 * @param cws 分词模型
	 * @param str 词性模型文件
	 * @throws LoadModelException
	 */
	public POSTagger(CWSTagger cws, String str) throws LoadModelException {
		super(str);
		if(cws==null)
			throw new LoadModelException("分词模型不能为空");
		this.cws = cws;	
	}
	/**
	 * 
	 * @param cws 分词模型
	 * @param str 词性模型文件
	 * @param dict 词性词典
	 * @param isSetSegDict bool 指定该dict是否用于cws分词（分词和词性可以使用不同的词典）。true为替换之前的分词词典, false为使用原来分词设置的词典。
	 * @throws Exception
	 */
	public POSTagger(CWSTagger cws, String str, Dictionary dict, boolean isSetSegDict) throws Exception {
		super(str);
		if(cws==null)
			throw new Exception("分词模型不能为空");
		this.cws = cws;
		setDictionary(dict, isSetSegDict);	
	}

	/**
	 * 设置词典, 参数指定是否同时设置分词词典
	 * @param dict 词典
	 */
	public void setDictionary(Dictionary dict, boolean isSetSegDict)   {
		removeDictionary(isSetSegDict);
		if(cws != null && isSetSegDict)
			cws.setDictionary(dict);
		dictPipe = null;
		dictPipe = new DictPOSLabel(dict, labels);
		oldfeaturePipe = featurePipe;
		featurePipe = new SeriesPipes(new Pipe[] { dictPipe, featurePipe });
		LinearViterbi dv = new ConstraintViterbi(
				(LinearViterbi) getClassifier().getInferencer(),labels.size());
		getClassifier().setInferencer(dv);
	}

	/**
	 * 移除词典, 参数指定是否同时移除分词词典
	 */
	public void removeDictionary(boolean isRemoveSegDict)	{
		if(cws != null && isRemoveSegDict)
			cws.removeDictionary();

		if(oldfeaturePipe != null){
			featurePipe = oldfeaturePipe;
		}
		LinearViterbi dv = new LinearViterbi(
				(LinearViterbi) getClassifier().getInferencer());
		getClassifier().setInferencer(dv);

		dictPipe = null;
		oldfeaturePipe = null;
	}

	/**
	 * 
	 * @param src 字符串
	 * @return
	 */
	public String[][] tag2Array(String src) {
		if(src==null||src.length()==0)
			return null;
		if(cws==null){
			System.out.println("只能处理分好词的句子");
			return null;
		}
		String[] words = cws.tag2Array(src);
		if(words.length==0)
			return null;
		String[] target = null;
		Instance inst = new Instance(words);
		doProcess(inst);
		int[]  pred = (int[]) getClassifier().classify(inst).getLabel(0);
		target = labels.lookupString(pred);


		String[][] tags = new String[2][];
		tags[0] = words;		
		tags[1] = target;
		return tags;
	}

	public String[][][] tag2DoubleArray(String src) {
		if(cws==null){
			System.out.println("只能处理分好词的句子");
			return null;
		}
		String[][] words = cws.tag2DoubleArray(src);
		String[][][] tags = new String[words.length][2][];

		for (int i = 0; i < words.length; i++) {
			tags[i][0] = words[i];
			tags[i][1] = tagSeged(words[i]);
		}

		return tags;
	}

	@Override
	public String tag(String src) {
		if(src==null||src.length()==0)
			return src;
		if(cws==null){
			System.out.println("只能处理分好词的句子");
			return null;
		}
		String[] words = cws.tag2Array(src);
		if(words.length==0)
			return src;

		Instance inst = new Instance(words);
		doProcess(inst);
		int[] pred = (int[]) getClassifier().classify(inst).getLabel(0);
		String[] target = labels.lookupString(pred);
		String res = format(words, target);
		return res;
	}
	/**
	 * 将词/词性数组转换成用空格隔开的“词/词性”序列序列
	 * @param words 词数组
	 * @param target 词性数组
	 * @return 用空格隔开的“词/词性”序列
	 */
	public String format(String[] words, String[] target) {
		StringBuilder sb = new StringBuilder();
		for(int j=0;j<words.length;j++){
			sb.append(words[j]);
			if(Chars.isWhiteSpace(words[j]))//空格不输出词性
				continue;
			sb.append("/");
			sb.append(target[j]);
			if(j<words.length-1)
				sb.append(delim);
		} 
		String res = sb.toString();
		return res;
	}


	/**
	 * 处理分好词的句子
	 * @param src
	 * @return
	 */
	public String[] tagSeged(String[] src) {
		if(src==null || src.length==0)
			return null;
		String[] target=null;
		try {
			Instance inst = new Instance(src);
			doProcess(inst);
			int[] pred = (int[]) getClassifier().classify(inst).getLabel(0);
			target = labels.lookupString(pred);			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return target;
	}
	/**
	 * 处理分好词的句子
	 * @param src
	 * @return 用空格隔开的词性序列
	 */
	public String tagSeged2String(String[] src) {
		StringBuilder sb = new StringBuilder();
		String[] target = tagSeged(src);
		if(target==null)
			return null;
		for(int j=0;j<target.length;j++){
			sb.append(target[j]);
			if(j<target.length-1)
				sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * 处理分好词的句子
	 * @param src
	 * @return 用空格隔开的“词/词性”序列
	 */
	public String tagSeged2StringALL(String[] src) {
		StringBuilder sb = new StringBuilder();
		String[] target = tagSeged(src);
		if(target==null)
			return null;
		String res = format(src, target);
		return res;
	}


	/**
	 * 得到支持的词性标签集合
	 * @return 词性标签集合
	 */
	public Set<String> getSupportedTags(){
		return labels.toSet();
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
							+ "java edu.fudan.nlp.tag.POSTagger -f cws_model_file pos_model_file input_file output_file;\n"
							+ "java edu.fudan.nlp.tag.POSTagger -s cws_model_file pos_model_file string_to_segement",
							opt);
			return;
		}
		String[] arg = cl.getArgs();
		String cws_model_file,pos_model_file;
		String input;
		String output = null;
		if (cl.hasOption("f") && arg.length == 4) {
			cws_model_file = arg[0];
			pos_model_file = arg[1];
			input = arg[2];
			output = arg[3];
		} else if (arg.length == 3) {
			cws_model_file = arg[0];
			pos_model_file = arg[1];
			input = arg[2];
		} else {
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}
		POSTagger pos = new POSTagger(cws_model_file, pos_model_file);
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

	/**
	 * 设定词性标注标记的类型
	 * @param lang cn:中文 en:英文
	 */
	public void SetTagType(String lang){
		if(lang.equals("en"))
			this.labels = factory.buildLabelAlphabet("label-en");
		else if(lang.equals("cn"))
			this.labels = factory.DefaultLabelAlphabet();
	}
}
