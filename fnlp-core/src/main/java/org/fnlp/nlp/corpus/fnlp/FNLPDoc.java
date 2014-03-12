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

package org.fnlp.nlp.corpus.fnlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.fnlp.util.MyFiles;
import org.fnlp.util.UnicodeReader;

/**
 * FudanNLP标准数据格式
 * @since FudanNLP 1.5
 */
public class FNLPDoc {

	private static AtomicInteger num = new AtomicInteger();

	/**
	 * 文档类别
	 */
	private String classes;
	/**
	 * 文档文件名
	 */
	public String name;



	public LinkedList<FNLPSent> sentences = new LinkedList<FNLPSent>();



	private Map<Integer,Integer> anaphora;

	public FNLPDoc(){
		name = String.valueOf(num.addAndGet(1));
	}

	public FNLPDoc(List<String> carrier) {
		parse(carrier);
	}

	public void read(String file){
		File f = new File(file);
		if(f.exists())
			read(f);
	}
	
	public void read(File file){
		name = file.getName();
		List<String> carrier = new ArrayList<String>();
		BufferedReader bfr =null;
		try {
			FileInputStream in = new FileInputStream(file);
			bfr = new BufferedReader(new InputStreamReader(in,Charset.forName("UTF-8")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String str = null;
		try {
			while((str=bfr.readLine())!=null){
				carrier.add(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parse(carrier);
	}

	private void parse(List<String> carrier) {
		Iterator<String> it = carrier.iterator();
		int bi=0;
		int ti = 0;
		while(it.hasNext()){
			String line = it.next();
			ti++;
			if(line.equals("<doc>")){
				continue;	
			}else if(line.startsWith("<c>")){
				int idx1 = line.indexOf("</c>");
				this.classes = line.substring(3,idx1);
				continue;
			}else if(line.startsWith("<name>")){
				int idx1 = line.indexOf("</name>");
				this.name = line.substring(6,idx1);
				continue;
			}else if(line.equals("</txt>")){//正文结束
				List<String> list = carrier.subList(bi, ti-1);
				if(list.size()>0){
					FNLPSent sent = new FNLPSent(list);
					sentences.add(sent);
				}
				continue;
			}else if(line.equals("<txt>")){	
				bi=ti;
				continue;				
			}else if(line.equals("</doc>")){		
				break;				
			}else if(line.length()==0){//空行，标志一个句子的结束
				List<String> list = carrier.subList(bi, ti-1);
				if(list.size()>0){
					FNLPSent sent = new FNLPSent(list);
					sentences.add(sent);
				}
				bi=ti;
				continue;
			}else{
				
			}
		}
	}
	
	
	
	
	public String toString(){
		StringBuffer sb = new StringBuffer();

		sb.append("<doc>"+"\n");
		if(classes!=null)
			sb.append("<c>"+classes+"</c>\n");
		if(name!=null)
			sb.append("<name>"+name+"</name>\n");
		sb.append("<txt>"+"\n");
		Iterator<FNLPSent> it = sentences.iterator();
		while(it.hasNext()){
			FNLPSent sent = it.next();
			sb.append(sent.toString());
			if(it.hasNext())
				sb.append("\n");
		}
		sb.append("</txt>"+"\n");
		sb.append("</doc>");
		return sb.toString();
	}

	public void write(String path){
		Writer out = null;
		String file = path + "/" + name;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file),"utf8");
			out.write(this.toString());
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}



	public void add(FNLPSent sent) {
		sentences.add(sent);		
	}

	public void clear() {
		sentences.clear();		
	}

    public LinkedList<FNLPSent> getSent() {
        return this.sentences;
    }

	public FNLPSent getSent(int idx) {
		if(idx<sentences.size())
			return sentences.get(idx);
		else
			return null;
		
	}
}