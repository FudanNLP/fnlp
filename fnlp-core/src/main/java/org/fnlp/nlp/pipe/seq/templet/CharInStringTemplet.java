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
 * 字符串中子序列的模版
 * 
 * @author xpqiu
 * @since FudanNLP 1.5
 * @version 1.0
 */
public class CharInStringTemplet implements Templet {

	private static final long serialVersionUID = -2535980449084713920L;
	private int id;
	/**
	 * 字符串中子序列的起始位置
	 */
	private int position;
	/**
	 * 字符串中子序列的长度
	 */
	private int plen;

	/**
	 * 构造函数
	 * @param id 模版
	 * @param pos 子序列起始位置
	 * @param len 子序列起始长度
	 */
	public CharInStringTemplet(int id, int pos,int len) {
		this.id = id;
		this.position = pos;
		this.plen = len;
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
		int indx = position;
		if(indx<0)
			indx = len+indx;
		if(indx<0)
			indx=0;
		int endIdx = indx+plen;
		if(endIdx>len)
			endIdx = len;
		String str = data[0][pos].substring(indx,endIdx); //这里数据行列和模板中行列相反				
		sb.append(str);	
//		System.out.println(sb.toString());
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