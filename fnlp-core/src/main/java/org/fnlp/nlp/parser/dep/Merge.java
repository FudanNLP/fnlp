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

package org.fnlp.nlp.parser.dep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Merge {

	public static void main(String[] args) throws IOException {
		String testfile = "/home/feng/Corpus/semantic/CoNLL2009-ST-evaluation-English-Joint.txt";
		String headfile = "/home/feng/Corpus/semantic/CoNLL2009-ST-evaluation-English.10.PHEAD.txt";
		String relafile = "/home/feng/Corpus/semantic/CoNLL2009-ST-evaluation-English.12.PDEPREL.txt";
		String resufile = "/home/feng/Corpus/semantic/CoNLL2009-ST-evaluation-English.txt";

		BufferedReader testread = new BufferedReader(new FileReader(testfile));
		BufferedReader headread = new BufferedReader(new FileReader(headfile));
		BufferedReader relaread = new BufferedReader(new FileReader(relafile));
		BufferedWriter out = new BufferedWriter(new FileWriter(resufile));
		String line = null;
		List<String[]> sentence = new ArrayList<String[]>();
		List<String> heads = new ArrayList<String>();
		List<String> relations = new ArrayList<String>();
		while ((line = testread.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$")) {
				line = headread.readLine();
				line = relaread.readLine();

				StringBuffer buf = new StringBuffer();
				for(int i = 0; i < sentence.size(); i++)	{
					String[] toks = sentence.get(i);
					for(int j = 0; j < 8; j++)	{
						buf.append(toks[j]);
						buf.append("\t");
					}
					buf.append(heads.get(i));
					buf.append("\t");
					buf.append("_");
					buf.append("\t");
					buf.append(relations.get(i));
					buf.append("\t");
					buf.append("_");
					buf.append("\t");
					buf.append(toks[12]);
					buf.append("\n");
				}
				out.write(buf.toString());
				out.newLine();
				
				sentence.clear();
				heads.clear();
				relations.clear();
			} else {
				String[] toks = line.split("\\t+|\\s+");
				sentence.add(toks);
				line = headread.readLine().trim();
				assert(line.matches("^$"));
				heads.add(line);
				line = relaread.readLine().trim();
				assert(line.matches("^$"));
				relations.add(line);
			}
		}
		
		out.flush();
		out.close();
	}

}