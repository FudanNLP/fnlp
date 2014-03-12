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
import gnu.trove.map.custom_hash.TObjectIntCustomHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.strategy.HashingStrategy;

import java.util.Map;

import org.fnlp.util.hash.AbstractHashCode;
import org.fnlp.util.hash.MurmurHash;

public class ClusterFeatureAlphabet implements IFeatureAlphabet {

    private static final long serialVersionUID = -6187935479742068611L;
    /**
     * 数据
     */
    protected TObjectIntCustomHashMap<String> data;
    /**
     * 是否冻结
     */
    protected boolean frozen;
    /**
     * 最后一个特征的位置
     */
    private int last;
    private Map<String, String> keyMap;

    public ClusterFeatureAlphabet() {
        data = new TObjectIntCustomHashMap<String>(new HashingStrategy<String>() {
            AbstractHashCode hash = new MurmurHash();
            @Override
            public int computeHashCode(String object) {
                                return hash.hashcode(object);
            }
            @Override
            public boolean equals(String o1, String o2) {
//              return (o1.charAt(0)==o2.charAt(0));
                return o1.equals(o2);
            }
        }, DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, noEntryValue);
        frozen = false;
        last = 0;
    }

    public int lookupIndex(String str, int indent) {
    	if(str.startsWith("/"))
    		System.out.println(str);
        String s = checkKeyMap(str);
        return lookupIndexOri(s, indent);
    }

    @Override
    public int lookupIndex(String str) {
        return lookupIndex(str, 1);
    }

    /**
     * 查询字符串索引编号
     * @param str 字符串
     * @param indent 间隔
     * @return 字符串索引编号，-1表示词典中不存在字符串
     */
    public int lookupIndexOri(String str, int indent) {
        if (indent < 1)
            throw new IllegalArgumentException(
                    "Invalid Argument in FeatureAlphabet: " + indent);

        int ret = data.get(str);

        if (ret==-1 && !frozen) {//字典中没有，并且允许插入
            synchronized (this) {
                data.put(str, last);
                ret = last;
                last += indent;
            }
        }
        return ret;
    }

    private String checkKeyMap(String s) {
//    	s = ChineseTrans.toHalfWidth(s);
        if (keyMap == null)
            return s;
        else if (keyMap.containsKey(s))
            return keyMap.get(s);
        else
            return s;
    }

    @Override
    public int size() {
        return last;
    }
    @Override
    public int keysize() {
        return data.size();
    }
    
    @Override
    public int nonZeroSize() {
        return this.data.size();
    }

    @Override
    public boolean hasIndex(int id) {
        return data.containsValue(id);
    }

    public int remove(String str)   {
        int ret = -1;
        if (data.containsKey(str))  {
            ret = data.remove(str);
        }
        return ret;
    }

    public boolean adjust(String str, int adjust)   {
        return data.adjustValue(str, adjust);
    }

    public TObjectIntIterator<String> iterator()    {
        return data.iterator();
    }

    public void clear() {
        data.clear();
        last=0;
        frozen = false;
    }

    @Override
    public TIntObjectHashMap<String> toInverseIndexMap() {
        TIntObjectHashMap<String> index = new TIntObjectHashMap<String>();
        TObjectIntIterator<String> it = data.iterator();
        while (it.hasNext()) {
            it.advance();
            String value = it.key();
            int key = it.value();
            index.put(key, value);
        }
        return index;
    }

    @Override
    public boolean isStopIncrement() {
        return frozen;
    }

    @Override
    public void setStopIncrement(boolean b) {
        frozen = b;
        
    }

    /**
     * @param keyMap the keyMap to set
     */
    public void setKeyMap(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }
    
}