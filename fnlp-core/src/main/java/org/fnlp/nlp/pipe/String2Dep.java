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

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.nlp.parser.dep.JointParser;
import org.fnlp.util.exception.UnsupportedDataTypeException;
/**
 * 将字符串转换为依次句法结构
 * @author xpqiu
 *
 */
public class String2Dep extends Pipe{

	private static final long serialVersionUID = -3646974372853044208L;
	private static CNFactory factory;

	public String2Dep(){
		this.factory = CNFactory.getInstance();
	}
	
	public String2Dep(CNFactory factory){
		this.factory = factory;
	}

	@Override
	public void addThruPipe(Instance inst) throws Exception {
		String data = (String)inst.getData();
//		System.out.println(data);
		DependencyTree t = factory.parse2T(data);
		

		inst.setData(t);
	}




}