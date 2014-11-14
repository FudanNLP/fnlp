/**
 * 
 */
package org.fnlp.nlp.tag;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2014年11月14日 上午10:27:23
 */
public class TaggerTest {

	/**
	 * @throws java.lang.Exception
	 * 上午10:27:24
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 * 上午10:27:24
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for {@link org.fnlp.nlp.tag.Tagger#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		try {
			Tagger.main("-train ../example-data/sequence/template ../example-data/sequence/train.txt ../tmp/tmp.m".split("\\s+"));
			Tagger.main("../tmp/tmp.m ../example-data/sequence/test.txt ../tmp/res.txt".split("\\s+"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
