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
import java.util.Arrays;
import java.util.List;

import org.fnlp.ml.types.Instance;

/**
 * @author xpqiu
 * @version 1.0
 * SimpleFileReader
 * 简单文件格式如下：
 * 类别 ＋ “分隔符” ＋ 数据 
 * 或者
 * 数据 ＋ “分隔符” ＋ 类别
 * 
 * package edu.fudan.ml.data
 */
public class SimpleFileReader extends Reader {
	
	public enum Type{
		LabelData,  //类别 ＋ “分隔符” ＋ 数据
		DataLabel  //数据 ＋ “分隔符” ＋ 类别
	}

	String content = null;
	/**
	 * 类别和数据之间的默认分割符为：空格
	 */
	String sep = " ";
	BufferedReader reader;
	int line;
	private boolean isSplited=false;
	/**
	 * 数据格式类型
	 */
	private Type type = Type.LabelData;

	/**
	 * 数据路径
	 * @param file
	 */
	public SimpleFileReader(String file){
		init(file);
	}

	/**
	 * 
	 * @param file 数据路径
	 * @param b 是否以空格分隔数据
	 */
	public SimpleFileReader(String file, boolean b) {
		init(file);
		isSplited = b;
	}
	/**
	 * 自定义分隔符
	 * @param file 数据路径
	 * @param s 自定义分隔符
	 * @param b 是否以空格分隔数据
	 * @param t 数据格式类型
	 */
	public SimpleFileReader(String file, String s,boolean b,Type t) {
		init(file);
		sep = s;
		isSplited = b;
		type = t;
	}


	private void init(String file) {
		try {
			File f = new File(file);
			FileInputStream in = new FileInputStream(f);
			reader = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		line=0;
	}


	public boolean hasNext() {
		
		try {
			while(true){
			content = reader.readLine();
			line++;
			if(content==null){
				reader.close();
				return false;
			}
			//跳过空行
			if(content.trim().length()>0)
				break;				
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
		return true;
	}

	public Instance next() {
		
		String data;
		String label;
		if(type == Type.LabelData){
			int idx = content.indexOf(sep);
			if(idx==-1){
				System.err.println("SimpleFileReader 数据格式不对");
				System.err.println(line+"行: "+content);
				return null;
			}
			data = content.substring(idx+sep.length()).trim();
			label = content.substring(0, idx);
		}else{
			int idx = content.lastIndexOf(sep);
			if(idx==-1){
				System.err.println("SimpleFileReader 数据格式不对");
				System.err.println(line+"行: "+content);
				return null;
			}
			label = content.substring(idx+sep.length()).trim();
			data = content.substring(0, idx);
		}
		if(data.length()==0){
			System.err.println("SimpleFileReader 数据为空字符串");
			System.err.println(line+"行: "+content);
			return null;
		}
		if(isSplited){
			String[] tokens = data.split("\\t+|\\s+");
			List<String> newdata = Arrays.asList(tokens);
			return new Instance (newdata,label);
		}else
			return new Instance (data, label);
	}

}