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

package org.fnlp.nlp.cn.anaphora.train;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 组合文件匹配
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class DocGroupMacher {
	 LinkedList<File> files;
	FileGroup fg;
	Charset charset;
	
	public DocGroupMacher(String path) {
		this(path, "UTF-8");
	}
	
	public DocGroupMacher(String path, String charsetName) {
		files = new LinkedList<File>();
		allPath(new File(path));
		charset = Charset.forName(charsetName);
	}

	private void allPath(File handle) {
		if (handle.isFile()&&(handle.toString().contains(".apf.")||handle.toString().contains(".sgm")))
			files.add(handle);
		else if (handle.isDirectory()) {
			for (File sub : Arrays.asList(handle.listFiles()))
				allPath(sub);
		}
	}

	public boolean hasNext() {
		if (files.isEmpty())
			return false;
		nextDocument();
		return true;
	}
	
	public int getLength(){
		return this.files.size()/2;
	}
	public FileGroup next() {
		return fg;
	}

	private void nextDocument() {
		StringBuffer buff = new StringBuffer();
		File f = files.poll();
		Iterator it;File f1= null;
		it=files.iterator();
		while(it.hasNext()){
			f1=(File) it.next();
			if(f.toString().contains(f1.toString().subSequence(0, 12))){
				it.remove();
				break;
			}				
		}
		if(f.toString().contains(".apf."))
			fg=new FileGroup(f1,f);
		else
			fg=new FileGroup(f,f1);
	}

}