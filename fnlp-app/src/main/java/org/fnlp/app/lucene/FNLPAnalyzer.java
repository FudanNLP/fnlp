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

package org.fnlp.app.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
import org.fnlp.util.exception.LoadModelException;
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