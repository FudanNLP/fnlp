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

package org.fnlp.ontology;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.io.IOException;
import java.io.Serializable;

import org.fnlp.util.MyCollection;

/**
 * 简易字典
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Dictionary implements Serializable{

	private static final long serialVersionUID = 4368388258602283597L;
	
	public String name;
	int maxLen;
	private THashSet<String> dict;

	public Dictionary() {
		dict = new THashSet<String>();
	}
	/**
	 * 从文件中读取
	 * @param path 文件路径
	 * @param tag 词典名
	 * @throws IOException 
	 */
	public void load(String path,String tag) throws IOException{
		if(path == null) return;
		dict = MyCollection.loadTSet(path);
		maxLen = 0;
		TObjectHashIterator<String> it = dict.iterator();
		while(it.hasNext()){
			String k = it.next();
			if(k.length()>maxLen){
				maxLen = k.length();
			}
		}		
		name = tag;		
	}	
	
	
	
	/**
	 * 返回词典标签
	 * @param word
	 * @return 词典列表
	 */
	public boolean contains(String word) {
		if(word.length()>maxLen)
			return false;
		return dict.contains(word);
	}
}