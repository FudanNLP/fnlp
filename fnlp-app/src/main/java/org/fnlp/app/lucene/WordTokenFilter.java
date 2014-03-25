package org.fnlp.app.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.tag.CWSTagger;
import edu.fudan.nlp.cn.tag.POSTagger;

public final class WordTokenFilter extends TokenFilter {

	private Iterator<String> tokenIter;
	private List<String> tokenBuffer;
	
	private Iterator<String> posIter;
	private List<String> posBuffer;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final POSAttribute posAtt = addAttribute(POSAttribute.class);
	
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	private int tokStart; // only used if the length changed before this filter
	private int tokEnd; // only used if the length changed before this filter
	private boolean hasIllegalOffsets; // only if the length changed before this filter
	private int idx=0;

	CNFactory factory;
	
	/**
	 * Construct a new WordTokenizer.
	 * 
	 * @param in {@link TokenStream} of sentences 
	 */
	public WordTokenFilter(TokenStream in) {
		super(in);
		factory = CNFactory.getInstance();
	}

	@Override
	public boolean incrementToken() throws IOException {   
		if (tokenIter == null || !tokenIter.hasNext()) {
			// there are no remaining tokens from the current sentence... are there more sentences?
			if (input.incrementToken()) {
				tokStart = offsetAtt.startOffset();
				tokEnd = offsetAtt.endOffset();
				// if length by start + end offsets doesn't match the term text then assume
				// this is a synonym and don't adjust the offsets.
				hasIllegalOffsets = (tokStart + termAtt.length()) != tokEnd;
				// a new sentence is available: process it.
				String[] w = factory.seg(termAtt.toString());
				String[] p = factory.tag(w);
				tokenBuffer = Arrays.asList(w);
				posBuffer = Arrays.asList(p);
				tokenIter = tokenBuffer.iterator();
				posIter = posBuffer.iterator();
				idx = 0;
				/* 
				 * it should not be possible to have a sentence with 0 words, check just in case.
				 * returning EOS isn't the best either, but its the behavior of the original code.
				 */
				if (!tokenIter.hasNext())
					return false;
			} else {
				return false; // no more sentences, end of stream!
			}
		} 
		// WordTokenFilter must clear attributes, as it is creating new tokens.
		clearAttributes();
		// There are remaining tokens from the current sentence, return the next one. 
		String nextWord = tokenIter.next();
		String pos = posIter.next();
		termAtt.append(nextWord);
		posAtt.setPartOfSpeech(pos);
		int end = idx+nextWord.length();
		if (hasIllegalOffsets) {
			offsetAtt.setOffset(tokStart, tokEnd);
		} else {
			offsetAtt.setOffset(idx, end-1);
		}
		idx = end;
		typeAtt.setType("word");
		return true;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		tokenIter = null;
	}
}
