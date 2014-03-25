package org.fnlp.app.lucene;

import org.apache.lucene.util.AttributeImpl;

public final class POSAttributeImpl extends AttributeImpl 
implements POSAttribute {
	
	private String pos = "";
	  
	  public void setPartOfSpeech(String pos) {
	    this.pos = pos;
	  }

	  public String getPartOfSpeech() {
	    return pos;
	  }
	  @Override
	  public void clear() {
	    pos = "";
	  }
	  @Override
	  public void copyTo(AttributeImpl target) {
	    ((POSAttribute) target).setPartOfSpeech(pos);
	  }
}
