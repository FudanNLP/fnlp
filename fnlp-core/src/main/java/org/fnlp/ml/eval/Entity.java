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

package org.fnlp.ml.eval;

public class Entity {
	private int startIndex = -1;
	private int endIndex = -1;
	private String entityStr = null;
	private String type = null;

	Entity(int startIndex,int endIndex,String poiStr){
		this.setStartIndex(startIndex);
		this.setEntityStr(poiStr);
		this.setEndIndex(endIndex);
	}
	public String getEntityStr() {
		return entityStr;
	}
	public void setEntityStr(String poiStr) {
		this.entityStr = poiStr;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public String toString(){
		return startIndex +"\t"
				+ endIndex + "\t"
				+ entityStr + "\t"
				+ type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Entity))
			return false;
		Entity obj1 = (Entity) obj;
		if((getStartIndex() == obj1.getStartIndex())
				&&(getEndIndex() == obj1.getEndIndex())
				&&(getType().equals(obj1.getType()))
				&& (entityStr.equals(obj1.getEntityStr()))){
			return true;

		}else
			return false;
	}
}