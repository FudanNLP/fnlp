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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 特征和标签索引字典管理器
 * @author Feng Ji
 */
public final class AlphabetFactory implements Serializable {
	
	public static enum Type{
		String,
		Integer
	}
	public static Type defaultFeatureType = Type.String;

	private static final long serialVersionUID = 4949560459448660488L;

	public static final String DefalutFeatureName = "FEATURES";
	public static final String DefalutLabelName = "LABELS";
	private Map<String, IAlphabet> maps = null;

	private AlphabetFactory() {
		maps = new HashMap<String, IAlphabet>();
	}

	private AlphabetFactory(Map<String, IAlphabet> maps) {
		this.maps = maps;
	}

	/**
	 * 构造词典管理器
	 * @return 词典工厂
	 */
	public static AlphabetFactory buildFactory() {
			return new AlphabetFactory();
	}

	/**
	 * 构造特征词典
	 * @param name 词典名称
	 * @return 特征词典
	 */
	public IFeatureAlphabet buildFeatureAlphabet(String name)	{
		return buildFeatureAlphabet(name,defaultFeatureType);
	}
	/**
	 * 构造特征词典
	 * @param name 词典名称
	 * @return 特征词典
	 */
	public IFeatureAlphabet buildFeatureAlphabet(String name,Type type)	{
		IAlphabet alphabet = null;
		if (!maps.containsKey(name))	{
			IFeatureAlphabet fa;
			if(type==Type.String)
				fa = new StringFeatureAlphabet();
			else if(type == Type.Integer)
				fa = new HashFeatureAlphabet();
			else
				return null;
			maps.put(name, fa);
			alphabet = maps.get(name);
		}else	{
			alphabet = maps.get(name);
			if (!(alphabet instanceof IFeatureAlphabet))	{
				throw new ClassCastException();
			}
		}
		return (IFeatureAlphabet) alphabet;
	}

	/**
	 * 建立缺省的特征字典
	 */
	public void setDefaultFeatureAlphabet(IFeatureAlphabet alphabet)	{
		maps.put(DefalutFeatureName, alphabet);
	}
	
	/**
	 * 建立缺省的特征字典
	 * @return 缺省特征词典
	 */
	public IFeatureAlphabet DefaultFeatureAlphabet()	{
		return DefaultFeatureAlphabet(defaultFeatureType);
	}
	
	/**
	 * 建立缺省的特征字典
	 * @return 缺省特征词典
	 */
	public IFeatureAlphabet DefaultFeatureAlphabet(Type type)	{
		return buildFeatureAlphabet(DefalutFeatureName,type);
	}

	/**
	 * 重建特征词典
	 * @param name 词典名称
	 * @return 特征词典
	 */
	public IFeatureAlphabet rebuildFeatureAlphabet(String name)	{
		IFeatureAlphabet alphabet = null;
		if (maps.containsKey(name))	{
			alphabet = (IFeatureAlphabet) maps.get(name);
			alphabet.clear();
		}else{
			return buildFeatureAlphabet(name,defaultFeatureType);
		}
		return alphabet;
	}
	public void remove(String name){
		if (maps.containsKey(name))	{
			maps.remove(name);
		}
	}

	/**
	 * 构造类别词典
	 * @param name 词典名称
	 * @return 类别词典
	 */
	public LabelAlphabet buildLabelAlphabet(String name)	{
		IAlphabet alphabet = null;
		if (!maps.containsKey(name))	{
			maps.put(name, new LabelAlphabet());
			alphabet = maps.get(name);
		}else	{
			alphabet = maps.get(name);
			if (!(alphabet instanceof LabelAlphabet))	{
				throw new ClassCastException();
			}
		}
		return (LabelAlphabet) alphabet;
	}
	/**
	 * 建立缺省的标签字典
	 * @return 标签字典
	 */
	public LabelAlphabet DefaultLabelAlphabet()	{
		IAlphabet alphabet = null;
		if (!maps.containsKey(DefalutLabelName))	{
			maps.put(DefalutLabelName, new LabelAlphabet());
			alphabet = maps.get(DefalutLabelName);
		}else	{
			alphabet = maps.get(DefalutLabelName);
			if (!(alphabet instanceof LabelAlphabet))	{
				throw new ClassCastException();
			}
		}
		return (LabelAlphabet) alphabet;
	}

	/**
	 * 得到类别数量 y
	 * @return 别数量 
	 */
	public int getLabelSize() {
		return DefaultLabelAlphabet().size();
	}
	/**
	 * 得到特征数量 f(x,y)
	 * @return 特征数量 
	 */
	public int getFeatureSize() {
		return DefaultFeatureAlphabet().size();
	}

	/**
	 *  不再增加新的词
	 * @param b
	 */
	public void setStopIncrement(boolean b) {
		Iterator<String> it = maps.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			maps.get(key).setStopIncrement(b);
		}
	}
}