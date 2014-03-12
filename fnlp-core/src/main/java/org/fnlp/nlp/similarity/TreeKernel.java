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

package org.fnlp.nlp.similarity;

import java.io.Serializable;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.ontology.graph.WordGraph;
/**
 * 计算两颗树的相似度
 *
 */
public class TreeKernel implements ISimilarity <DependencyTree>, Serializable{

	private static final long serialVersionUID = 6749406907457182885L;
	 public double factor = 0.2;
	private WordGraph wg;

	@Override
	public float calc(DependencyTree item1, DependencyTree item2){
		float score = getDepScore(item1, item2, 1);
//		float base = getBase(item1) * getBase(item2);
		float base = item1.size()*item2.size();
		base = (float) Math.sqrt(base);
		return (score / base);
	}

	/**
	 * 计算Tree Kernel
	 * @param t1
	 * @param t2
	 * @param depth
	 * @return
	 */
	private float getDepScore(DependencyTree t1, DependencyTree t2, int depth){
		float score = 0.0f;
		float modify = getDepthModify(depth);
		if(modify == 0)
			return score;
		
		double sScore = getWordScore(t1, t2);
        if (sScore != 0)
            score += modify * sScore;
        else
            score += factor * modify * getTagScore(t1, t2);
		
		for(int i = 0; i < t1.leftChilds.size(); i++)
			for(int j = 0; j < t2.leftChilds.size(); j++)
				score += getDepScore(t1.leftChilds.get(i), t2.leftChilds.get(j), depth+1);
		
//		for(int i = 0; i < t1.leftChilds.size(); i++)
//			for(int j = 0; j < t2.rightChilds.size(); j++)
//				score += getDepScore(t1.leftChilds.get(i), t2.rightChilds.get(j), depth+1);
//		
//		for(int i = 0; i < t1.rightChilds.size(); i++)
//			for(int j = 0; j < t2.leftChilds.size(); j++)
//				score += getDepScore(t1.rightChilds.get(i), t2.leftChilds.get(j), depth+1);
		
		for(int i = 0; i < t1.rightChilds.size(); i++)
			for(int j = 0; j < t2.rightChilds.size(); j++)
				score += getDepScore(t1.rightChilds.get(i), t2.rightChilds.get(j), depth+1);
		
		return score;
	}
	
    /**
     * c函数
     * @param t1
     * @param t2
     * @return
     */
    private float getTagScore(DependencyTree t1, DependencyTree t2){
        if(t1.pos.equals(t2.pos))
            return 1;
        else return 0;
    }
	
	/**
	 * s函数
	 * @param t1
	 * @param t2
	 * @return
	 */
	private float getWordScore(DependencyTree t1, DependencyTree t2){
		float score=0;
		if(wg!=null){
			if(wg.isSym(t1.word, t2.word))
				score = 1;
			else if(wg.isAntonym(t1.word, t2.word))
				score = -1;
		}else if(t1.word.equals(t2.word))
			score = 1;
		return score;
	}
	
	/**
	 * 深度修正参数
	 * @param depth
	 * @return
	 */
	private float getDepthModify(int depth){
		if(depth == 1)
			return 1;
		else if(depth == 2)
			return 0.9f;
		else if(depth == 3)
			return 0.8f;
		else if(depth == 4)
			return 0.65f;
		else if(depth == 5)
			return 0.5f;
		else if(depth == 6)
			return 0.3f;
		else if(depth == 7)
			return 0.1f;
		else return 0;
	}

	/**
	 * 分数归一化
	 * @param t
	 * @return
	 */
	private float getBase(DependencyTree t){
		return getDepScore(t,t,1);
	}

	public void setWordGraph(WordGraph wg) {
		this.wg = wg;
		
	}
}