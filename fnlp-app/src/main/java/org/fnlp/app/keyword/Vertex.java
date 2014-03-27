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

package org.fnlp.app.keyword;

import java.util.ArrayList;

public class Vertex {
	public String id;
	public int index;
	private int forwardCount = 0;
	private ArrayList<Vertex> next = null;
	private ArrayList<Integer> wNext = null;
	
	public Vertex(String id){
		this.id = id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void addVer(Vertex ver){
		if(next == null){
			next = new ArrayList<Vertex>();
			wNext = new ArrayList<Integer>();
		}
		next.add(ver);
		wNext.add(1);
	}
	
	public ArrayList<Vertex> getNext(){
		return next;
	}
	
	public ArrayList<Integer> getWNext(){
		return wNext;
	}
	
	public void setWNext(int index, int wAdd){
		int w = wNext.get(index);
		wNext.set(index, w + wAdd);
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public void addForwardCount(int wAdd){
		forwardCount += wAdd;
	}
	
	public int getForwardCount(){
		return forwardCount;
	}
	
	public String vertexToString(){
		String s = id+ " " + String.valueOf(index)+ " " + String.valueOf(forwardCount);//+ " " + next.toString();
		return s;
	}
}