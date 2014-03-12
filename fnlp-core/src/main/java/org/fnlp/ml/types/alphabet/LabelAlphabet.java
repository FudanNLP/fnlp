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

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 标记词典，以自增的方式存放标记
 * @version 1.0
 *
 */
public final class LabelAlphabet implements ILabelAlphabet<String> {

	private static final long serialVersionUID = 2877624165731267884L;

	/**
	 * 数据
	 */
	protected TObjectIntHashMap<String> data;

	/**
	 * 标记索引
	 */
	private TIntObjectHashMap<String> index;
	/**
	 * 是否冻结
	 */
	protected boolean frozen;

	public LabelAlphabet() {
		data = new TObjectIntHashMap<String>(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR,noEntryValue);
		frozen = false;
		index = new TIntObjectHashMap<String>();
	}

	@Override
	public int size() {
		return index.size();
	}

	/**
	 * 查找标记的索引编号
	 * @param str 标记
	 * @return 索引编号
	 */
	@Override
	public int lookupIndex(String str) {
		int ret = data.get(str);
		if (ret ==-1 && !frozen)	{
			ret = index.size();
			data.put(str, ret);
			index.put(ret, str);
		}
		return ret;
	}

	/**
	 * 查找索引编号对应的标记
	 * @param id 索引编号
	 * @return 标记
	 */
	public String lookupString(int id) {
		return index.get(id);
	}

	/**
	 * 查找一组编号对应的标记
	 * @param ids 索引编号数组
	 * @return 标记数组
	 */
	public String[] lookupString(int[] ids) {
		String[] vals = new String[ids.length];
		for(int i = 0; i < ids.length; i++)	{
			vals[i] = index.get(ids[i]);
		}
		return vals;
	}

	public TIntSet toTSet() {
		return index.keySet();
	}

	public int[] toArray() {
		return index.keys();
	}
	/**
	 * 得到索引集合
	 * @return
	 */
	public Set<Integer> getIndexSet() {
		Set<Integer> set = new HashSet<Integer>();
		for (TObjectIntIterator<String> it = data.iterator(); it.hasNext();) {
			it.advance();
			set.add(it.value());
		}
		return set;
	}


	public Map<String,Integer> toMap() {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for (TObjectIntIterator<String> it = data.iterator(); it.hasNext();) {
			it.advance();
			map.put(it.key(), it.value());
		}
		return map;
	}

	/**
	 * 得到标签集合
	 */
	public Set<String> toSet() {
		Set<String> set = new HashSet<String>();
		for (TObjectIntIterator<String> it = data.iterator(); it.hasNext();) {
			it.advance();
			set.add(it.key());
		}
		return set;
	}

	/**
	 * 将标签集合输出为字符串
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (TObjectIntIterator<String> it = data.iterator(); it.hasNext();) {
			it.advance();
			sb.append(it.key());
			sb.append(",");
		}
		return sb.toString();
	}

	/**
	 * 恢复成新字典
	 */
	public void clear() {
		index.clear();
		data.clear();		
	}

	@Override
	public boolean isStopIncrement() {
		return frozen;
	}

	@Override
	public void setStopIncrement(boolean b) {
		this.frozen = b;
	}
}