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

package org.fnlp.ml.types.alphabet;

import java.lang.reflect.Array;

public class LabelAlphabetEnum<T extends Enum<T>> implements ILabelAlphabet<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1821762568866399277L;

	private final Class<T> clazz;
	
	public LabelAlphabetEnum(Class<T> clazz) {
        this.clazz = clazz;
    }
	
	

	@Override
	public boolean isStopIncrement() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setStopIncrement(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int lookupIndex(String str) {
		T t;
		try {
			t = Enum.valueOf(clazz, str);
			return t.ordinal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
		
	}

	@Override
	public int size() {		
		return clazz.getEnumConstants().length;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T lookupString(int id) {
		T[] ts = clazz.getEnumConstants();
		if(id<0||id>ts.length-1)
			return null;
		return ts[id];
	}

	@Override
	public T[] lookupString(int[] ids) {
		@SuppressWarnings("unchecked")
		T[] vals = (T[]) Array.newInstance(clazz.getComponentType(), ids.length);
		for(int i = 0; i < ids.length; i++)	{
			vals[i] = lookupString(ids[i]);
		}
		return vals;
	}

}