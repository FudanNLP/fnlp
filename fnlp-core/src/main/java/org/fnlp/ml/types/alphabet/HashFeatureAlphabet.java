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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.fnlp.util.hash.AbstractHashCode;
import org.fnlp.util.hash.MurmurHash;

import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;

/**
 * 特征词典
 * @author Feng Ji
 *
 */
public final class HashFeatureAlphabet implements IFeatureAlphabet {

    private static final long serialVersionUID = -6187935479742068611L;

    AbstractHashCode hashcode = new MurmurHash();
    private Map<String, String> keyMap;
    transient Map<Integer, HashSet<String>> map = new HashMap<Integer, HashSet<String>>();
    transient int count = 0;
    /**
     * 数据
     */
    protected TIntIntHashMap intdata;
    /**
     * 是否冻结
     */
    protected boolean frozen;

    

    /**
     * 最后一个特征的位置
     */
    private int last;

    public HashFeatureAlphabet() {
        intdata = new TIntIntHashMap(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR,noEntryValue,noEntryValue);
        frozen = false;
        last = 0;
    }

    @Override
    public int lookupIndex(String str) {
        return lookupIndex(str, 1);
    }

    @Override
    public int lookupIndex(String str, int indent) {
        String s = checkKeyMap(str);
        int code = hashcode.hashcode(s);
        if(!frozen){
            if (!map.containsKey(code)) 
            {
                HashSet<String> hashset = new HashSet<String>();
                hashset.add(s);
                count++;
                map.put(code, hashset);
            }else{
                HashSet<String> hashset = map.get(code);
                if (!hashset.contains(s)) {
                    count++;
                    hashset.add(s);
                }
            }
        }
        return lookupIndex(code, indent);       
    }

    private String checkKeyMap(String s) {
        if (keyMap == null)
            return s;
        else if (keyMap.containsKey(s))
            return keyMap.get(s);
        else
            return s;
    }

    public int lookupIndex(int code, int indent) {
        if (indent < 1)
            throw new IllegalArgumentException(
                    "Invalid Argument in FeatureAlphabet: " + indent);

        int ret = intdata.get(code);

        if (ret==-1 && !frozen) {//字典中没有，并且允许插入

            synchronized (this) {
                intdata.put(code, last);
                ret = last;
                last += indent;
            }
        }

        return ret;
    }

    @Override
    public int size() {
        return last;
    }
    @Override
    public int keysize() {
        return intdata.size();
    }

    @Override
    public int nonZeroSize() {
        return this.intdata.size();
    }

    @Override
    public boolean hasIndex(int id) {
        return intdata.containsValue(id);
    }

    public int remove(String s) {
        String str = checkKeyMap(s);
        int code = hashcode.hashcode(str);
        int ret = -1;
        if (intdata.containsKey(code))  {
            ret = intdata.remove(code);
        }
        return ret;
    }

    public boolean adjust(String s, int adjust)   {
        String str = checkKeyMap(s);
        int code = hashcode.hashcode(str);
        return intdata.adjustValue(code, adjust);
    }

    public void clear() {
        intdata.clear();
        last=0;
        frozen = false;
    }

    public void countConflict() {
        int conflict = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            HashSet<String> hashset = (HashSet<String>)entry.getValue();
            conflict += (hashset.size() - 1);
            //            if(hashset.size() >1)
            //              System.out.println(hashset);
        }
        System.out.println(conflict + " / " + count + " = " + (double)conflict/(double)count);
        map.clear();
        map =null;
    }

    @Override
    public boolean isStopIncrement() {
        return frozen;
    }

    @Override
    public void setStopIncrement(boolean b) {
        frozen = b;
    }
    @Override
    public TIntHash toInverseIndexMap() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TIntIntIterator iterator() {     
        return intdata.iterator();
    }

    /**
     * @param keyMap the keyMap to set
     */
    public void setKeyMap(Map<String, String> keyMap) {
        this.keyMap = keyMap;
    }
}