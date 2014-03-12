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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.fnlp.ml.types.Instance;
/**
 * 将不含第三人称代词和指示代词的文件过滤掉
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class DocFilter {

	BufferedReader orReader;
	BufferedReader markReader;
	LinkedList<Instance> list;
	FileGroup fGroup;
	private String opath;
	public DocFilter(String file,String opath){
		this.opath = opath;
		try {
			list = new LinkedList<Instance>();
			DocGroupMacher MDReader = new DocGroupMacher(file);		
			FileInputStream in1=null;
			FileInputStream in2=null;
			while(MDReader.hasNext()){
				fGroup=MDReader.next();
				 in1= new FileInputStream(fGroup.getOrgFile());
				orReader = new BufferedReader(new InputStreamReader(in1,"UTF-8"));
				 in2 = new FileInputStream(fGroup.getMarkFile());
				markReader = new BufferedReader(new InputStreamReader(in2,"UTF-8"));
				this.doit();
			}
			in1.close();in2.close();
		
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
	}
	private void doit()  {
		StringBuffer s0 =new StringBuffer(); String s3=null;
		StringBuffer s1=new StringBuffer(); 
		StringBuffer ss1 = new StringBuffer();//StringBuffer markStr = new StringBuffer();
			try {
				while ((s3=orReader.readLine())!=null){
					s0.append(s3);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while ((s3=markReader.readLine())!=null){
					s1.append(s3);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MarkFileManager mfp = new MarkFileManager(s1.toString());
			ss1 = mfp.getAllstr();
			boolean bl = false;
			if(ss1.toString().contains("他")||ss1.toString().contains("她")||ss1.toString().contains("它")||ss1.toString().contains("该")||ss1.toString().contains("这")||ss1.toString().contains("那"))
				bl = true;			
			if(bl){System.out.print(s0.toString());
			this.writeOut( opath+fGroup.getOrgFile().getName(),s0.toString());
				this.writeOut( opath+fGroup.getMarkFile().getName(),s1.toString());
				
			}
			
	}
	private void writeOut(String file ,String str){
		File f = new File(file);
		Writer out = null;
		try {
			
			out = new OutputStreamWriter(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]) throws IOException{
		DocFilter fgr = new DocFilter(
				"\\\\10.141.200.3\\d$\\NLP\\Datasets\\ACE 2005\\Chinese\\wl\\adj",
				"./tmp/ar/");
	    
	}
}