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

package org.fnlp.nlp.corpus.ctbconvert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class CorpusManager {

	public static void main(String args[]) throws IOException{
		File file = new File("./ctb/data");  //为目录
		StringBuffer sb = new StringBuffer();
		for(File sub : Arrays.asList(file.listFiles())){
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sub),Charset.forName("UTF-8")));
			String str;
			while((str = br.readLine())!=null){
				if(str.length()!=0&&!str.startsWith("<")){
					sb.append(str+"\n");
				}				
			}
		}
		Writer wr = new OutputStreamWriter(new FileOutputStream(new File("./ctb/penn.txt")));//输出目录
		wr.write(sb.toString());
		wr.close();
	}
	
}