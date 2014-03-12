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

import java.io.Serializable;
import java.util.ArrayList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.Chars;
import org.fnlp.nlp.cn.Chars.CharType;
import org.fnlp.nlp.cn.Chars.StringType;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 将字符串直接转换成待标注的序列
 * 例子：
 * "我abc"转换成{"我","a","b","c"};如果预处理英文，则转换成{"我","abc"}
 * 
 * @author xpqiu
 * @version 2.0
 *
 */
public class String2Sequence extends Pipe implements Serializable {


	/**
	 * 是否预处理英文，默认为真
	 */
	boolean isEnFilter = true;

	private static final long serialVersionUID = 5699154494725645936L;

	/**
	 * 构造函数
	 * @param b 是否带标签
	 */
	public String2Sequence(boolean b){
		isEnFilter = b;
	}


	/**
	 * 将一个字符串转换成按标注序列
	 * 每列一个字或连续英文token的信息
	 * @param inst 样本
	 */
	@Override
	public void addThruPipe(Instance inst) {
		String str = (String) inst.getData();
		String[][] data;
		if(isEnFilter){
			data = genSequence(str);			
		}else{
			data = new String[2][str.length()];
			for(int i = 0; i < str.length(); i++){
				data[0][i] = str.substring(i,i+1);
				data[1][i] = Chars.getStringType(data[0][i]).toString();
			}
		}
		inst.setData(data);
	}

	/**
	 * 预处理连续的英文和数字
	 * @param sent
	 * @return
	 */
	public static String[][] genSequence(String sent){

		CharType[] tags = Chars.getType(sent);
		int len = sent.length();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> types = new ArrayList<String>();
		int begin =0;
		for(int j=0; j<len; j++) {			
			if(j<len-1 && tags[j]==CharType.L && tags[j+1]==CharType.L){//当前是连续英文
				continue;
			}else if(j<len-1 &&tags[j]==CharType.D && tags[j+1]==CharType.D){//当前是连续数字
				continue;
			}
			StringType st = Chars.char2StringType(tags[j]);
			String w = sent.substring(begin,j+1);
			words.add(w);
			types.add(st.toString());
			begin = j+1;
		}		
		String[][] data = new String[2][];
		data[0] = words.toArray(new String[words.size()]);
		data[1] = types.toArray(new String[types.size()]);
		return data;
	}


}