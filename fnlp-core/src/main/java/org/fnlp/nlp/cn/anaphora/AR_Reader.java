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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
/**
 * 用于指代消解的读入
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class AR_Reader extends Reader{
	private String data;
	private LinkedList<Instance> list;
	private Iterator it;
	private LinkedList<Entity> ll;
	private EntitiesGetter elp;
	
	public AR_Reader (String[][][] stringTag,String data) throws Exception
	{
		this.data = data;
		elp= new EntitiesGetter();
		ll = elp.parse(stringTag);
		this.dothis();
		it = list.iterator();
	}
	private void dothis() throws Exception{		
		list = new LinkedList<Instance>();
		Entity ss = null;Entity s2 =null;
		EntityGroup eg = null;
		FeatureGeter fp = null;
		Instance in = null;
		Iterator it =null;
		List<String> newdata = null;
		while(ll.size()>0){	
			ss=(Entity)ll.poll();					
			it= ll.iterator();				
			while(it.hasNext()){
				s2 = (Entity)it.next();				
				eg = new EntityGroup(ss,s2);
				fp = new FeatureGeter(eg);
				String[] tokens = this.intArrayToString(fp.getFeatrue()).split("\\t+|\\s+");
				newdata= Arrays.asList(tokens);
				in = new Instance(newdata,null);
				in.setSource(eg);
				list.add(in);								
			}	
		}
	}
	private String intArrayToString(int[] ia){
		StringBuffer sb = new StringBuffer();
		for(int i = 0;i<ia.length;i++){
			sb.append(ia[i]);
			sb.append(" ");
		}
		return sb.toString();
	}
	public Instance next ()
	{
		return (Instance) it.next();
	}

	public boolean hasNext ()	{	
		return it.hasNext();	
	}

}