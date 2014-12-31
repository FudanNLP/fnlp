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

import gnu.trove.set.hash.TCharHashSet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * 简易字典
 * @author xpqiu
 *
 */
public class CharClassDictionary implements Serializable{

	private static final long serialVersionUID = 4368388258602283597L;

	public String name;
	private TCharHashSet dict;

	public CharClassDictionary() {
		dict = new TCharHashSet();
	}
	public void load(String path,String tag) throws Exception{
		if(path == null) return;
		BufferedReader bfr;
		bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));
		String line = null;			
		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;
			int len = line.length();
			dict.add(line.charAt(0));
			if(len>1){
				for(int i=1;i<len-1;i++){
					dict.add(line.charAt(i));
				}
				dict.add(line.charAt(len-1));
			}
		}
		name = tag;

	}	


	/**
	 * 返回词典标签
	 */
	public boolean contains(char c) {
		return dict.contains(c);
	}
}