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

import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * 特征词典
 * @author Feng Ji
 *
 */
public interface IFeatureAlphabet extends IAlphabet {

	/**
	 * 查询字符串索引编号
	 * @param str 字符串
	 * @param indent 间隔
	 * @return 字符串索引编号，-1表示词典中不存在字符串
	 */
	public int lookupIndex(String str, int indent);


	/**
	 * 字典键的个数
	 * @return
	 */
	public int keysize();
	
	/**
	 * 实际存储的数据大小
	 * @return
	 */
	public int nonZeroSize();

	/**
	 * 索引对应的字符串是否存在在词典中
	 * @param id 索引
	 * @return 是否存在在词典中
	 */
	public boolean hasIndex(int id);

	public TIterator iterator();
	
	/**
	 * 按索引建立HashMap并返回
	 * @return 按“索引-特征字符串”建立的HashMap
	 */
	public TIntHash toInverseIndexMap();
	
}