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

package org.fnlp.ml.loss;

public class ZeroOneLoss implements Loss {

	private float calc(Integer i1, Integer i2) {
		return i1==i2?0:1;
	}

	private float calc(String l1, String l2) {
		return l1.equals(l2)?0:1;
	}

	public float calc(Object l1, Object l2) {
		if (!l1.getClass().equals(l2.getClass()))	{
			throw new IllegalArgumentException("Exception in ZeroOneLoss: l1 and l2 have different types");
		}
		
		float ret = 0;
		if (l1 instanceof Integer)	{
			ret = calc((Integer)l1, (Integer)l2);
		}else if (l1 instanceof String)	{
			ret = calc((String)l1, (String)l2);
		}
		
		return ret;
	}

}