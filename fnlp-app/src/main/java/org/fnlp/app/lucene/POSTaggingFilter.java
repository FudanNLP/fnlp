package org.fnlp.app.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;

import edu.fudan.nlp.cn.Tags;

public final class POSTaggingFilter extends FilteringTokenFilter {

  
  private final POSAttribute posAtt = addAttribute(POSAttribute.class);


  public POSTaggingFilter(boolean enablePositionIncrements, TokenStream in) {
    super(enablePositionIncrements, in);
  }
  
  @Override
  public boolean accept() throws IOException {
    String pos = posAtt.getPartOfSpeech();
    return !Tags.isStopword(pos);
  }
}