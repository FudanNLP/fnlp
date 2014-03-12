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

package org.fnlp.nlp.pipe.nlp;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.tag.CWSTagger;
import org.fnlp.nlp.pipe.Pipe;

/**
 * 进行分词等操作
 * @author xpqiu
 *
 */
public class CNPipe extends Pipe{

	private static final long serialVersionUID = -2329969202592736092L;
	private transient CWSTagger seg;

	public CNPipe() {
	}

	public CNPipe(CWSTagger seg) {
		this.seg = seg;
	}

	@Override
	public void addThruPipe(Instance inst) {
		String data = (String) inst.getData();
		String[] newdata = seg.tag2Array(data);
		inst.setData(newdata);
	}
}