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



/**
 * 标记词典，以自增的方式存放标记
 * @version 1.0
 *
 */
public interface ILabelAlphabet<T> extends IAlphabet {
	

	/**
	 * 查找索引编号对应的标记
	 * @param id 索引编号
	 * @return 标记
	 */
	public T lookupString(int id);

	/**
	 * 查找一组编号对应的标记
	 * @param ids 索引编号数组
	 * @return 标记数组
	 */
	public T[] lookupString(int[] ids);
	
}