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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
/**
 * 将字符数组类型的数据转换成稀疏向量
 * 数据类型：List&lt;&gt;String;&gt; -;&gt; SparseVector
 * @author xpqiu
 */
public class StringArray2SV extends Pipe implements Serializable {

	private static final long serialVersionUID = 358834035189351765L;
	protected IFeatureAlphabet features;
	protected LabelAlphabet label;
	protected static final String constant = "!#@$";
	/**
	 * 常数项。为防止特征字典优化时改变，设为不可序列化
	 */
	protected transient int constIndex;
	
	
	/**
	 * 特征是否为有序特征
	 */
	protected boolean isSorted = false;
	
	public StringArray2SV() {
	}

	public StringArray2SV(AlphabetFactory af) {
		init(af);
	}
	public StringArray2SV(AlphabetFactory af,boolean b){
		init(af);
		isSorted = b;
	}
	
	
	protected void init(AlphabetFactory af) {
		this.features = af.DefaultFeatureAlphabet();
		this.label = af.DefaultLabelAlphabet();
		// 增加常数项
		constIndex = features.lookupIndex(constant);
	}
	
	
	
	@Override
	public void addThruPipe(Instance inst) throws Exception {
		List<String> data = (List<String>) inst.getData();
		int size = data.size();
		HashSparseVector sv = new HashSparseVector();
		
		Iterator<String> it = data.iterator();
		
		for(int i=0;i<size;i++){
			String token = it.next();
			if(isSorted){
				token+="@"+i;
			}
			int id = features.lookupIndex(token);
			if(id==-1)
				continue;
			sv.put(id, 1.0f);
		}
		sv.put(constIndex, 1.0f);
		inst.setData(sv);
	}
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
		constIndex = features.lookupIndex(constant);
	}

}