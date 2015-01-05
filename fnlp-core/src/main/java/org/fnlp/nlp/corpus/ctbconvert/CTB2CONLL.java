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
import java.io.BufferedWriter;
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
/**
 * 调用Penn2Malt转换
 * @author Xipeng
 *
 */
public class CTB2CONLL {
	public static void main(String[] args)
	{
		try
		{
			String ls_1;
			Process process =null;
//			File handle = new File("../tmp/ctb_v1/data");
			File handle = new File("../tmp/ctb_v6/data/bracketed");
			
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("../tmp/malt.train"), "UTF-8"));
			for (File sub : Arrays.asList(handle.listFiles())){
				String file = sub.getAbsolutePath();
				if(!file.endsWith(".fid"))
					continue;
				clean(file);
				process = Runtime.getRuntime().exec("cmd /c java -jar ../tmp/Penn2Malt.jar "+file+" ../tmp/headrules.txt 3 2 chtb");			
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				while ( (ls_1=bufferedReader.readLine()) != null)
				{
					System.out.println(ls_1);
				}
				bufferedReader = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				while ( (ls_1=bufferedReader.readLine()) != null)
				{
					System.out.println(ls_1);
				}
			}			
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}

	/**
	 * 对ctb的格式进行预处理，去掉尖括号注释信息，只保留圆括号里的内容
	 * @param file 文件名
	 * @throws IOException
	 * 下午5:34:24
	 */
	private static void clean(String file) throws IOException {
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("utf8")));
		String str;
		while((str = br.readLine())!=null){
			if(str.length()!=0&&!str.trim().startsWith("<")){
				if(str.equalsIgnoreCase("root"))
					continue;
				if(str.contains("</HEADER> ")||str.contains("</HEADLINE>"))
					continue;
				sb.append(str+"\n");

			}				
		}

		br.close();
		Writer wr = new OutputStreamWriter(new FileOutputStream(new File(file)),Charset.forName("gbk"));//输出目录
		wr.write(sb.toString());
		wr.close();

	}
}
