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

import org.fnlp.nlp.cn.anaphora.Entity.FUNC;
import org.fnlp.nlp.cn.anaphora.Entity.Sex;
import org.fnlp.nlp.cn.anaphora.Entity.Singular;

/**
 * 用规则的方法来获取指代对的权重
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class WeightGetter {

	private Entity entity;
	private Entity pronoun;
	
	String pdata;
	String edata;
	int edistance;
	int pdistance;
	
	
	public WeightGetter(EntityGroup EntityGroup){
		this.entity = EntityGroup.antecedent;
		this.pronoun = EntityGroup.anaphor;
		 pdata = pronoun.getData();
		 edistance = entity.sentNo;
		 pdistance = pronoun.sentNo;
		 edata = entity.getData();
	}
	private Boolean isSub(String str1,String str2){
		Boolean bl = true;
		for(int i=1;i<str2.length();i++){
			bl = bl&&str1.contains(str2.substring(i, i+1));
			if(i==1&&!bl){
				bl = true;
			}
		}	
		return bl;
	}
	private int roleWeight(){
		
		if(pdata.equals("他")||pdata.equals("她")
				||pdata.equals("它")){
			
			FUNC ptag = pronoun.getGraTag();
			FUNC etag = entity.getGraTag();		
			
			
			if(edistance==pdistance&&entity.getSubDistance()==pronoun.getSubDistance()&&
					etag!=FUNC.ADJ && ptag!=FUNC.ADJ	){
				return -100;
			}
			if(entity.getSubDistance()!=pronoun.getSubDistance()&&edistance==pdistance
					&&etag==FUNC.SUB && ptag==FUNC.SUB){
				return 4;
				
			}
			if(etag==FUNC.SUB && etag == ptag){
				return 4;				
			}
			
			else if(edistance==pdistance
					&&etag==FUNC.OBJ && ptag==FUNC.OBJ){
				return 3;				
			}
			else if(edistance==pdistance
					&&(etag==FUNC.SUB && ptag==FUNC.OBJ||
							etag==FUNC.OBJ && ptag==FUNC.SUB)){
				return 1;
				
			}
			else if(edistance!=pdistance
					&&etag==FUNC.SUB && ptag==FUNC.SUB){
				return 3;
			}
			else if(edistance!=pdistance
					&&etag==FUNC.OBJ && ptag==FUNC.OBJ){
				return 2;
			}
			else if(edistance!=pdistance
					&&(etag==FUNC.SUB && ptag==FUNC.OBJ||
							etag==FUNC.OBJ && ptag==FUNC.SUB)){
				return 1;
			}
			return 0;
		}
		else{ 
			
			if(this.isSub(edata, pdata))
			return 6;
			else
			return -200;
		}
	}
	private int distanceWeight(){
		if(entity.sentNo==pronoun.sentNo){
			return (pronoun.getSubDistance()-entity.getSubDistance());
		}
		else 
			return (pronoun.sentNo-entity.sentNo)+2;
	}
	private int sexWeight(){
		if(entity.getSex().equals(pronoun.getSex())
				&&entity.getSex()!=Sex.UNKONW){
			return 2;
		}
		else if(entity.getSex()==Sex.UNKONW
				||pronoun.getSex()==Sex.UNKONW)
			return 0;
		else 
			return -100;
	}

	private int numWeight(){
		Singular psing = pronoun.singular ;
		Singular esing = entity.singular;
		if(psing == Singular.Yes && psing == esing){
			return 3;
		}
		else if(psing == Singular.No && psing == esing){
			return 5;
		}
		else if(esing == Singular.UNKONW||psing == Singular.UNKONW)
			return 0;
		else 
			return -100;
	}
	
	public int getWeight(){
		return (this.numWeight()+this.roleWeight()+this.sexWeight()-this.distanceWeight());
	}
}