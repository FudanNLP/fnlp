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
import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.similarity.train.KMeansWordCluster;

/**
 * 当前位置字符的语义类型
 *  
 * @author xpqiu
 * 
 */
public class CharClassTemplet3 implements Templet {

	private static final long serialVersionUID = 3572735523891704313L;
	private int id;
	private KMeansWordCluster cluster;
	private int[] idxs;

	public CharClassTemplet3(int id,KMeansWordCluster kmwc, int... dim) {
		this.id = id;
		this.cluster = kmwc;
		this.idxs = dim;
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

		for(int idx : idxs){
			pos = pos+idx;
			if(pos<0||pos>=len)
				return -1;
			String context = "";
			if(pos>1)
				context += data[0][pos-1]; //这里数据行列和模板中行列相反
			else
				context += "Begin0";
			context += data[0][pos]; 
			if(pos<len-1)
				context += data[0][pos+1]; //这里数据行列和模板中行列相反
			else
				context += "End0";
			// 得到字符串类型		
			int cid;
			char c = data[0][pos].charAt(0);
			if(c>='A'+ 65248 &&c<='Z'+65248)
				cid = 1039;
			else
				cid= cluster.classifier(context);
			sb.append(":");
			sb.append(cid);
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