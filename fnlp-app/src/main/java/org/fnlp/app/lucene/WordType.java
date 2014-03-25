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
