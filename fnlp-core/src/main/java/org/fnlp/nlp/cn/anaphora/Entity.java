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

package org.fnlp.nlp.cn.anaphora;

import org.fnlp.nlp.cn.PartOfSpeech;

/**
 * 实体
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Entity implements Comparable<Entity>{

	public enum Sex {
		Male, Female, UNKONW;
	}

	public enum Singular {
		Yes, No, UNKONW;
	}

	public enum FUNC {
		SUB, OBJ, ADJ;
	}



	String data;
	PartOfSpeech posTag;
	FUNC graTag;
	Sex sex = Sex.UNKONW;
	Singular singular = Singular.UNKONW;
	int subDistance;
	/**
	 * 句子序号
	 */
	int sentNo;
	int start;
	int end;
	int id;
	boolean isResolution;
	String headword;

	public int getId(){
		return this.id;
	}
	public boolean getIsResolution() {
		return this.isResolution;
	}
	public String getData(){
		return this.data;
	}
	public PartOfSpeech getPosTag(){
		return this.posTag;
	}
	public FUNC getGraTag(){
		return this.graTag;
	}
	public Sex getSex(){
		return this.sex;
	}

	public void setFemale() {
		sex = Sex.Female;
	}

	public void setMale(){
		sex = Sex.Male;
	}

	
	public int getSubDistance(){
		return this.subDistance;
	}


	public void setId(int id){
		this.id = id;
	}
	public void setIsResolution(boolean isResolution){
		this.isResolution = isResolution;
	}
	public void setData(String data){
		this.data = data;
	}
	public void setPosTag(PartOfSpeech posTag){
		this.posTag = posTag;
	}
	public void setGraTag(FUNC graTag){
		this.graTag = graTag;
	}

	
	public void setSubDistance(int subDistance){
		this.subDistance  = subDistance;
	}

	@Override
	public int compareTo(Entity et) {

		if(start>et.start)
			return 1;
		else if(start<et.start)
			return -1;
		else
			return 0;
	}

	public String toString(){		
		StringBuilder sb = new StringBuilder();
		sb.append("data: ").append(data)
		.append("\tisResolution: ").append(isResolution)
		.append("\tsigular： ").append(singular)
		.append("\tsex: ").append(sex)
		.append("\tgraTag: ").append(graTag)
		.append("\theadword: ").append(headword)
		.append("\tposTag: ").append(posTag)
		.append("\n");

		return sb.toString();
	}
	public void setHeadWord(String headword) {
		this.headword = headword;

	}

}