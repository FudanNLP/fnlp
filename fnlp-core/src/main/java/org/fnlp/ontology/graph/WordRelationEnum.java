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

package org.fnlp.ontology.graph;



public enum WordRelationEnum {
	
	
	
	SYM("同义词",Direction.BOTH),
	ANTONYM("反义词",Direction.BOTH);
	
	private String cname;
	
	private Direction direction;

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	private WordRelationEnum(String name,Direction direction){
		this.cname = name;
		this.direction = direction;
	}

	public static WordRelationEnum getWithName(String name) {
		WordRelationEnum[] tasks = WordRelationEnum.values();
		for(WordRelationEnum task:tasks){
			if(task.cname.equals(name))
				return task;
		}
		return null;
	}

}