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
