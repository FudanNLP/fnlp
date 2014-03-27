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

package org.fnlp.train.prepare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;

import org.fnlp.util.MyFiles;

public class FileCombine {
	
	
	public void combineFiles(List<File> files,String saveFileName) throws Exception{ 
		File mFile=new File(saveFileName); 

		if(!mFile.exists()){ 
			mFile.createNewFile(); 
		} 
		FileChannel mFileChannel = new FileOutputStream(mFile).getChannel(); 
		FileChannel inFileChannel; 
		for(File file:files){ 

			inFileChannel=new FileInputStream(file).getChannel(); 
			inFileChannel.transferTo(0, inFileChannel.size(), mFileChannel); 

			inFileChannel.close(); 
		} 

		mFileChannel.close(); 

	} 

	public static void main(String[] args) throws Exception { 
		FileCombine fc=new FileCombine(); 
		List<File> files = MyFiles.getAllFiles("./tmpdata/FNLPDATA/", ".cws");
		fc.combineFiles(files, "./tmpdata/FNLPDATA/all.cws");  
		System.out.println(new Date().toString());
		System.out.println("Done!");
	}
}