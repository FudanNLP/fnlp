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

package org.fnlp.nlp.parser;

import java.util.Arrays;

import org.fnlp.nlp.parser.dep.DependencyTree;

/**
 * 句法树的另一种表示形式
 * ，包括依赖词的id和依赖关系
 * @author 
 *
 */
public class Target {
	private String[] words;
	private String[] pos;
	private String[] relations;
	private int[] heads;

	public Target(int[] heads ,String[] relations){
		this.heads = heads;
		this.relations = relations;
	}
	public Target(int len) {
		words= new String[len];
		pos = new String[len];
		relations = new String[len];
		heads = new int[len];
		Arrays.fill(heads, -1);
	}
	public Target() {
	}

	public String getDepClass(int idx){
		return this.relations[idx];
	}
	public int getHead(int idx){
		return this.heads[idx];
	}

	public void setDepClass(int i, String relations){
		this.relations[i] = relations;
	}
	public void setHeads(int i, int heads){
		this.heads[i] = heads;
	}
	public int size() {

		return heads.length;
	}
	public String[] getRelations() {
		return relations;
	}
	public int[] getHeads() {
		return heads;
	}
	public static Target ValueOf(DependencyTree dt) {
		Target t = new Target(dt.size());
		to2HeadsArray(dt,t);
		return t;
	}

	private static void to2HeadsArray(DependencyTree dt, Target t) {
		for(int i = 0; i < dt.leftChilds.size(); i++)	{
			DependencyTree ch = dt.leftChilds.get(i);
			t.setHeads(ch.id, dt.id);
			t.setDepClass(ch.id, ch.getDepClass());
			to2HeadsArray(ch,t);
		}
		t.words[dt.id] = dt.word;
		t.pos[dt.id] = dt.pos;
		for(int i = 0; i < dt.rightChilds.size(); i++)	{
			DependencyTree ch = dt.rightChilds.get(i);
			t.setHeads(ch.id,dt.id);
			t.setDepClass(ch.id,ch.getDepClass());
			to2HeadsArray(ch,t);
		}
	}


}