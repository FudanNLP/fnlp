package org.fnlp.demo.nlp;
import org.fnlp.app.keyword.AbstractExtractor;
import org.fnlp.app.keyword.WordExtract;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.corpus.StopWords;

/**
 * 关键词抽取使用示例
 * @author jyzhao,ltian
 *
 */
public class KeyWordExtraction {
	
	public static void main(String[] args) throws Exception {
		
		
		StopWords sw= new StopWords("../models/stopwords");
		CWSTagger seg = new CWSTagger("../models/seg.m");
		AbstractExtractor key = new WordExtract(seg,sw);
		
		System.out.println(key.extract("甬温线特别重大铁路交通事故车辆经过近24小时的清理工作，26日深夜已经全部移出事故现场，之前埋下的D301次动车车头被挖出运走", 20, true));
		
		//处理已经分好词的句子
		sw=null;
		key = new WordExtract(seg,sw);
		System.out.println(key.extract("甬温线 特别 重大 铁路交通事故车辆经过近24小时的清理工作，26日深夜已经全部移出事故现场，之前埋下的D301次动车车头被挖出运走", 20));
		System.out.println(key.extract("赵嘉亿 是 好人 还是 坏人", 5));
		
		key = new WordExtract();
		System.out.println(key.extract("", 5));

		
	}
}
