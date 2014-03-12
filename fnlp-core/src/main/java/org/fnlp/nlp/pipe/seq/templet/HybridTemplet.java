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

package org.fnlp.nlp.pipe.seq.templet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;

/**
 * 混合序列模型模板类
 *  0: s[i], t[i]; 
 * -1: s[i]t[i], t[i-1]s[i];
 *  1: t[i-1]t[i], s[i-1]s[i];
 *  2: t[i-1]s[i]t[i], s[i-1]t[i-1]s[i]
 * @author Feng Ji
 *
 */
public class HybridTemplet implements Templet {

	/*
	 * Order or Type of JointTemplet: 0 : s[i], t[i] -1 : s[i]t[i], t[i-1]s[i] 1
	 * : t[i-1]t[i], s[i-1]s[i] 2 : t[i-1]s[i]t[i], s[i-1]t[i-1]s[i]
	 */

	private static final long serialVersionUID = 3519288368823314632L;

	private int order = 0;
	private String prefix;
	private String pattern;
	private int[][] vars;
	private int[][] dims;
	static Pattern parser = Pattern
			.compile("(?:%(x|s|t)\\[(-?\\d+)(?:,(\\d+))?\\])");

	public HybridTemplet(String prefix, String pattern) {
		this.prefix = prefix;
		this.pattern = pattern;

		Matcher matcher = parser.matcher(pattern);

		vars = new int[2][];
		List<String> l = new ArrayList<String>();
		while (matcher.find()) {
			if (matcher.group(1).equals("s")) {
				l.add(matcher.group(2));
			}
		}
		vars[0] = new int[l.size()];
		for (int j = 0; j < l.size(); j++) {
			vars[0][j] = Integer.parseInt(l.get(j));
		}
		matcher.reset();

		l.clear();
		while (matcher.find()) {
			if (matcher.group(1).equals("t")) {
				l.add(matcher.group(2));
			}
		}
		vars[1] = new int[l.size()];
		for (int j = 0; j < l.size(); j++) {
			vars[1][j] = Integer.parseInt(l.get(j));
		}
		matcher.reset();
		order = Math.abs(vars[0].length + vars[1].length - 1);
		if (vars[0].length == 1 && vars[1].length == 1)
			order = -order;

		l.clear();
		while (matcher.find()) {
			if (matcher.group(1).equals("x")) {
				l.add(matcher.group(2));
				l.add(matcher.group(3));
			}
		}
		dims = new int[l.size() / 2][2];
		for (int i = 0; i < l.size(); i += 2) {
			dims[i / 2][0] = Integer.parseInt(l.get(i));
			dims[i / 2][1] = Integer.parseInt(l.get(i + 1));
		}
		l = null;
	}

	public int generateAt(Instance instance, IFeatureAlphabet features, int cur,
			int... labels) throws Exception {
		assert (labels.length == 2);

		String[][] data = (String[][]) instance.getData();

		for (int i = 0; i < vars.length; i++) {
			for (int n = 0; n < vars[i].length; n++) {
				int j = vars[i][n];
				if (cur + j < 0 || cur + j >= data.length)
					return -1;
			}
		}

		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(':');
		for (int i = 0; i < dims.length; i++) {
			String rp = "";
			int j = dims[i][0];
			int k = dims[i][1];
			if (cur + j < 0 || cur + j >= data.length) {
				if (cur + j < 0)
					rp = "B_" + String.valueOf(-(cur + j) - 1);
				if (cur + j >= data.length)
					rp = "E_" + String.valueOf(cur + j - data.length);
			} else {
				rp = data[cur + j][k];
			}

			if (-1 != rp.indexOf('$'))
				rp = rp.replaceAll("\\$", "\\\\\\$");

			sb.append(rp);
			sb.append("//");
		}

		int indent = (int) Math.pow(labels[0], vars[0].length);
		for(int i = 1; i < labels.length; i++)	{
			indent *= (int)Math.pow(labels[i], vars[i].length);
		}
		int index = features.lookupIndex(sb.toString(), indent);

		return index;
	}

	public int getOrder() {
		return order;
	}
	
	public String toString()	{
		return prefix+":"+pattern;
	}

	@Override
	public int[] getVars() {
		// TODO Auto-generated method stub
		return null;
	}

}