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

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;

import org.fnlp.nlp.cn.Tags;

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