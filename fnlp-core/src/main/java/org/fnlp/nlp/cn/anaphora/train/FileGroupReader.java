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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.anaphora.EntitiesGetter;
import org.fnlp.nlp.cn.anaphora.Entity;
import org.fnlp.nlp.cn.anaphora.EntityGroup;
import org.fnlp.nlp.cn.tag.POSTagger;
/**
 * 读入组合文件
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class FileGroupReader {

	public static POSTagger tag;
	BufferedReader orReader;
	BufferedReader markReader;
	LinkedList<Instance> list;
	 	
	public FileGroupReader(String file){	
		try {	
			list = new LinkedList<Instance>();
			DocGroupMacher MDReader = new DocGroupMacher(file);
			FileGroup fGroup = null;
			FileInputStream in1=null;
			FileInputStream in2=null;
			while(MDReader.hasNext()){
				fGroup=MDReader.next();
				 in1= new FileInputStream(fGroup.getOrgFile());
				orReader = new BufferedReader(new InputStreamReader(in1,
						"UTF-8"));
				 in2 = new FileInputStream(fGroup.getMarkFile());
				markReader = new BufferedReader(new InputStreamReader(in2,
						"UTF-8"));
				this.buidList();
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
	private String getContent(String str){
		StringBuffer ss1 = new StringBuffer();int i = 0;String ss =null;int j = 0;
		while(i>=0){
			i = str.indexOf("<TURN>");
			if(i<0)
				break;
			j = str.indexOf("</TURN>");
			ss = str.substring(i+6,j);
			str = str.substring(j+7);
			ss1.append(ss);
		}
		return ss1.toString();
	}
	private void buidList() throws Exception  {
		
		
		StringBuffer sb0 =new StringBuffer();Entity et = null;
		Entity et1 = null;String s3=null;
		StringBuffer sb1=new StringBuffer();
		LinkedList<Entity> ll = new LinkedList<Entity>();		
		StringBuffer sb2 = new StringBuffer(); 
			try {
				while ((s3=orReader.readLine())!=null){
					sb0.append(s3);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while ((s3=markReader.readLine())!=null){
					sb1.append(s3);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			EntitiesGetter elp = new EntitiesGetter();
			MarkFileManager mfp = new MarkFileManager(sb1.toString());
			String s = sb0.toString();
			String[][][] tags = tag.tag2DoubleArray(s);
			ll = elp.parse(tags);
			Iterator it =null;Iterator it1 =null;	
			LinkedList<StringBuffer> llsb = mfp.getELstr(); 
			it = ll.iterator();LinkedList<Entity> etLL = new LinkedList<Entity>(); 
			while(it.hasNext()){	
				et = (Entity) it.next();
				while(!et.getIsResolution()&&it.hasNext()){
					etLL.add(et);
					et = (Entity)it.next();
				}
				it1 = etLL.iterator();
				boolean bl1 = false;
				while(it1.hasNext()){
					int flag = 0;
					et1 = (Entity) it1.next();
					Iterator<StringBuffer> it2 = llsb.iterator();
					while(it2.hasNext()){
						sb2 = it2.next();
						if(sb2.toString().contains(et.getData())&&sb2.toString().contains(et1.getData())){
							flag = 1;
							bl1 = true;
							break;
						}
					}
					if(bl1){
						list.add(new Instance(new EntityGroup(et1,et),flag));
					}
				}
				etLL = new LinkedList<Entity>();
				etLL.add(et);
			}
					
			
			
	}
	
	public boolean hasNext() {
		return (!list.isEmpty());
	}
	
	public Instance next() {
		if(this.hasNext())
			return list.poll();
		else
			return null;
	}
	public static void main(String args[]) throws IOException{
		FileGroupReader fgr = new FileGroupReader("F:\\媒体 学习\\研一（下）\\cc");
		EntityGroup eg=null;Instance in = null;int i = 0;int j = 0;
		while(!fgr.list.isEmpty())
		{ in=  fgr.list.poll();eg=(EntityGroup) in.getData();
			if(in.getTarget().toString().equals("1")){i++;
			}
			else
				j++;	
		}	
		System.out.print(i);
		System.out.print("\n");
		System.out.print(j);
	}
}