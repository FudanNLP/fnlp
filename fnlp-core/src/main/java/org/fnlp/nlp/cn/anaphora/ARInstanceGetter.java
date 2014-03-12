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

import org.fnlp.ml.types.Instance;

/**
 * 获得指代消解的样本
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */

public class ARInstanceGetter {

	private Instance instance;
	public ARInstanceGetter(FeatureGeter fBuilder){	
		this.instance = new Instance(fBuilder.getFeatrue(),
				fBuilder.getInst().getTarget());
		this.instance.setSource(fBuilder.getInst().getData());
	}
	
	public Instance getInstance(){
		return this.instance;
	}

	
}