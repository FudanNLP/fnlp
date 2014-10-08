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

/**
 * 当前位置固定窗口下，字符串的组合类型
 * 
 * @see org.fnlp.nlp.cn.Chars#getType(String)
 * @author xpqiu
 * 
 */
public class CustomTemplet implements Templet {

	private static final String DUPLICATE = "D";
	private static final String NO = "N";
	private static final String TIME = "T";
	private static final long serialVersionUID = -1632435387620824968L;
	private int id;

	public CustomTemplet(int id) {
		this.id = id;
	}



	/**
	 *  {@inheritDoc}
	 */
	@Override
	public int generateAt(Instance instance, IFeatureAlphabet features, int pos,
			int... numLabels) {
		String[][] data = ( String[][]) instance.getData();
		int len = data[0].length;
		
		StringBuilder sb = new StringBuilder();

		sb.append(id);
		sb.append(':');
		
		if (pos + 1 < len){
			String str1 = data[0][pos]; //这里数据行列和模板中行列相反
			String str2 = data[0][pos+1];
			if(str1.length()==1&&str1.equals(str2)){
				sb.append(DUPLICATE);
			}else
				sb.append(NO);
		}else{
			sb.append(NO);
		}
		if ( pos-1>=0 && pos + 1 < len){
//			String str1 = data[1][pos-2]; //这里数据行列和模板中行列相反
			String str2 = data[1][pos-1];
			String str3 = data[0][pos]; 
			String str4 = data[1][pos+1];
//			String str5 = data[1][pos+2]; 
			if((str3.equals(":")||str3.equals("："))&&str2.equals("D")&&str4.equals("D")){
				sb.append(TIME);
			}
			else
				sb.append(NO);
		}else{
			sb.append(NO);
		}

		int index = features.lookupIndex(sb.toString(),numLabels[0]);
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