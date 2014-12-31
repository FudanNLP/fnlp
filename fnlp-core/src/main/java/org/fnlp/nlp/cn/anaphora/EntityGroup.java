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

package org.fnlp.nlp.cn.anaphora;

import java.util.Iterator;

/**
 * 实体对
 * @author jszhao,xpqiu
 * @version 2.0
 * @since FudanNLP 1.5
 */
public class EntityGroup {
	/**
	 *  先行词
	 */
	public Entity antecedent;
	/**
	 * 照应语
	 */
	public Entity anaphor;      
	public int weight;
	
	public EntityGroup( Entity antecedent, Entity anaphor ){
		this.antecedent=antecedent;
		this.anaphor = anaphor;
	}
	
	
	/**
	 * 用标注工具的最后结果
	 */
	public String toString(){
		
		StringBuilder strBuf = new StringBuilder();		
			strBuf.append(antecedent.getData())
			.append("(")
			.append(antecedent.start)
			.append(") <-- ")
			.append(anaphor.getData())
			.append("(")
			.append(anaphor.start)
			.append(")\n");
		return strBuf.toString();
	}


	public void setWeight(int w) {
		weight = w;
		
	}


	public int getWeight() {		
		return weight;
	}

}