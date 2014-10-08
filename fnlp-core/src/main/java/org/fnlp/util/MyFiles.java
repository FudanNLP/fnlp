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

package org.fnlp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.nlp.pipe.seq.String2Sequence;
/**
 * 自定义文件操作类
 * @author xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyFiles {

	private static void allPath(List<File> files, File handle, String suffix) {
		if (handle.isFile()){
			if(suffix==null||handle.getName().endsWith(suffix))
				files.add(handle);
		}
		else if (handle.isDirectory()) {
			for (File sub : Arrays.asList(handle.listFiles()))
				allPath(files, sub,suffix);
		}
	}

	public static List<File> getAllFiles(String path, String suffix) {
		ArrayList<File> files = new ArrayList<File>();
		allPath(files, new File(path), suffix);
		return files;
	}
	/**
	 * 将字符串写到文件
	 * @param path
	 * @param str
	 * @throws IOException
	 */
	public static void write(String path, String str) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				path), "utf8"));
		out.append(str);
		out.close();		
	}
	public static void write(String path, Serializable o) {
		try {
			ObjectOutputStream outstream = new ObjectOutputStream(
					new GZIPOutputStream(new FileOutputStream(path)));
			outstream.writeObject(o);
			outstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 转换文件编码
	 * @param infile
	 * @param outfile
	 * @param enc1
	 * @param enc2
	 * @throws IOException
	 */
	public static void conver(String infile, String outfile, String enc1, String enc2) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile), enc1));
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				outfile), enc2));

		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();	
			if(line.length()==0)
				continue;
			out.write(line);
			out.newLine();
		}
		in.close();
		out.close();
		
	}
	
	public static Object read(String path) {
		Object o=null;
		try {
			ObjectInputStream instream = new ObjectInputStream(new GZIPInputStream(
					new FileInputStream(path)));
			o = instream.readObject();
			instream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public static Object loadObject(String path) throws IOException,
    ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                new GZIPInputStream(new FileInputStream(path))));
        Object obj = in.readObject();
        in.close();
        return obj;
    }

	public static void saveObject(String path, Object obj) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(path))));
		out.writeObject(obj);
		out.close();
	}
	/**
	 * 从文件中读入完整的字符串，包括\r\n等。
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static String loadString(String file) throws IOException {
		StringBuilder sb = new StringBuilder();
		UnicodeReader reader = new UnicodeReader(new FileInputStream(file), "utf8");
		int n;	
		char[] cs = new char[256];
		while ((n = reader.read(cs)) != -1) {		
			sb.append(Arrays.copyOfRange(cs, 0, n));			
		}
		return sb.toString();
	}

	/**
	 * 删除文件
	 * @param filename
	 */
	public static void delete(String filename) {
		File file = new File(filename);
		if(file.exists()){
			file.delete();
		}
	}
	
	/**
	 * 合并文件
	 * @param saveFileName
	 * @param files
	 * @throws Exception
	 */
	public static void combine(String saveFileName, String... files) throws Exception{
		File mFile=new File(saveFileName); 

		if(!mFile.exists()){ 
			mFile.createNewFile(); 
		} 
		
		FileOutputStream os = new FileOutputStream(mFile);
		FileChannel mFileChannel = os.getChannel(); 
		FileChannel inFileChannel; 
		for(String file:files){
			File f = new File(file);
			if(!f.exists())
				continue;
			inFileChannel=new FileInputStream(f).getChannel(); 
			inFileChannel.transferTo(0, inFileChannel.size(), mFileChannel); 

			inFileChannel.close(); 
		} 

		mFileChannel.close(); 
		os.close();
	}
	
	
	/**
	 * 合并文件
	 * @param saveFileName
	 * @param files
	 * @throws Exception
	 */
	public static void combine(String saveFileName, File...files) throws Exception{ 
		File mFile=new File(saveFileName); 

		if(!mFile.exists()){ 
			mFile.createNewFile(); 
		} 
		
		FileOutputStream os = new FileOutputStream(mFile);
		FileChannel mFileChannel = os.getChannel(); 
		FileChannel inFileChannel; 
		for(File file:files){ 
			if(!file.exists())
				continue;
			inFileChannel=new FileInputStream(file).getChannel(); 
			inFileChannel.transferTo(0, inFileChannel.size(), mFileChannel); 

			inFileChannel.close(); 
		} 

		mFileChannel.close(); 
		os.close();
	}
}