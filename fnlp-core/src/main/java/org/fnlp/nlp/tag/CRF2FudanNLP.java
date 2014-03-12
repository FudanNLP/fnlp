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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
/**
 * 将crf++模型转为FudanNLP模型
 * @author xpqiu
 *
 */
public class CRF2FudanNLP {

	public void convert(String from, String to) {
	try {
		BufferedReader rd = new BufferedReader(
					new InputStreamReader(new FileInputStream(from), "gbk"));
		
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream (
				new GZIPOutputStream (new FileOutputStream(to))));
		AlphabetFactory factory = AlphabetFactory.buildFactory();
		LabelAlphabet labels = factory.DefaultLabelAlphabet();
		IFeatureAlphabet features = factory.DefaultFeatureAlphabet();;
		String s;
		rd.readLine();	//version
		List lst = new ArrayList();			//template
		while(true) {
			s = rd.readLine();
			if(s.isEmpty()) break;
			lst.add(s);
		}
		out.writeInt(lst.size());
		Iterator it1 = lst.iterator();
		while(it1.hasNext()) {
			out.writeObject(it1.next());
		}
		
		s = rd.readLine();					//#label
		int nLabel = Integer.parseInt(s);
		System.out.println(nLabel);
		for(int i=0; i<nLabel; i++) {
			s = rd.readLine();				//label
			labels.lookupIndex(s);
		}
		out.writeObject(labels);
		rd.readLine();						//blank line
		rd.readLine();						//#column
		rd.readLine();						//blank line
		
		TreeMap map = new TreeMap();
		String[] arr;
		
		s = rd.readLine();					//#feature
		int nFeature = Integer.parseInt(s);
		System.out.println(nFeature);
		for(int i=0; i<nFeature; i++) {
			s = rd.readLine();				//feature: string offset
			arr = s.split("\t");
			map.put(Integer.parseInt(arr[1]), arr[0]);
		}
		
		rd.readLine();						//blank line
		s = rd.readLine();					//#index
		int nIndex = Integer.parseInt(s);
		System.out.println(nIndex);
		int[] index = new int[nIndex];
		for(int i=0; i<nIndex; i++) {
			s = rd.readLine();				//index of feature weight
			index[i] = Integer.parseInt(s);
		}
		
		rd.readLine();						//blank line
		s = rd.readLine();					//#weight
		int nWeight = Integer.parseInt(s);
		System.out.println(nWeight);
		double[] wt = new double[nWeight];
		for(int i=0; i<nWeight; i++) {
			s = rd.readLine();				//weight
			wt[i] = Double.parseDouble(s);
		}
		
		Iterator<Entry> it2 = map.entrySet().iterator();
		Entry e1, e2;
		int key1 = 0, key2 = 0;
		String v1 = null, v2 = null;
		e1 = it2.next();
		while(e1 != null) {
			
			key1 = (Integer) e1.getKey();
			v1 = (String) e1.getValue();
			
			if(it2.hasNext()) {
				e2 = it2.next();
				key2 = (Integer) e2.getKey();
			} else {
				e2 = null;
				key2 = nIndex;
			}
			
			int ofs = features.lookupIndex(v1, key2-key1);
			e1 = e2;
			System.out.print(key1);
			System.out.print('\t');
			System.out.print(ofs);
			System.out.print('\t');
			System.out.println(v1);
		}
		
		System.out.println(features.size());
		out.writeObject(features);
		
		double[] weights = new double[nIndex];
		for(int i=0; i<nIndex; i++) {
			if(index[i] == -1)
				weights[i] = 0.0;
			else
				weights[i] = wt[index[i]];
		}
		out.writeObject(weights);
		rd.close();
		out.close();
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public static void main(String[] args) {
		new CRF2FudanNLP().convert("d:\\PeopleDailyCorpus\\peopledaily.seg.model.1",
				"seg.m");
	}
}