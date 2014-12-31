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

package org.fnlp.nlp.pipe;

import java.util.Iterator;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
/**
 * 将字符数组类型的数据转换成特征索引
 * 数据类型：List&lt;String&gt; -&gt; int[]
 * @author xpqiu
 */
public class StringArray2IndexArray extends StringArray2SV{

	private static final long serialVersionUID = 358834035189351765L;


	public StringArray2IndexArray(AlphabetFactory af) {
		init(af);
	}
	public StringArray2IndexArray(AlphabetFactory af,boolean b){
		init(af);
		isSorted = b;
	}
	
	
	@Override
	public void addThruPipe(Instance inst) throws Exception {
		List<String> data = (List<String>) inst.getData();
		int size = data.size();
		int[] newdata = new int[data.size()+1];
		Iterator<String> it = data.iterator();
		
		for(int i=0;i<size;i++){
			String token = it.next();
			if(isSorted){
				token+="@"+i;
			}
			int id = features.lookupIndex(token,label.size());
//			if(id==-1)
//				continue;
			newdata[i] = id;
		}
		newdata[size]=constIndex;
		inst.setData(newdata);
	}

}