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

package org.fnlp.util;

import java.util.Map;
/**
 * Map按值比较
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class ValueComparator implements java.util.Comparator {
	private Map m; // the original map

	public ValueComparator(Map m) {
		this.m = m;
	}

	public int compare(Object o1, Object o2) {
		// handle some exceptions here
		Object v1 = m.get(o1);
		Object v2 = m.get(o2);
		// make sure the values implement Comparable
		return -((Comparable) v1).compareTo(v2);
	}
}