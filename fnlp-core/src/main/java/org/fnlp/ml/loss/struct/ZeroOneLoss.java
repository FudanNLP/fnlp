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

package org.fnlp.ml.loss.struct;

import java.util.List;

import org.fnlp.ml.loss.Loss;

/**
 * 0-1错误
 */
public class ZeroOneLoss implements Loss {

	private float calc(List l1, List l2) {
		boolean eq = true;
		for(int i=0; i<l1.size(); i++) {
			if (!l1.get(i).equals(l2.get(i))){
				eq = false;
				break;
			}
		}
		return eq?0:1;
	}
	
	private float calc(int[] l1, int[] l2) {
		boolean eq = true;
		for(int i=0; i<l1.length; i++) {
			if (l1[i] != l2[i]){
				eq = false;
				break;
			}
		}
		return eq?0:1;
	}

	public float calc(Object l1, Object l2) {
		if (!l1.getClass().equals(l2.getClass()))
			throw new IllegalArgumentException("Exception in HammingLoss: l1 and l2 have different types");
		
		float ret = 0;
		if (l1 instanceof int[])	{
			ret = calc((int[])l1, (int[])l2);
		}else if (l1 instanceof List)	{
			ret = calc((List)l1, (List)l2);
		}else	{
			throw new UnsupportedOperationException("");
		}
		
		return ret;
	}
}