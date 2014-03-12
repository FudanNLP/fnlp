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

package org.fnlp.ml.classifier.hier;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.ml.types.sv.ISparseVector;

/**
 * 计算类中心
 * @author xpqiu
 *
 */
public class Mean {



	public static HashSparseVector[] mean (InstanceSet trainingList,Tree tree)
	{

		LabelAlphabet alphabet = trainingList.getAlphabetFactory().DefaultLabelAlphabet();
		int numLabels = alphabet.size();
		HashSparseVector[] means = new  HashSparseVector[numLabels];
		int[] classNum = new int[numLabels];

		for(int i=0;i<numLabels;i++){
			means[i]=new HashSparseVector();
		}

		for (int ii = 0; ii < trainingList.size(); ii++){
			Instance inst = trainingList.getInstance(ii);
			ISparseVector fv = (ISparseVector) inst.getData ();
			int target = (Integer) inst.getTarget();
			if(tree!=null){
				int[] anc = tree.getPath(target);
				for(int j=0;j<anc.length;j++){
					means[anc[j]].plus(fv);
					classNum[anc[j]]+=1;
				}
			}else{
				means[target].plus(fv);
				classNum[target]+=1;
			}
		}
		for(int i=0;i<numLabels;i++){
			if(classNum[i]>0)
				means[i].scaleDivide(classNum[i]);
		}
		return means;
	}

}