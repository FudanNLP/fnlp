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

package org.fnlp.wsytry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
	static public void main(String args[]){

		try {
			String fileName="./tmp/POS_tag_dataset.txt";
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					fos, "UTF8"));
			
			int l=1;
			int r=4411;
			String pre[]={"000","000","00","0",""};
			int fileNum=0;
			int failedNum=-2;
			List list=new ArrayList();
			for(int i=l;i<=r;i++){
				//if(i>l+1) break;
				String num=Integer.toString(i);
				String inFileName="chtb_"+pre[num.length()]+num;
				if(i<=931)
					inFileName+=".nw.pos";
				else if(i<=1151)
					inFileName+=".mz.pos";
				else if(i<=3145)
					inFileName+=".bn.pos";
				else if(i<=4050)
					inFileName+=".nw.pos";
				else if(i<=4111)
					inFileName+=".bn.pos";
				else if(i<=4197)
					inFileName+=".bc.pos";
				else if(i<=4411)
					inFileName+=".wb.pos";
				if(genPOSdata("./tmp/postagged/"+inFileName,bout)){
					fileNum++;
					if(fileNum%100==0)
						System.out.println("No."+fileNum+": "+inFileName+"Complete");
				}
				else{
					if(i-failedNum>1)
						list.add(i);
					failedNum=i;
				}
			}
			bout.close();
			for(int i=0;i<list.size();i++)
				System.out.println(list.get(i));
			System.out.println("totalFile:"+fileNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static boolean genPOSdata(String fileName,BufferedWriter bout){
		//System.out.println(fileName);
		File file=new File(fileName);
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

			return false;
		}
		
		BufferedReader reader=new BufferedReader(fileReader);
		String str;
		try {
			while((str=reader.readLine())!=null){
				//bout.write(str+"\n");
				if(str.length()<2)
					continue;
				if(str.contains("（_PU 完_VV ）_PU "))
					continue;
				if(!str.startsWith("<")||str.startsWith("<_")){
					int st=-1,ed=-1;
					while(ed<str.length()){
						st=str.indexOf('_', st+1);
						if(st==-1)
							break;
						ed=str.indexOf(' ',st+1);
						if(ed==-1)
							ed=str.length();
						String substr=str.substring(st+1, ed);
						if(substr.contains("ULLETＪ.路易斯_NR"))
								substr="NR";
						bout.write(substr+" ");
					}
					bout.write("\n");
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}