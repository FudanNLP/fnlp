/**
 * 
 */
package org.fnlp.ml.eval;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2015年5月6日 下午4:54:05
 */
public class SeqEvalTest {

	/**
	 * @throws java.lang.Exception
	 * 下午4:54:05
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 * 下午4:54:05
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws IOException {



//		String filePath = "./paperdata/ctb6-seg/work/ctb_三列式结果_0.txt";
		String dictpath =  "D:\\项目\\9.评测\\NLPCC2015分词\\data21_No_0\\all.dict";
		String filePath = "D:\\项目\\9.评测\\NLPCC2015分词\\data21_No_0\\testSeg.txt";
//		String dictpath =  "D:\\项目\\9.评测\\NLPCC2015分词\\data21_No_0\\all.dict";

		//		filePath = "./example-data/sequence/seq.res";

//		//读取评测结果文件，并输出到outputPath
//		SeqEval ne1;
//		ne1 = new SeqEval();
//		ne1.readOOV(dictpath);
//		ne1.read(filePath);
////		ne1.NeEvl(null);
//		double[] res = ne1.calcPRF();
//		System.out.print(res[0] +" " + res[1]+" " +res[2]+" "+res[3]);

	}

}
