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

package org.fnlp.nlp.pipe.seq.templet;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.cn.Chars.StringType;

/**
 * 字符串中字符组合模板
 * 
 * @author xpqiu
 * 
 */
public class StringTypeTemplet implements Templet {

	private static final long serialVersionUID = -4911289807273417691L;
	private int id;

	public StringTypeTemplet(int id) {
		this.id = id;
	}



	/**
	 *  {@inheritDoc}
	 */
	@Override
	public int generateAt(Instance instance, IFeatureAlphabet features, int pos,
			int... numLabels) {
		String[][] data = ( String[][]) instance.getData();
		
		int len = data[0][pos].length();

		StringBuilder sb = new StringBuilder();

		sb.append(id);
		sb.append(':');
		sb.append(pos);
		sb.append(':');
		
		String str = data[0][pos]; //这里数据行列和模板中行列相反
		StringType type = Chars.getStringType(str);
		String stype = type.name(); 
		
		if(type == StringType.M){
			
			if(str.length()>4 && str.startsWith("http:"))
				stype = "U$";
			else if(str.length()>4&&str.contains("@"))
				stype  = "E$";
			else if(str.contains(":"))
				stype = "T$";
		}
		
		sb.append(stype);	
		
		int index = features.lookupIndex(sb.toString(), numLabels[0]);
		return index;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	public int[] getVars() {
		return new int[] { 0 };
	}

	public int offset(int... curs) {
		return 0;
	}

}