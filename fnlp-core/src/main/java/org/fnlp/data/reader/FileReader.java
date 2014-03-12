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
import java.nio.charset.Charset;
import java.util.LinkedList;

import org.fnlp.ml.types.Instance;
/**
 * @author xpqiu
 * @version 1.0
 * 文档数据读取如下：
 * 输入为数据存放路径（子文件夹不处理）
 * 不同类别存放在各自文件中
 * 类别：文件名
 * 数据：文件内的一行字符
 * package edu.fudan.ml.data
 */
public class FileReader extends Reader {

	LinkedList<File> files;
	Instance cur;
	Charset charset;
	String content = null;
	BufferedReader reader;
	int line;
	File currentFile;
	private String filter;

	public FileReader(String path) {
		this(path, "UTF-8",null);
	}
	/**
	 * 
	 * @param path 路径名
	 * @param charsetName 字符编码
	 * @param filter 文件类型过滤
	 */
	public FileReader(String path, String charsetName, String filter) {
		files = new LinkedList<File>();
		this.filter = filter;
		File fpath = new File(path);

		if(fpath.isDirectory()) {
			File[] flist = fpath.listFiles();
			for(int i=0;i<flist.length;i++){
				if(flist[i].isFile()){
					if(filter==null)
						files.push(flist[i]);
					else if(flist[i].getName().endsWith(filter))
						files.push(flist[i]);						
				}
			}
		}else{
			System.err.println("输入必须为目录");
		}
		if(files.size()==0)
			System.err.println("找不到合法文件");
		charset = Charset.forName(charsetName);
		getFile();
	}

	private boolean getFile() {
		currentFile = files.poll();
		if(currentFile==null)
			return false;
		try {
			FileInputStream in = new FileInputStream(currentFile);
			reader = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
		} catch (FileNotFoundException e) {			
			System.err.println("文件不存在");
			return false;
		} catch (UnsupportedEncodingException e) {
			System.err.println("文件编码错误");
			return false;
		}
		line=0;
		return true;
	}

	public boolean hasNext() {
		while(true){
			try{
				content = reader.readLine();
				line++;
				if(content==null){
					reader.close();
					if(!getFile()){
						return false;
					}
					continue;
				}
				content = content.trim();
				if(content.length()==0)
					continue;
				else
					return true;			
			}catch (IOException e) {
				System.err.println("读文件错误。文件名："+currentFile.getName()+"行数："+(line-1));
				return false;
			}
		}
	}

	public Instance next() {
		int idx = currentFile.getName().indexOf(".");
		return new Instance (content,currentFile.getName().substring(0, idx));
	}
}