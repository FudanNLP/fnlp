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

package org.fnlp.nlp.cn.anaphora.rule;

import java.util.Iterator;
import java.util.LinkedList;

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.nlp.cn.anaphora.EntitiesGetter;
import org.fnlp.nlp.cn.anaphora.Entity;
import org.fnlp.nlp.cn.anaphora.EntityGroup;
import org.fnlp.nlp.cn.anaphora.WeightGetter;
import org.fnlp.nlp.cn.tag.POSTagger;

/**
 * @author jszhao
 *
 */
public class RuleAnaphora {

	private EntitiesGetter ep;
	POSTagger pos;
	public RuleAnaphora() throws Exception{	
		ep = new EntitiesGetter();	
	}
	
	public RuleAnaphora(String segmodel, String posmodel) throws Exception{
		pos = new POSTagger(segmodel,posmodel);
		ep = new EntitiesGetter();	
	}
	public LinkedList<EntityGroup> resolve(String str) throws Exception{
		LinkedList<EntityGroup> arGroup = new LinkedList<EntityGroup>();
		
		String[][][] taggedstr = pos.tag2DoubleArray(str);
		LinkedList<Entity> entityList = ep.parse(taggedstr);
		return doIt(entityList, arGroup);
	}
	public LinkedList<EntityGroup> resolve(String[][][] strigTag,String str){
		LinkedList<EntityGroup> arGroup = new LinkedList<EntityGroup>();
		LinkedList<Entity> entityList = ep.parse(strigTag);
		return  doIt(entityList, arGroup);
	}
	private LinkedList<EntityGroup> doIt(LinkedList<Entity> entityList, LinkedList<EntityGroup> arGroup){
		LinkedList<Entity> ll =null;
		int flag = 0;Entity re =null;Entity re1 =null;
		int i = entityList.size();int j =0;
		WeightGetter wp = null;
		EntityGroup reg =null;
		EntityGroup reg1 =null;
		while(flag!=i-j){
			flag =0;
			ll = new LinkedList<Entity>();
			Iterator<Entity> it = entityList.iterator();
			while(it.hasNext()){
				flag++;
				re = it.next();
				if(!re.getPosTag().isPronoun()){
					ll.add(re);
				}
				else{
					j++;
					it.remove();
					break;
				}					
			} 
			if(flag==i-j&&!re.getPosTag().isPronoun())
				break;
			it = ll.iterator();
			int ii = -100;
			while(it.hasNext()){
				re1 = it.next();			
				reg = new EntityGroup(re1,re);
				wp = new WeightGetter(reg);
				if(wp.getWeight()>=ii){
					ii = wp.getWeight();
					reg1 =reg;
					reg1.weight = ii;
				}
			}
			if(reg1!=null)
				arGroup.add(reg1);
		}
		return arGroup;
	}
	
	
	
	public static void main(String args[]) throws Exception{
		String str2 = "复旦大学创建于1905年,它位于上海市，这个大学培育了好多优秀的学生。";
		String str3[] = {"复旦","大学","创建","于","1905年","，","它","位于","上海市","，","这个","大学","培育","了","好多","优秀","的","学生","。"};
		String str4[] = {"专有名","名词","动词","介词","时间短语","标点","代词","动词","专有名","标点","限定词","名词","动词","动态助词","数词","形容词","结构助词","名词","标点"};
		String str5[][][] = new String[1][2][str3.length];
		str5[0][0] = str3;
		str5[0][1] = str4;		
		RuleAnaphora arp = new RuleAnaphora();
		System.out.printf(arp.resolve(str5, str2).toString());
		
//		RuleAnaphora arp = new RuleAnaphora("./models/seg.m","./models/pos.m");
//		LinkedList<EntityGroup> ll = arp.resolve("复旦大学创建于1905年,他位于上海市，这个大学培育了好多优秀的学生。");
//		
	}
}