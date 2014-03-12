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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
/**
 * 标记文件内容处理
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MarkFileManager {
	private LinkedList<StringBuffer> ELstr;//实体链中的元素
	private StringBuffer Allstr;  //所有元素
	public MarkFileManager(String str){
		ELstr = new LinkedList<StringBuffer>();
		Allstr = new StringBuffer();
		this.doProducter(str);
		
	}
	
	private void doProducter(String str){
		String strBuffer1 = null;
		StringBuffer strb = null;

		int j = 0; int i = 0; 
		while(i>=0){              //对一个实体链操作
			strb = new StringBuffer();
			int k = 0;
			i = str.indexOf("<entity ");
				
			if(i<0)
				break;
			j = str.indexOf("</entity>",i)+8;
			strBuffer1 =str .substring(i, j);
			str = str.substring(j);
			while(k>=0){              //对一个entity_mention 操作
				k = strBuffer1.indexOf("<charseq ");
				if(k<0)
					break;
				k = strBuffer1.indexOf(">",k);
				j = strBuffer1.indexOf("</charseq>",k);
				strb.append(strBuffer1.substring(k+1, j));
				strb.append(" ");
			
				strBuffer1 = strBuffer1.substring(j);
			}
			ELstr.add(strb);
			Allstr.append(strb);
			Allstr.append(" ");
		}
	}
	public LinkedList<StringBuffer> getELstr(){
		return this.ELstr;
	}
	public StringBuffer getAllstr(){
		return this.Allstr;
	}
	public static void main(String agrs[]){
		StringBuffer buff = new StringBuffer();
		File f = new File("E:\\学习数据\\研一（下）\\ace2005\\Chinese\\bn\\adj\\CBS20001001.1000.0041.apf.xml");
		try {
			BufferedReader cf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "UTF-8"));
			String line = null;
			while((line = cf.readLine()) != null)	{
				buff.append(line);
			}
			cf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		MarkFileManager mfp = new MarkFileManager(buff.toString());
		Iterator it = mfp.getELstr().iterator();
		while(it.hasNext())
		System.out.print(it.next().toString()+"\n");
	}

}