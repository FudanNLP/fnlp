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
/**
 * 文件组合，包括原文件和标记好的文件
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class FileGroup {
	private File orgFile;   // 原文件
	private File markFile;   //标记文件
	
	public FileGroup(File orgFile,File markFile){
		this.orgFile= orgFile;
		this.markFile = markFile;
		
	}
	
	public File getOrgFile(){
		return orgFile;
	}
	public File getMarkFile(){
		return markFile;
	}
	public void setOrgFile(File orgFile){
		this.orgFile = orgFile;
	}
	public void setMarkFile(File markFile){
		this.markFile = markFile;
	}
}