package org.fnlp.app.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.util.exception.LoadModelException;
/**
 * 基于FudanNLP的Lucene分析器
 * @author xpqiu
 */
public final class FNLPAnalyzer extends Analyzer {

	private final Version matchVersion;
	boolean usePOSFilter;

	/**
	 * 指定CNFactory路径
	 * @param matchVersion 
	 * @param path CNFactory路径
	 * @param usePOSFilter 使用词性作为停用词过滤
	 * @throws LoadModelException 
	 */
	public FNLPAnalyzer(Version matchVersion,String path,boolean usePOSFilter) throws LoadModelException {
		this.matchVersion = matchVersion;
		CNFactory.getInstance(path,Models.SEG_TAG);
		this.usePOSFilter = usePOSFilter;
	}
	/**
	 * 指定CNFactory路径
	 * @param matchVersion CNFactory路径
	 * @param path
	 * @throws LoadModelException 
	 */
	public FNLPAnalyzer(Version matchVersion,String path) throws LoadModelException {
		this.matchVersion = matchVersion;
		CNFactory.getInstance(path,Models.SEG_TAG);
	}

	/**
	 * 需要预先建立CNFactory
	 * @param matchVersion
	 */
	public FNLPAnalyzer(Version matchVersion) {
		this.matchVersion = matchVersion;
		this.usePOSFilter = true;
	}

	@Override
	public TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer tokenizer = new SentenceTokenizer(reader);
		TokenStream result = new WordTokenFilter(tokenizer);
		result = new POSTaggingFilter(true,result);

		return new TokenStreamComponents(tokenizer, result);
	}
}
