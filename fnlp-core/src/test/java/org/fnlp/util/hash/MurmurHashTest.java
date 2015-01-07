/**
 * 
 */
package org.fnlp.util.hash;

import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.exception.LoadModelException;

/**
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2015年1月8日 下午3:14:28
 */
public class MurmurHashTest {

	private CWSTagger cws;

	public MurmurHashTest() {
		try {
			long t = System.currentTimeMillis();
			cws = new CWSTagger("../models/seg.m");
			long elapsed = System.currentTimeMillis() - t;
			System.out.println("Fnlp loaded in " + elapsed + " ms.");
		} catch (LoadModelException e) {
			throw new RuntimeException("Failed to load fnlp", e);
		}
	}
	

	protected String[] getCws(String text) {
		return cws.tag2Array(text);
	}

	protected void benchmark() {
		long t = System.currentTimeMillis();
		String input = "12月1日，长江经济带海关区域通关一体化改革实现流域全覆盖，南昌、武汉、长沙、成都、重庆、贵阳、昆明等7个海关加入改革。当天，流域12个关区的海关特殊监管区域也纳入区域通关一体化，长江全流域真正实现了“12关如1关”。这标志着京津冀、长江经济带、广东地区三大区域通关一体化改革全面实施";
		for (int i = 0; i < 5000; i++) {
			getCws(input);
		}
		long elapsed = System.currentTimeMillis() - t;
		System.out.println("Benchmarked " + elapsed + " ms.");
	}

	public void run() {
		// warm up the code, and perform benchmark
		for (int k = 0; k < 10; k ++) {
			benchmark();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new MurmurHashTest().run();
	}
}

