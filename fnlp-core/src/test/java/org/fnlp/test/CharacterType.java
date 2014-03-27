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

package org.fnlp.test;

public class CharacterType {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Character.getType('我'));
		System.out.println(Character.getType('I'));
		System.out.println(Character.getType(' '));
		System.out.println(Character.getType('。'));
		System.out.println(Character.getType('.'));
		System.out.println(Character.getType('6'));
		System.out.println(Character.getType('⑴'));
		System.out.println(Character.getType('十'));

	}

}