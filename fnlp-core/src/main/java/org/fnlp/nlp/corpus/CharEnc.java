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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class CharEnc {

	private static boolean labeled=false;

	
	public static void main(String[] args) throws Exception {

		String input1 ="D:/Datasets/sighan2006/processed";
		
		File f = new File(input1);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				processLabeledData(files[i].toString(),"utf8","gbk");
			}
		}

		System.out.println("Done");
	}


	public static void processLabeledData(String input,String enc1, String enc2) throws Exception{
		FileInputStream is = new FileInputStream(input);
//		is.skip(3); //skip BOM
		BufferedReader r = new BufferedReader(
				new InputStreamReader(is, enc1));
		OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(input+"."+enc2), enc2);
		while(true) {
			String sent = r.readLine();
			if(null == sent) break;			
			w.write(sent);
			w.write('\n');
		}
		r.close();
		w.close();
	}

}