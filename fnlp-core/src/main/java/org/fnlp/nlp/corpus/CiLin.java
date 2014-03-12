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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;


/**
 * 本类用来分析《哈工大同义词林》
 * @author Administrator
 * @version 1.0
 * @since 1.0
 */
public class CiLin {

	/**
	 * 找出同义的词对,建造hashset;
	 * @return  同义词集合
	 */
	public static HashSet buildSynonymSet(String fileName){

		try {		
			InputStreamReader  read = new InputStreamReader (new FileInputStream(fileName),"utf-8");
			BufferedReader bin = new BufferedReader(read);
			HashSet<String> synSet = new HashSet<String>();
			int c=0;
			String str = bin.readLine();
			while(str!=null&&str.length()==0){
				String[] strs = str.trim().split(" ");
				if(strs[0].endsWith("=")){
					//System.out.println(strs[0]);
					int wordNum = Integer.parseInt(strs[1]);

					for(int i=2;i<2+wordNum-1;i++){
						for(int j=i+1;j<2+wordNum;j++){

							String combine1 = strs[i]+"|"+strs[j];
							System.out.println(combine1 + c);
							synSet.add(combine1);
							String combine2 = strs[j]+"|"+strs[i];
							synSet.add(combine2);
							c++;
						}
					}
				}else{

				}
				str = bin.readLine();
			}
			return synSet;
		}catch(Exception e){
			return null;
		}


	}

	public static void main(String[] argv){
		HashSet<String> synSet = buildSynonymSet("\\\\10.11.7.3\\f$\\对于共享版《同义词词林》的改进\\improvedThesaurus.data");
	}

}