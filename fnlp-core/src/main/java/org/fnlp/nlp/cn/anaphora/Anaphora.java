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

import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.util.exception.LoadModelException;
/**
 * 指代消解的程序接口
 * @author jszhao,xpqiu
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class Anaphora {

	private Linear cl;
	private TreeSet<Entity> ts;
	private FormChanger fc;
	private LinkedList<Instance> llis;
	private InstanceSet test;
	EntitiesGetter ep;
	POSTagger pos;
	
	public Anaphora(String armodel) throws LoadModelException {
		ep = new EntitiesGetter();		
		cl = Linear.loadFrom(armodel);
	}
	
	public Anaphora(String segmodel, String posmodel, String armodel) throws LoadModelException{
		pos = new POSTagger(segmodel,posmodel);
		ep = new EntitiesGetter();		
		cl = Linear.loadFrom(armodel);
	}
	
	public Anaphora(POSTagger tag,String armodel) throws LoadModelException{
		pos = tag;
		ep = new EntitiesGetter();		
		cl = Linear.loadFrom(armodel);
	}
	
	

	/**
	 * 用了标注模型，得到指代对集合
	 * @param str
	 * @return 指代对集合
	 * @throws Exception
	 */
	public LinkedList<EntityGroup> resolve(String str) throws Exception{
		LinkedList<EntityGroup> arGroup = new LinkedList<EntityGroup>();
		
		String[][][] taggedstr = pos.tag2DoubleArray(str);
		init(taggedstr, str);
		LinkedList<Entity> entityList = ep.parse(taggedstr);
		return doIt(entityList, arGroup);
	}
	
	public LinkedList<EntityGroup> resolve(String[][][] strigTag,String str) throws Exception{
		LinkedList<EntityGroup> arGroup = new LinkedList<EntityGroup>();
		init(strigTag, str);
		LinkedList<Entity> entityList = ep.parse(strigTag);
		return  doIt(entityList, arGroup);
	}
	
	

	private void init(String[][][]stringTag,String str) throws Exception{
		ts = new TreeSet<Entity>();
		llis = new LinkedList<Instance>();
		fc = new FormChanger();
		test = new InstanceSet(cl.getPipe());
		test.loadThruPipes(new AR_Reader(stringTag,str));
		for(int i=0;i<test.size();i++){
			String ss = cl.getStringLabel(test.getInstance(i));
			if(ss.equals("1")){
				llis.add(test.getInstance(i));
			}	
		}
		fc.groupToList(llis);
		fc.getLlsb();
		ts = fc.getTs();
	}
	
	
	private LinkedList<EntityGroup> doIt(LinkedList<Entity> entityList, LinkedList<EntityGroup> arGroup){
		LinkedList<Entity> ll =null;
		int flag = 0;Entity re =null;Entity re1 =null;
		int i = this.ts.size();int j =0;
		WeightGetter wp = null;
		EntityGroup reg =null;
		EntityGroup reg1 =null;
		while(flag!=i-j){
			flag =0;
			ll = new LinkedList<Entity>();
			Iterator<Entity> it = this.ts.iterator();
			while(it.hasNext()){
				flag++;
				re = it.next();
				if(!re.getIsResolution()){
					ll.add(re);
				}
				else{
					j++;
					it.remove();
					break;
				}					
			}
			if(flag==i-j&&!re.getIsResolution())
				break;
			it = ll.iterator();
			int ii = -1000;
			while(it.hasNext()){
				re1 = it.next();			
				reg = new EntityGroup(re1,re);
				wp = new WeightGetter(reg);
				if(wp.getWeight()>=ii){
					ii = wp.getWeight();
					reg1 =reg;
					reg1.setWeight(ii);
				}
			}
			if(reg1!=null){
				if(reg1.getWeight()<-100)
				continue;
				arGroup.add(reg1);
			}
		}
		return arGroup;
	}
}