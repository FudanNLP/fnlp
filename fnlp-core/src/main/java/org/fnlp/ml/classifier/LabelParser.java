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

package org.fnlp.ml.classifier;

import org.fnlp.ml.types.alphabet.LabelAlphabet;

/**
 * 将内部预测值类型解析成原始标签
 * 原始的类别标签，在内部表示成索引标号，需要将其还原
 * 
 * @author xpqiu
 * @since 1.5
 */
public class LabelParser {

	public enum Type{
		/**
		 * 单个字符串
		 */
		STRING,
		/**
		 * 字符串序列
		 */
		SEQ
	}

	public static Predict parse(TPredict res,
			LabelAlphabet labels, Type t) {
		int n = res.size();
		Predict pred = null;
		switch(t){
		case SEQ:			
			pred = new Predict<String[]>(n);
			for(int i=0;i<n;i++){
				int[] preds = (int[]) res.getLabel(i);
				String[] l = labels.lookupString(preds);
				pred.set(i, l, res.getScore(i));
			}
			break;
		case STRING: 
			pred = new Predict<String>(n);
			for(int i=0;i<n;i++){
				if(res.getLabel(i)==null){
					pred.set(i, "null", 0f);
					continue;
				}
				int idx =  (Integer) res.getLabel(i);
				String l = labels.lookupString(idx);
				pred.set(i, l, res.getScore(i));
			}
			return pred;
		default:
			break;

		}
		return pred;

	}



}