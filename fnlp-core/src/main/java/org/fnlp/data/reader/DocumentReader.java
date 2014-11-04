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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.util.MyFiles;
/**
 * @author xpqiu
 * @version 1.0
 * 文档数据读取如下：
 * 输入为数据存放路径
 * 不同类别的文件放在不同的子文件夹下
 * 类别：子文件夹名
 * 数据：文件内所有字符
 */
public class DocumentReader extends Reader {

	List<File> files;
	Instance cur;
	Charset charset;
	
	public DocumentReader(String path) {
		this(path, "UTF-8");
	}
	
	public DocumentReader(String path, String charsetName) {
		files = MyFiles.getAllFiles(path,null);
		charset = Charset.forName(charsetName);
	}

	

	public boolean hasNext() {
		if (files.isEmpty())
			return false;
		nextDocument();
		return true;
	}

	public Instance next() {
		return cur;
	}

	private void nextDocument() {
		StringBuffer buff = new StringBuffer();
		File f = files.remove(files.size()-1);
		try {
			BufferedReader cf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), charset));
			String line = null;
			while((line = cf.readLine()) != null)	{
				buff.append(line);
				buff.append('\n');
			}
			cf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	String label = f.getParentFile().getName();
		cur = new Instance(buff.toString(), label);
		buff = null;
	}
}