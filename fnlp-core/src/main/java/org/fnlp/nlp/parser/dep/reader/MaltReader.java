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

package org.fnlp.nlp.parser.dep.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.parser.Sentence;

public class MaltReader extends Reader {

	BufferedReader reader = null;
	Sentence next = null;
	List<String[]> carrier = new ArrayList<String[]>();

	public MaltReader(String filepath) throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(
				filepath), "UTF-8"));
		advance();
	}

	private void advance() throws IOException {
		String line = null;
		carrier.clear();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$"))
				break;
			carrier.add(line.split("\\t+|\\s+"));
		}

		next = null;
		if (!carrier.isEmpty()) {
			String[] forms = new String[carrier.size()];
			String[] postags = new String[carrier.size()];
			int[] heads = new int[carrier.size()];
			String[][] source = new String[carrier.size()][];
			for (int i = 0; i < carrier.size(); i++) {
				String[] tokens = carrier.get(i);
				source[i] = tokens;
				forms[i] = tokens[1];
				
				postags[i] = tokens[2];
				heads[i] = Integer.parseInt(tokens[3])-1;
				
			}

			next = new Sentence(forms, postags, heads);
			next.setSource(source);
		}
	}

	public boolean hasNext() {
		return (next != null);
	}

	public Instance next() {
		Sentence cur = next;
		try {
			advance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cur;
	}
}