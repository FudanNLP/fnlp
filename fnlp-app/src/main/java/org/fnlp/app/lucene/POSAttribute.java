package org.fnlp.app.lucene;

import org.apache.lucene.util.Attribute;

public interface POSAttribute extends Attribute {

    public void setPartOfSpeech(String pos);
  
    public String getPartOfSpeech();
  }