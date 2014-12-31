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

package org.fnlp.nlp.cn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.fnlp.ml.types.Dictionary;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.NERTagger;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.nlp.parser.dep.TreeCache;
import org.fnlp.util.exception.LoadModelException;
/**
 * 统一的自然语言处理入口
 * 自然语言处理对象有此函数产生， 确保整个系统只有这一个对象，避免重复构造。
 * @author xpqiu
 */
public class CNFactory {

	public static CWSTagger seg;
	public static POSTagger pos;
	public static NERTagger ner;
	public static JointParser parser;
	public static TreeCache treeCache;

	public static String segModel = "/seg.m";
	public static String posModel = "/pos.m";
	public static String parseModel = "/dep.m";
	public static Dictionary dict = new Dictionary(true);

	public static CNFactory factory = new CNFactory();
	public static ChineseTrans ct = new ChineseTrans();
	private static boolean isEnFilter = true;

	/**
	 * 设置自定义词典
	 * @param path
	 * @throws LoadModelException
	 */
	public static void loadDict(String... path) throws LoadModelException {

		for(String file:path){
			dict.addFile(file);
		}
		setDict();

	}
	/**
	 * 
	 * @param al 字典 ArrayList&lt;String[]&gt;
	 * 						每一个元素为一个单元String[].
	 * 						String[] 第一个元素为单词，后面为对应的词性
	 */
	public static void addDict(ArrayList<String[]> al)  {
		dict.add(al);
		setDict();
	}
	
	public static void addDict(Collection<String> strs) {
		dict.addSegDict(strs);
		setDict();		
	}
	
	/**
	 * 增加词典
	 * @param words
	 * @param pos
	 */
	public static void addDict(Collection<String> words, String pos) {
		for(String w:words){
			dict.add(w, pos);
		}
		setDict();
	}

	/**
	 * 更新词典
	 */
	private static void setDict()  {
		if(dict==null||dict.size()==0)
			return;
		if(pos!=null){
			pos.setDictionary(dict, true);
		}
		else if(seg!=null){
			seg.setDictionary(dict);
		}
	}
	
	public static void saveDict(String path){
		if(dict==null||dict.size()==0)
			return;
		dict.save(path);
	}




	public enum Models{
		SEG,
		TAG,
		SEG_TAG,
		NER,
		PARSER,
		ALL, 
	}


	/**
	 *  初始化
	 * @return
	 */
	public static CNFactory getInstance(){
		return factory;
	}
	/**
	 *  初始化，加载所有模型
	 * @return
	 * @throws LoadModelException 
	 */
	public static CNFactory getInstance(String path) throws LoadModelException{
		return getInstance(path,Models.ALL);
	}
	/**
	 * 初始化
	 * @param path 模型文件所在目录，结尾不带"/"。
	 * @param model 载入模型类型
	 * @return
	 * @throws LoadModelException 
	 */
	public static CNFactory getInstance(String path,Models model) throws LoadModelException{
		if(path.endsWith("/"))
			path = path.substring(0,path.length()-1);
		if(model ==Models.SEG){
			loadSeg(path);
		}else if(model ==Models.SEG){
			loadTag(path);
		}else if(model ==Models.SEG_TAG){
			loadSeg(path);
			loadTag(path);
		}else if(model ==Models.ALL){
			loadSeg(path);
			loadTag(path);
			loadNER(path);
			loadParser(path);
		}		
		setDict();
		return factory;
	}
	/**
	 * 读入句法模型
	 * @param path 模型所在目录，结尾不带"/"。
	 * @throws LoadModelException 
	 */
	public static void loadParser(String path) throws LoadModelException {
		if(parser==null){
			String file = path+parseModel;
			parser = new JointParser(file);
		}
	}
	/**
	 * 读入实体名识别模型
	 * @param path 模型所在目录，结尾不带"/"。
	 * @throws LoadModelException 
	 */
	public static void loadNER(String path) throws LoadModelException {
		if(ner==null && pos!=null){
			ner = new NERTagger(pos);
		}
	}
	/**
	 * 读入词性标注模型
	 * @param path 模型所在目录，结尾不带"/"。
	 * @throws LoadModelException 
	 */
	public static void loadTag(String path) throws LoadModelException {
		if(pos==null){

			String file = path+posModel;
			if(seg==null)
				pos = new POSTagger(file);
			else{
				pos = new POSTagger(seg,file);					
			}
		}
	}

	/**
	 * 读入分词模型
	 * @param path 模型所在目录，结尾不带"/"。
	 * @throws LoadModelException 
	 */
	public static void loadSeg(String path) throws LoadModelException {
		if(seg==null)
		{
			String file = path+segModel;
			seg = new CWSTagger(file);
			seg.setEnFilter(isEnFilter);
		} 
	}

	/**
	 * 分词
	 * @param input 字符串
	 * @return 词数组
	 */
	public String[] seg(String input){
		if(seg==null)
			return null;
		return seg.tag2Array(input);
	}

	/**
	 * 词性标注
	 * @param input 词数组
	 * @return 词性数组
	 */
	public String[] tag(String[] input){
		if(pos==null)
			return null;
		return pos.tagSeged(input);
	}

	/**
	 * 词性标注
	 * @param input 字符串
	 * @return 词+词性数组
	 */
	public String[][] tag(String input){
		if(pos==null||seg==null)
			return null;
		return pos.tag2Array(input);
	}
	/**
	 * 词性标注
	 * @param input 字符串
	 * @return 词/词性 的连续字符串
	 */
	public String tag2String(String input) {
		if(pos==null||seg==null)
			return null;
		return pos.tag(input);
	}

	/**
	 * 句法分析
	 * @param words 词数组
	 * @param pos 词性数组
	 * @return 依赖数组
	 */
	public int[] parse(String[] words, String[] pos){
		if(parser==null)
			return null;
		return parser.parse(words, pos);
	}
	/**
	 * 句法分析
	 * @param tagg 词数组+词性数组
	 * @return
	 */
	public DependencyTree parse2T(String[][] tagg) {
		if(tagg==null)
			return null;
		return parse2T(tagg[0], tagg[1]);
	}

	/**
	 * 句法分析
	 * @param words 词数组
	 * @param pos 词性数组
	 * @return 句法树
	 */
	public DependencyTree parse2T(String[] words, String[] pos) {
		if(parser==null)
			return null;
		if(treeCache!=null){
			DependencyTree tree = treeCache.get(words,pos);
			if(tree!=null)
				return tree;
		}
		
		if(words==null||pos==null||words.length==0||pos.length==0||words.length!=pos.length)
			return null;
		return parser.parse2T(words, pos);
	}
	/**
	 * 句法树
	 * @param sent 字符串
	 * @return 句法树
	 */
	public DependencyTree parse2T(String sent){
		if(pos==null||seg==null||parser==null||sent==null)
			return null;
		String[][] wc = tag(sent);
		if(wc==null)
			return null;		
		
		return parse2T(wc[0], wc[1]);
	}
	/**
	 * 实体名识别
	 * @param s
	 * @return 实体名+类型
	 */
	public static HashMap<String, String> ner(String s) {
		if(ner==null)
			return null;
		return ner.tag(s);
	}
	public static void setEnFilter(boolean b) {
		isEnFilter = b;

	}

	public static void readDepCache(String file) throws IOException{
		treeCache = new TreeCache();
		treeCache.read(file);	
	}
	
	


}