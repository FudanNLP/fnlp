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

package org.fnlp.nlp.duplicate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.fnlp.nlp.duplicate.FingerPrint.Type;


public class DupDetector {
	public  TreeMap<Integer, DocSim> map = new TreeMap<Integer, DocSim>();

	public  TreeSet<DocSim> resultMap = 
			new TreeSet<DocSim>();

	private ISimilarity sim=null;

	private ArrayList<Documents> docs;

	private int numThreads;
	
	/**
	 * 是否输出频次
	 */
	private boolean bFreq = false;

	private TreeSet<DocSim> dsMap;

	private int minLen = 30;

	public DupDetector(int i) {
		numThreads = i;
	}


	public  void loadData(String fileList) throws IOException {
		int id=0;
		FileInputStream fi = new FileInputStream(fileList);
		Scanner scanner = new Scanner(fi, "UTF8");
		docs = new ArrayList<Documents>();
		while(scanner.hasNext()) {
			String ss = scanner.nextLine().trim();	
			if(ss.length()<minLen )
				continue;
			Documents d = new Documents(ss);
			docs.add(d);
		}
		scanner.close();
		fi.close();
	}


	public List<String> getTopMessage(int num) {
		List<String> list = new ArrayList<String>();			

		int i=0;
		Iterator<DocSim> iter =dsMap.iterator();
		while (iter.hasNext()&&i++<num) {
			DocSim ds = iter.next();
			String s = docs.get(ds.ids.get(0)).content;
			if(bFreq){
				s = String.valueOf(ds.ids.size())+ "\n"+ s;
			}
			list.add(s);
		}		
		return list;
	}


	public  void sort(String file,Type type) throws Exception {
		loadData(file);
		sim = new SimilaritySlow(numThreads, type);
		dsMap = sim.duplicate(docs);

	}
	public void sort2File(String file, String ofile,Type type,int num) throws Exception {
		sort(file,type);
		List<String> list = getTopMessage(num);
		PrintWriter pw = new PrintWriter(ofile, "utf8");

		for(int i = 0; i < list.size(); i++) {
			pw.println(list.get(i));
		}
		pw.close();
	}



	public void sortFeats2File(String file, String ofile, Type type,
			int num) throws Exception {
		sort(file,type);
		PrintWriter pw = new PrintWriter(ofile, "utf8");
		int i=0;
		Iterator<DocSim> iter = dsMap.iterator();
		
		while (iter.hasNext()&&i<num) {
			Set<String> mset = new TreeSet<String>();
			DocSim ds = iter.next();
			for(int idx = 0;idx<ds.ids.size();idx++){
				Set set=FingerPrint.featureset(docs.get(ds.ids.get(idx)).content,type);
				mset.addAll(set);
			}
			if(mset.size()<=1||mset.size()>10)
				continue;
			pw.println(ds.ids.size());
			Iterator<String> it = mset.iterator();
			while(it.hasNext()){
				pw.print(it.next());
				pw.print(" ");
			}
			pw.println();
			i++;
		}
		pw.close();

	}
	public static void main(String[] args) throws Exception {
		DupDetector s = new DupDetector(8);
		String file;
		if(args.length>0)
			file=args[0];
		else
			file="./tmp/filterByTopic/filterhealth.y";
		s.sort2File(file,file+".s", Type.Char, 500);
		System.out.println("done");
	}
}