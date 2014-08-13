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

package org.fnlp.nlp.similarity;

import java.io.Serializable;

public class Cluster implements Serializable {

	private static final long serialVersionUID = 5016034594028420143L;
	float prop;
	int id;
	public String rep;
	Cluster left;
	Cluster right;


	public Cluster(int key, float v, String s) {
		id = key;
		prop = v;
		rep = s;
	}

	public Cluster(int newid, Cluster c1, Cluster c2, float pc) {
		id = newid;
		prop = pc;
		left = c1;
		right = c2;
		if(c1==null && c2==null)
			rep = null;
		else if(c1==null)
			rep = c2.rep;
		else if(c2==null)
			rep = c1.rep;
		else			
			rep = c1.rep+":"+c2.rep;		
	}

	String getN() {
		return id+":"+rep+" "+ prop;
	}

	Cluster getRight() {
		return right;
	}

	Cluster getLeft() {
		return left;
	}  
	public String toString(){
		return rep;
	}
}