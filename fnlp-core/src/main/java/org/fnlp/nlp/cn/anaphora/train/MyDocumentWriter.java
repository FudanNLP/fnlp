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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.anaphora.ARInstanceGetter;
import org.fnlp.nlp.cn.anaphora.EntitiesGetter;
import org.fnlp.nlp.cn.anaphora.FeatureGeter;
import org.fnlp.nlp.cn.tag.POSTagger;

/**
 * 特征训练文件生成
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class MyDocumentWriter {

	private LinkedList<Instance> llist;
	private FileGroupReader fgr;
	private String str;
	public MyDocumentWriter(String str){
		this.str = str;
		fgr = new FileGroupReader(str);
		this.llist = new LinkedList<Instance>();
		Instance in = null; FeatureGeter fp = null;
		ARInstanceGetter arip = null;
		while(fgr.hasNext()){
			in = fgr.next();
			fp = new FeatureGeter(in);
			arip = new ARInstanceGetter(fp);
			llist.add(arip.getInstance());			
		}

	}
	
	public void writeOut(String writer){
		MyDocumentWriter dr = new MyDocumentWriter(str);
		Iterator it =dr.llist.iterator();
		File f = new File(writer);
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Instance mi = null;
		while(it.hasNext()){
			mi = (Instance) it.next();
			try {		
				out.write(mi.getTarget()+" ");
				int[] dat = (int[]) mi.getData();
				for(int i=0;i<dat.length;i++)
					out.write(dat[i]+" ");
				out.write('\n');
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public LinkedList<Instance> getLlist(){
		return this.llist;
		
	}
	public static void main(String args[]) throws Exception{
		FileGroupReader.tag = new POSTagger("../models/seg.m", "../models/pos.m");
		
		MyDocumentWriter dr1 = new MyDocumentWriter("../tmp/ar");
		dr1.writeOut("../tmp/ar-train.txt");
		System.out.print("已经写入文档");
	}
}