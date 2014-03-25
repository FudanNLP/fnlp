package org.fnlp.app.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public abstract class FilteringTokenFilter extends TokenFilter {

	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
	private boolean enablePositionIncrements; // no init needed, as ctor enforces setting value!

	public FilteringTokenFilter(boolean enablePositionIncrements, TokenStream input){
		super(input);
		this.enablePositionIncrements = enablePositionIncrements;
	}

	/** Override this method and return if the current input token should be returned by {@link #incrementToken}. */
	protected abstract boolean accept() throws IOException;

	@Override
	public final boolean incrementToken() throws IOException {
		if (enablePositionIncrements) {
			int skippedPositions = 0;
			while (input.incrementToken()) {
				if (accept()) {
					if (skippedPositions != 0) {
						posIncrAtt.setPositionIncrement(posIncrAtt.getPositionIncrement() + skippedPositions);
					}
					return true;
				}
				skippedPositions += posIncrAtt.getPositionIncrement();
			}
		} else {
			while (input.incrementToken()) {
				if (accept()) {
					return true;
				}
			}
		}
		// reached EOS -- return false
				return false;
	}

	/**
	 * @see #setEnablePositionIncrements(boolean)
	 */
	public boolean getEnablePositionIncrements() {
		return enablePositionIncrements;
	}

	/**
	 * If true, this TokenFilter will preserve
	 * positions of the incoming tokens (ie, accumulate and
	 * set position increments of the removed tokens).
	 * Generally, true is best as it does not
	 * lose information (positions of the original tokens)
	 * during indexing.
	 * 
	 * 
	 When set, when a token is stopped
	 * (omitted), the position increment of the following
	 * token is incremented.
	 *
	 * 
	 NOTE: be sure to also
	 * set org.apache.lucene.queryparser.classic.QueryParser#setEnablePositionIncrements if
	 * you use QueryParser to create queries.
	 */
	public void setEnablePositionIncrements(boolean enable) {
		this.enablePositionIncrements = enable;
	}
}