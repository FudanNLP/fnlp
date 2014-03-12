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

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 处理混合语言字符串
 * @author Feng Ji
 *
 */
public class MixedString2Sequence extends Pipe {

	@Override
	public void addThruPipe(Instance inst) throws Exception {
		String str = (String) inst.getData();
		char[] toks = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < toks.length; i++)	{
			if (Chars.isChar(toks[i]))	{
				sb.append(toks[i]);
				sb.append(" ");
			}
		}
	}

}