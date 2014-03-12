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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
			File handle = new File("./tmpdata/ctb/data3");
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("./tmpdata/malt.train"), "UTF-8"));
			for (File sub : Arrays.asList(handle.listFiles())){
				String str = sub.getAbsolutePath();
				process = Runtime.getRuntime().exec("cmd /c java -jar ./tmpdata/ctb/Penn2Malt.jar "+str+" ./tmpdata/ctb/headrules.txt 3 2 chtb");			
				BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
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
}