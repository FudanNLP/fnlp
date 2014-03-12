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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.fnlp.ml.types.Instance;
/**
 * 将指代对样本集合转换成代词和代词候选集的集合
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class FormChanger {
	private LinkedList<StringBuffer> llsb;
	private LinkedList<Instance> llst;
	private TreeSet<Entity> ts;
	public FormChanger(){
		llsb = new LinkedList<StringBuffer>();
		llst = new LinkedList<Instance>();
	}
	
	public void groupToList(LinkedList<Instance> ll){
		StringBuffer sb = null;
		EntityGroup eg1 = null;
		EntityGroup eg2 = null;

		
		Instance in1 = null;
		Instance in2 = null;
		Iterator<Instance> it = null;
		if(ll.size()==0)
			ts = new TreeSet<Entity>();
		while(ll.size()>0){
			in1 = ll.poll();
			ts=new TreeSet<Entity>();
			sb = new StringBuffer();
			sb.append("<  ");
			eg1= (EntityGroup) in1.getSource();
			ts.add(eg1.antecedent);
			ts.add(eg1.anaphor);
			do{
				if(llst.size()>0){
					in1 = llst.poll();
					eg1= (EntityGroup) in1.getSource();
				}
				it = ll.iterator();
				while(it.hasNext()){					
					in2 = (Instance) it.next();
					eg2 = (EntityGroup) in2.getSource();
					if(eg1.antecedent.start == eg2.antecedent.start||eg1.anaphor.start == eg2.antecedent.start){
						ts.add(eg2.anaphor);
						llst.add(in2);
						it.remove();
					}
					else if(eg1.antecedent.start == eg2.anaphor.start||eg1.anaphor.start == eg2.anaphor.start){
						ts.add(eg2.antecedent);
						llst.add(in2);
						it.remove();
					}		
				}
			}
			while(llst.size()>0);
			Iterator<Entity> it3 = ts.iterator();
			while(it3.hasNext()){
				Entity et1 = it3.next();
				sb.append(et1.getData()+"("+et1.start+")"+"  ");
			}
			sb.append(">");
			llsb.add(sb);
			
		}
			
	}
	public TreeSet<Entity> getTs(){
		return this.ts;
	}
	public LinkedList<StringBuffer> getLlsb(){
		return this.llsb;
	}

}