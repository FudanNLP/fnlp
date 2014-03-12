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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.SparseVector;

/**
 * @author xpqiu
 * @version 1.0 
 * 简单文件格式如下： 类别 ＋ “空格” ＋ 数据 package
 * 
 */
public class svmFileReader extends Reader {

	String content = null;
	BufferedReader reader;
	int type = 1;

	public svmFileReader(String file) {
		try {
			File f = new File(file);
			FileInputStream in = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param file
	 * @param type （+1,-1,0）分别表示类标签在每行的（左，右，无）
	 */
	public svmFileReader(String file,int type) {
		this(file);
		this.type = 1;
		
	}

	public boolean hasNext() {
		try {
			content = reader.readLine();
			if (content == null) {
				reader.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
		return true;
	}

	public Instance next() {
		String[] tokens = content.split("\\t+|\\s+");
		HashSparseVector sv = new HashSparseVector();
		
		for (int i = 1; i < tokens.length; i++) {
			String[] taken = tokens[i].split(":");
			if (taken.length > 1) {
				float value = Float.parseFloat(taken[1]);
				int idx = Integer.parseInt(taken[0]);
				sv.put(idx, value);
			}
		}
		return new Instance(sv, tokens[0]);
	}

}