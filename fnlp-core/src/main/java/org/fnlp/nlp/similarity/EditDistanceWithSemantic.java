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

package org.fnlp.nlp.similarity;

import java.util.HashSet;

import org.fnlp.nlp.corpus.CiLin;

/**
 * @author xpqiu
 * @version 1.0
 * @since 1.0
 */
public class EditDistanceWithSemantic extends EditDistance implements ISimilarity <String>{
	
	private int wordlen;
	private HashSet<String> synSet;
	
	public EditDistanceWithSemantic(){
		wordlen = 2;
		
    	String dataFile = "\\\\10.11.7.3\\f$\\对于共享版《同义词词林》的改进\\improvedThesaurus.data";
		synSet = (HashSet<String>) CiLin.buildSynonymSet(dataFile);
	}

	/**
	 * 将x转换到y的编辑距离，可以自定义一些代价
	 */
	public float calc(String item1, String item2) {   
		
		String str1 = (String) item1;
		String str2 = (String) item2;
		float d[][];  //矩阵   
		int n = str1.length();   
		int m = str2.length();   
		int i;  //遍历str1的   
		int j;  //遍历str2的   
		char ch1;   //str1的   
		char ch2;   //str2的   
		int cost;   //记录相同字符,在某个矩阵位置值的增量,不是0就是1   
		if(n == 0) {   
			return m;   
		}   
		if(m == 0) {   
			return n;   
		}   
		d = new float[n+1][m+1];   
		for(i=0; i<=n; i++) {    //初始化第一列   
			d[i][0] = i;   
		}   
		for(j=0; j<=m; j++) {    //初始化第一行   
			d[0][j] = j;   
		}   
		for(i=1; i<=n; i++) {    //遍历str1   
			char cX = str1.charAt(i-1);   
			//去匹配str2   
			for(j=1; j<=m; j++) {   
				
				//根据同义计算未来代价
				for(int ii=1;ii<=wordlen;ii++){
					if(ii+i-1>str1.length())
						break;
					for(int jj=1;jj<=wordlen;jj++){
						if(jj+j-1>str2.length())
							break;
						String combine = str1.substring(i-1, ii+i-1)+"|"+str2.substring(j-1,jj+j-1);
						//System.out.println(combine);
						if(synSet.contains(combine)){
							if(d[i+ii-1][j+jj-1]>0)
								d[i+ii-1][j+jj-1]=Math.min(d[i+ii-1][j+jj-1],d[i-1][j-1]+0.1f);
							else
								d[i+ii-1][j+jj-1]=d[i-1][j-1]+0.1f;
						}
							
					}
				}
				
				char cY = str2.charAt(j-1);   
				float temp = (cX == cY ? d[i-1][j-1] // match
							: costReplace(cX,cY) + d[i-1][j-1]);
				if(d[i][j]>0){
					temp = Math.min(temp, d[i][j]);
				}
				d[i][j] = Math.min(temp, // 替换代价
						             Math.min(costDel(cX)+d[i-1][j], // 删除代价
						            		costIns(cY)+d[i][j-1])); // 插入代价
			
				
			}   
		}   
		return d[n][m];   
	}   

	

	
    public static void main(String[] args) {
    	EditDistanceWithSemantic ed = new EditDistanceWithSemantic();
    	
        String str1 = "发行时间 ";   
        String str2 = "生日";   
        System.out.println("ld="+ed.calc(str1, str2));   
        //System.out.println("sim="+ed.sim(str1, str2));   
    }

}