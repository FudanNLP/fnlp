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

	

}