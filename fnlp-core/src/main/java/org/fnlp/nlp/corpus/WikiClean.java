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

package org.fnlp.nlp.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.util.MyCollection;
import org.fnlp.util.exception.LoadModelException;

/**
 * 
 * @author Xipeng Qiu E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2014年10月29日 下午4:56:55
 */
public class WikiClean {
	
	static String infile = "../tmp/wiki_00";
	static String simpfile = "../tmp/wiki_simp";
	static String segfile = "../tmp/wiki_simp_seg";
	static String segfile_mini = "../tmp/wiki_mini_simp_seg";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
//		toSimp();
//		seg();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(segfile ), "utf8"));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				segfile_mini), "utf8"));

		String line = null;	
		int count=0;
		int ncount=0;
		while ((line = in.readLine()) != null) {
			if(line.length()==0){
				
			}else if(line.startsWith("<doc")){
				count++;				
			}else if(line.startsWith("</doc>")){
				count--;
				if(++ncount==100)
					break;
			}
			out.append(line);
			out.append("\n");
		}
		System.out.println(count);
		in.close();
		out.close();

	}
	
	private static void seg() throws IOException, LoadModelException {
		CWSTagger seg = new CWSTagger("../models/seg.m");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(simpfile ), "utf8"));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				segfile), "utf8"));

		String line = null;	
		int count=0;
		while ((line = in.readLine()) != null) {
			if(line.length()==0){
				
			}else if(line.startsWith("<doc")){
				count++;				
			}else if(line.startsWith("</doc>")){
				count--;
			}else{
				line = seg.tag(line);			
			}
			out.append(line);
			out.append("\n");
		}
		System.out.println(count);
		in.close();
		out.close();
	}

	private static void toSimp() throws IOException {
		ChineseTrans ct = new ChineseTrans();

		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(infile ), "utf8"));

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				simpfile), "utf8"));

		String line = null;	
		int count=0;
		while ((line = in.readLine()) != null) {
			if(line.length()==0){
				
			}else if(line.startsWith("<doc")){
				count++;				
			}else if(line.startsWith("</doc>")){
				count--;
			}else{
				line = ct.toSimp(line);				
			}
			out.append(line);
			out.append("\n");
		}
		System.out.println(count);
		in.close();
		out.close();
	}


}