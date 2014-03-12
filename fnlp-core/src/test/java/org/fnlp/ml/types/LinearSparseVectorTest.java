package org.fnlp.ml.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LinearSparseVectorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LinearSparseVector sv = new LinearSparseVector();
		for (int i = 0; i < 10; i++) {
			sv.put(i, i + 0.0f);
		}
		LinearSparseVector vec = new LinearSparseVector(sv);
		for (int i = 0; i < vec.length; i++) {
			vec.add(i, 1);
		}
		int[] index = sv.indices();
		System.out.println(sv);
		System.out.println(vec);

		System.out.println(sv.l1Norm());
		System.out.println(sv.l2Norm2());
		System.out.println(vec.l1Norm());
		System.out.println(vec.l2Norm2());

		vec.plus(sv);
		System.out.println(sv);
		System.out.println(vec);

		sv.minus(vec);
		System.out.println(sv);
		System.out.println(vec);
		
		System.out.println(sv.dotProduct(vec));
		System.out.println(vec.dotProduct(sv));
		System.out.println(sv.l2Norm2());
		System.out.println(sv.dotProduct(sv));
		
		sv.minus(sv);
		System.out.println(sv.size());
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
	public void testSparseVectorDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSparseVectorSparseVector() {
		fail("Not yet implemented");
	}

	@Test
	public void testMinus() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlusSparseVector() {
		fail("Not yet implemented");
	}

	@Test
	public void testPlusSparseVectorDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testElementAt() {
		fail("Not yet implemented");
	}

	@Test
	public void testIndices() {
		fail("Not yet implemented");
	}

	@Test
	public void testDotProductSparseVector() {
		fail("Not yet implemented");
	}

	@Test
	public void testDotProductSparseVectorDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testDotProductSparseVectorIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleMultiply() {
		fail("Not yet implemented");
	}

	@Test
	public void testScaleDivide() {
		fail("Not yet implemented");
	}

	@Test
	public void testL1Norm() {
		fail("Not yet implemented");
	}

	@Test
	public void testSquared() {
		fail("Not yet implemented");
	}

	@Test
	public void testL2Norm() {
		fail("Not yet implemented");
	}

	@Test
	public void testInfinityNorm() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplicate() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testEuclideanDistance() {
		fail("Not yet implemented");
	}

	@Test
	public void testClear() {
		fail("Not yet implemented");
	}

	@Test
	public void testNormalize() {
		fail("Not yet implemented");
	}

	@Test
	public void testNormalize2() {
		fail("Not yet implemented");
	}

	@Test
	public void testDotProductDoubleArray() {
		fail("Not yet implemented");
	}

}
