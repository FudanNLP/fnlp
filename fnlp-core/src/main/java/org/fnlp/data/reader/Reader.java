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

package org.fnlp.data.reader;

import java.util.Iterator;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;

/**
 * @author xpqiu
 * @version 1.0	
 * Reader为数据读入接口，用一个迭代器依次读入数据，每次返回一个Instance对象
 * 使得数据处理和读入无关
 * package edu.fudan.data.reader
 */
public abstract class Reader implements Iterator<Instance> {

	public void remove () {
		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}
	
	
	public InstanceSet read(){
		InstanceSet instSet = new InstanceSet();
		while (hasNext()) {
            Instance inst = next();
            if(inst!=null){
            	instSet.add(inst);
            }
		}
		return instSet;
	}
}