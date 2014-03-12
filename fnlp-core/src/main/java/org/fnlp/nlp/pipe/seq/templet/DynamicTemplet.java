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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.ml.types.DynamicInfo;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;

public class DynamicTemplet implements Templet {
	private static final long serialVersionUID = -965487786100531480L;
	
	Pattern parser = Pattern.compile("(?:%(x|y|pos|word|len)\\[(-?\\d+)(?:,(\\d+))?\\])");
	String templet;

	int order;
	int id;
	int[][] dims;
	int[] vars;
	int[] dynamic_pos, dynamic_word, dynamic_len;
	public static int MAX = 0;
	public static int MIN = 0;
	
	public DynamicTemplet(int id, String templet) {
		this.id = id;
		this.templet = templet;
		Matcher matcher = parser.matcher(this.templet);
		/**
		 * 解析y的位置
		 */
		List<String> y = new ArrayList<String>();
		List<String> x = new ArrayList<String>();
		List<String> pos = new ArrayList<String>();
		List<String> word = new ArrayList<String>();
		List<String> len = new ArrayList<String>();
		
		while (matcher.find()) {
			if (matcher.group(1).equalsIgnoreCase("y")) {
				y.add(matcher.group(2));
			} else if (matcher.group(1).equalsIgnoreCase("x")) {
				x.add(matcher.group(2));
				x.add(matcher.group(3));
			} else if (matcher.group(1).equalsIgnoreCase("pos")) {
				pos.add(matcher.group(2));
			} else if (matcher.group(1).equalsIgnoreCase("word")) {
				word.add(matcher.group(2));
			} else if (matcher.group(1).equalsIgnoreCase("len")) {
				len.add(matcher.group(2));
			}
		}
		if(y.size()==0){//兼容CRF++模板
			vars = new int[]{0};
		}else{
			vars = new int[y.size()];
			for (int j = 0; j < y.size(); j++) {
				vars[j] = Integer.parseInt(y.get(j));
			}
		}
		order = vars.length - 1;
			
		dynamic_pos = getDynamic(pos);
		dynamic_word = getDynamic(word);
		dynamic_len = getDynamic(len);
		
		dims = new int[x.size() / 2][2];
		for (int i = 0; i < x.size(); i += 2) {
			dims[i / 2][0] = Integer.parseInt(x.get(i));
			dims[i / 2][1] = Integer.parseInt(x.get(i + 1));
		}	
	}
	
	private int[] getDynamic(List<String> d) {
		int[] dynamic = new int[d.size()];
		for (int j = 0; j < d.size(); j++) {
			dynamic[j] = -(Integer.parseInt(d.get(j)));
			//dynamic[j] = -(Integer.parseInt(d.get(j)) + 1);//DynamicDoubleViterbi
			if(dynamic[j] > MAX)
				MAX = dynamic[j];
			if(dynamic[j] < MIN)
				MIN = dynamic[j];
		}
		return dynamic;
	}
	
	@Override
	public int getOrder() {
		return this.order;
	}
	
	public String toString() {
		return this.templet;
	}

	@Override
	public int generateAt(Instance instance, IFeatureAlphabet features, int pos,
			int... numLabels) throws Exception {
		assert (numLabels.length == 1);

		String[][] data = (String[][]) instance.getSource();
	
		ArrayList<String> preLabel1 = null;
		ArrayList<String> preLabel2 = null;
		if(instance.getTempData() instanceof LinkedList) {
			LinkedList<ArrayList<String>> al = (LinkedList<ArrayList<String>>) instance.getTempData();
			preLabel1 = al.get(0);
			preLabel2 = al.get(1);
		}
		ArrayList<DynamicInfo> preLabel = null;
		if(instance.getTempData() instanceof ArrayList) {
			preLabel = (ArrayList<DynamicInfo>)instance.getTempData();
		}
		
		for(int i = 0; i < vars.length; i++)	{
			int j = vars[i];
			if (pos+j < 0 || pos+j >= data.length)
				return -1;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(id);
		sb.append(':');
		for (int i = 0; i < dims.length; i++) {
			String rp = "";
			int j = dims[i][0];
			int k = dims[i][1];
			if (pos + j < 0 || pos + j >= data.length) {
				if (pos + j < 0)
					rp = "B_" + String.valueOf(-(pos + j) - 1);
				if (pos + j >= data.length)
					rp = "E_" + String.valueOf(pos + j - data.length);
			} else {
				rp = data[pos + j][k];
			}
			if (-1 != rp.indexOf('$'))
				rp = rp.replaceAll("\\$", "\\\\\\$");
			sb.append(rp);
			sb.append("//");
		}
		if(preLabel != null) {
			setDynamicFeature(sb, dynamic_pos, preLabel, 0);
			setDynamicFeature(sb, dynamic_word, preLabel, 1);
			setDynamicFeature(sb, dynamic_len, preLabel, 2);
		} else
			setDynamicFeature2(sb, dynamic_pos, preLabel1, preLabel2);

//		System.out.println(sb.toString());

		int index = features.lookupIndex(sb.toString(),
				(int) Math.pow(numLabels[0], order + 1));
		return index;
	}
	
	private void setDynamicFeature(StringBuffer sb, int[] dynamic, ArrayList<DynamicInfo> preLabel, int choice) {
		for (int i = 0; i < dynamic.length; i++) {
			String rp = "";
			int j = dynamic[i];
			if(j < 0) {
				continue;
			} else if (j >= preLabel.size()) {
				rp = "B_" + (j - preLabel.size());
			} else {
				if(choice == 0)
					rp = preLabel.get(j).getPos();
				else if(choice == 1)
					rp = preLabel.get(j).getWord();
				else if(choice == 2)
					rp = preLabel.get(j).getLen() + "";
				else
					System.out.println("DynamicTemplet.setDynamicFeature Error!");
			}
			sb.append(rp);
			sb.append("//");
		}
	}
	
	private void setDynamicFeature2(StringBuffer sb, int[] dynamic, ArrayList<String> preLabel, ArrayList<String> preLabel2) {
		for (int i = 0; i < dynamic.length; i++) {
			String rp = "";
			int j = dynamic[i] - 1;
			if(j < 0) {
				if(preLabel2 == null)
					continue;
				else {
					int k = -j - 1;
					if(k >= preLabel2.size())
						rp = "E_" + (k - preLabel2.size());
					else
						rp = preLabel2.get(k);
				}
			} else if (j >= preLabel.size()) {
				rp = "B_" + (j - preLabel.size());
			} else {
				rp = preLabel.get(j);
			}
			sb.append(rp);
			sb.append("//");
		}
	}

	@Override
	public int[] getVars() {
		// TODO Auto-generated method stub
		return null;
	}

}