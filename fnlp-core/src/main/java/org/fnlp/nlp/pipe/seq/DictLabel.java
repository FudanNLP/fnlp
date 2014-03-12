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
import java.util.Arrays;

import org.fnlp.ml.types.Dictionary;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 将字符序列转换成特征序列 因为都是01特征，这里保存的是索引号
 * 
 * @author xpqiu
 * 
 */
public class DictLabel extends Pipe  {

	class WordInfo{

		String word;
		int len;

		public WordInfo(String string, int n) {
			word = string;
			len = n;
		}
	}

	private static final long serialVersionUID = -8634966199670429510L;

	protected Dictionary dict;
	protected LabelAlphabet labels;

	//BMES标签索引
	int idxB;
	int idxM;
	int idxE;
	int idxS;

	private boolean mutiple;

	public DictLabel(Dictionary dict, LabelAlphabet labels) {
		this.dict = dict;
		this.mutiple = dict.isAmbiguity();
		this.labels = labels;
		idxB = labels.lookupIndex("B");
		idxM = labels.lookupIndex("M");
		idxE = labels.lookupIndex("E");
		idxS = labels.lookupIndex("S");
	}

	public void setDict(Dictionary dict)	{
		this.dict = dict;
	}

	public void addThruPipe(Instance instance) throws Exception {
		String[][] data = (String[][]) instance.getData();

		int length = data[0].length;
		int[][] dicData = new int[length][labels.size()];				

		int indexLen = dict.getIndexLen();
		for (int i = 0; i < length; i++) {
			if (i + indexLen <= length) {
				WordInfo s = getNextN(data[0], i, indexLen);
				int[] index = dict.getIndex(s.word);
				if(index != null) {
					for(int k = 0; k < index.length; k++) {
						int n = index[k];
						if(n == indexLen) { //下面那个check函数的特殊情况，只为了加速
							label(i, s.len, dicData);
							if(!mutiple){
								i = i + s.len;
								break;
							}
						}
						int len = check(i, n, length, data[0], dicData);
						if(len>0&&!mutiple){
							i = i + len;
							break;
						}
					}
				}
			}
		}

		for (int i = 0; i < length; i++) 
			if (hasWay(dicData[i]))
				for(int j = 0; j < dicData[i].length; j++)
					dicData[i][j]++;

		instance.setDicData(dicData);
	}

	private boolean hasWay(int[] ia) {
		for(int i = 0; i < ia.length; i++) {
			if(ia[i] == -1)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param i
	 * @param n
	 * @param length
	 * @param data
	 * @param tempData
	 * @return
	 */
	private int check(int i, int n, int length, String[] data, int[][] tempData) {

		WordInfo s = getNextN(data, i, n);
		if (dict.contains(s.word)) {
			label(i, s.len, tempData);	
			return s.len;
		}
		return 0;
	}
	/**
	 * 
	 * @param i
	 * @param n
	 * @param tempData
	 */
	private void label(int i, int n, int[][] tempData) {
		// 下面这部分依赖{1=B,2=M,3=E,0=S}		
		if (n == 1) {			
			tempData[i][idxS] = -1;
		} else {
			tempData[i][idxB] = -1;
			for (int j = i + 1; j < i + n - 1; j++)
				tempData[j][idxM] = -1;
			tempData[i + n - 1][idxE] = -1;
		}
	}

	/**
	 * 得到从位置index开始的长度为N的字串
	 * @param data String[]
	 * @param index 起始位置
	 * @param N 长度
	 * @return
	 */
	public WordInfo getNextN(String[] data, int index, int N) {
		StringBuilder sb = new StringBuilder();
		int i = index;
		while(sb.length()<N&&i<data.length){			
			sb.append(data[i]);
			i++;
		}
		if(sb.length()<=N)	
			return new WordInfo(sb.toString(),i-index);
		else
			return new WordInfo(sb.substring(0,N),i-index);
	}

}