package org.fnlp.ml.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.ml.types.sv.HashSparseVector;

public class HashSparseVectorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private HashSparseVector sv;

	@Before
	public void setUp() throws Exception {
		sv = new HashSparseVector();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {
		sv.put(1, 1);
		System.out.println(sv.containsKey(2));
		System.out.println(sv.get(2));
		System.out.println(sv.get(1));
	}

	@Test
	public void testPut() {
		sv.put(1, 1);
		
	}

}
