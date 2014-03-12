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

package org.fnlp.nlp.pipe.seq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.fnlp.ml.types.DynamicInfo;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.seq.templet.DynamicTemplet;
import org.fnlp.nlp.pipe.seq.templet.Templet;

public class Sequence2DynamicFeatureSequence extends Pipe {
	
	private static final long serialVersionUID = -5568795070369739920L;
	List<Templet> templets;
	List<Templet> dynamicTemplets;
	public IFeatureAlphabet features;
	LabelAlphabet labels;

	public Sequence2DynamicFeatureSequence(List<Templet> templets, List<Templet> dynamicTemplets,
			IFeatureAlphabet features, LabelAlphabet labels) {
		this.templets = templets;
		this.dynamicTemplets = dynamicTemplets;
		this.features = features;
		this.labels = labels;		
	}

	public void addThruPipe(Instance instance) throws Exception {	
		String[][] data = (String[][]) instance.getData();
		String[] target = (String[]) instance.getTempData();
		instance.setSource(instance.getData());
		
		int[][] newData = new int[data.length][templets.size() + dynamicTemplets.size()];
		for (int i = 0; i < data.length; i++) {
			Arrays.fill(newData[i], -1);
			for (int j = 0; j < templets.size(); j++) {	
				newData[i][j] = templets.get(j).generateAt(instance,
						this.features, i, labels.size());
			}
//			System.out.println(data[i][0]);
			
			if(DynamicTemplet.MIN == 0) {
				ArrayList<DynamicInfo> preLabel = getPreInfo(data, instance, i, target);
//				System.out.println(preLabel);
				instance.setTempData(preLabel);
			} else {
				ArrayList<String> fl = getPreLabel(instance, i, target);
				ArrayList<String> bl = getPreLabel_back(instance, i, target);
				LinkedList<ArrayList<String>> preLabel = new LinkedList<ArrayList<String>>();
				preLabel.add(fl);
				preLabel.add(bl);
				instance.setTempData(preLabel);
			}
			
			for (int j = 0; j < dynamicTemplets.size(); j++) {	
				newData[i][j + templets.size()] = dynamicTemplets.get(j).generateAt(instance,
						this.features, i, labels.size());
			}
		}
		instance.setData(newData);
	}
	
	private String getLabelName(String s) {
		int index = s.indexOf("-");
		String t = index < 0 ? s : s.substring(0, index);
		return index < 0 ? t : s.substring(index + 1, s.length());
	}
	
	private boolean isEndLabel(String s) {
		int index = s.indexOf("-");
		String t = index < 0 ? s : s.substring(0, index);
		if(t.equals("E") || t.equals("S"))
			return true;
		else
			return false;
	}
	
	private ArrayList<DynamicInfo> getPreInfo(String[][] data, Instance instance, int p, String[] target) {
		ArrayList<DynamicInfo> al = new ArrayList<DynamicInfo>();
		StringBuffer word = new StringBuffer();
		String pos = null;
		if(p > 0) {
			if(isEndLabel(target[p - 1])) 
				al.add(new DynamicInfo("", "", 0));
			else 
				pos = getLabelName(target[p - 1]);
		}
		for(int l = p - 1; l >= 0; l--) {
			String s = target[l];
			int index = s.indexOf("-");
			String t = index < 0 ? s : s.substring(0, index);	
			if(t.equals("S") || t.equals("E")) {
				if(pos != null) {
					word.reverse();
					al.add(new DynamicInfo(pos, word.toString(), word.toString().length()));
					if(al.size() > DynamicTemplet.MAX) 
						break;
				}
				pos = index < 0 ? t : s.substring(index + 1, s.length());
				word = new StringBuffer();
			}
			word.append(data[l][0]);
		}
		if(al.size() <= DynamicTemplet.MAX && pos != null) {
			word.reverse();
			al.add(new DynamicInfo(pos, word.toString(), word.toString().length()));
		}
		return al;
	}
	
	private ArrayList<String> getPreLabel(Instance instance, int p, String[] target) {
		ArrayList<String> al = new ArrayList<String>();
		for (int l = p - 1; l >= 0; l--) {
			String s = target[l];
			int index = s.indexOf("-");
			String t = s.substring(0, index);
			if(t.equals("S") || t.equals("E"))
				al.add(s.substring(index + 1, s.length()));
			if(al.size() > DynamicTemplet.MAX)
				break;
		}
		return al;
	}
	
	private ArrayList<String> getPreLabel_back(Instance instance, int p, String[] target) {
		ArrayList<String> al = new ArrayList<String>();
		for (int l = p; l < target.length; l++) {
			String s = target[l];
			int index = s.indexOf("-");
			String t = s.substring(0, index);
			if(t.equals("S") || t.equals("E"))
				al.add(s.substring(index + 1, s.length()));
			if(al.size() > - DynamicTemplet.MIN - 1)
				break;
		}
		return al;
	}
}