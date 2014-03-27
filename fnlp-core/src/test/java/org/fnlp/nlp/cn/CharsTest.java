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

package org.fnlp.nlp.cn;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.nlp.cn.Chars.CharType;

public class CharsTest {

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
	public void testIsLetterOrDigitOrPunc() {
		char input = '我';
		boolean s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
		input = '９';
		s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
		input = '：';
		s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
		input = ':';
		s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
		input = '五';
		s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
	}
	
	@Test
	public void testGetTag_Simple() {
		String input = "我们 五在 这ss里 哈哈哈";
		CharType[] tag = Chars.getType(input);
		String s = Chars.type2String(tag);
		System.out.println(s);
	}

	@Test
	public void testIsPunc() {
		String input = "ss";
		boolean s = Chars.isPunc(input);
		System.out.println(s);
	}
	@Test
	public void testIsLetterOrDigitOrPuncString() {
		String input = "VV( [^ ]+){0,1} 。( [^ ]+){0,1} 。( [^ ]+){0,1} 。 ";
//		input = MyString.normalizeRE(input);
		boolean s = Chars.isLetterOrDigitOrPunc(input);
		System.out.println(s);
	}

	@Test
	public void testIsLetter() {
		String input = "ａｂｃ１３５123我们abcABC ";
		for(int i=0;i<input.length();i++){
			char c = input.charAt(i);
			boolean b = Character.isLowerCase(c);
			System.out.println(c+String.valueOf(b));
		}
		
	}
	@Test
	public void testIsDigit() {
		String input = "ａｂｃ１３５123一二零〇abcABC ";
		for(int i=0;i<input.length();i++){
			char c = input.charAt(i);
			boolean b = Character.isDigit(c);
			System.out.println(c+String.valueOf(b));
		}
		
	}
	
	@Test
	public void testIsWhitespace() {
		String input = "　 \t\n1\r";
		for(int i=0;i<input.length();i++){
			char c = input.charAt(i);
			boolean b = Character.isWhitespace(c);
			System.out.println(c+String.valueOf(b));
		}
		
	}
	
	@Test
	public void testGetType() {
		
	}

}