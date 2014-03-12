package org.fnlp.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.SparseVector;

public class MyHashSparseArraysTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		float[] w = { 0.3f, 1.2f, 1.09f, -0.45f, -1.2f, 0, 0, 0, 0 };
		HashSparseVector sv = new HashSparseVector(w);
		int[][] idx = MyHashSparseArrays.getTop(sv.data, 0.99f);
		MyHashSparseArrays.setZero(sv.data, idx[1]);
		System.out.println(sv);
	}

}
