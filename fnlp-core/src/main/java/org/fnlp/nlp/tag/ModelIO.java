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

package org.fnlp.nlp.tag;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
/**
 * 统一处理序列标注模型的读写操作
 * @author xpqiu
 */
public class ModelIO {
	
	public static TempletGroup templets;
	public static Linear cl;

	public static void saveTo(String modelfile, TempletGroup templets, Linear cl) throws IOException {
		File  f = new File(modelfile);
		File p = f.getParentFile();
		if(p!=null&&!p.exists()){
			p.mkdirs();
		}
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(modelfile))));
		out.writeObject(templets);
		out.writeObject(cl);
		out.close();
	}

	public static void loadFrom(String modelfile) throws IOException,
	ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(modelfile))));
		templets = (TempletGroup) in.readObject();
		cl = (Linear) in.readObject();
		in.close();
	}
	
}