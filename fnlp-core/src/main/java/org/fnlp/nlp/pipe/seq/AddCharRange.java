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

package org.fnlp.nlp.pipe.seq;

import java.util.ArrayList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 在原有序列基础上，增加一列字符类型信息 
 * @see org.fnlp.nlp.cn.Chars#getType(String)
 * @author xpqiu
 * 
 */
public class AddCharRange extends Pipe {

	private static final long serialVersionUID = 3572735523891704313L;

	@Override
	public void addThruPipe(Instance inst) throws Exception {
		Object sdata = inst.getData();
		String[][] data;
		int colum;
		if(sdata instanceof ArrayList){
			ArrayList ssdata = (ArrayList) sdata;
			colum = ssdata.size();
			data = new String[colum+1][];
			for(int i=0;i<colum;i++){
				ArrayList<String> idata =  (ArrayList<String>) ssdata.get(i);
				data[i] = idata.toArray(new String[idata.size()]);
			}			
		}else{
			return;
		}
		
		int len = data[0].length;
		data[colum] = new String[len];
		for(int i=0;i<len;i++){
			data[colum][i] = Chars.getStringType(data[0][i]).toString();
		}		

		inst.setData(data);
	}

}