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

/**
 * Internal SmartChineseAnalyzer token type constants
 * @lucene.experimental
 */
public class WordType {

  /**
   * Start of a Sentence
   */
  public final static int SENTENCE_BEGIN = 0;

  /**
   * End of a Sentence
   */
  public final static int SENTENCE_END = 1;

  /**
   * Chinese Word 
   */
  public final static int CHINESE_WORD = 2;

  /**
   * ASCII String
   */
  public final static int STRING = 3;

  /**
   * ASCII Alphanumeric 
   */
  public final static int NUMBER = 4;

  /**
   * Punctuation Symbol
   */
  public final static int DELIMITER = 5;

  /**
   * Full-Width String
   */
  public final static int FULLWIDTH_STRING = 6;

  /**
   * Full-Width Alphanumeric
   */
  public final static int FULLWIDTH_NUMBER = 7;

}