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

import java.io.Serializable;
import java.util.ArrayList;

import org.fnlp.ml.types.Instance;

/**
 * Pipe组合，按先后顺序进行数据类型转换
 * @author xpqiu
 *
 */
public class SeriesPipes extends Pipe  implements Serializable {


	private static final long serialVersionUID = -9080917611618077919L;
	private ArrayList<Pipe> pipes = null;

	public int size(){
		return pipes.size();
	}

	public SeriesPipes(Pipe[] pipes)	{
		this.pipes = new ArrayList<Pipe>(pipes.length);
		for(int i = 0; i < pipes.length; i++){
			if(pipes[i]!=null)
				this.pipes.add(pipes[i]);
		}
	}

	public ArrayList<Pipe> getPipes()	{
		return pipes;
	}

	@Override
	public void addThruPipe(Instance carrier) throws Exception {
		for(int i = 0; i < pipes.size(); i++)
			pipes.get(i).addThruPipe(carrier);
	}


	public Pipe getPipe(int id)	{
		if (id < 0 | id > pipes.size())
			return null;
		return pipes.get(id);
	}

	/**
	 * 删除使用类标签的Pipe
	 */
	public void removeTargetPipe() {
		for(int i = pipes.size()-1; i >=0 ; i--){
			Pipe p = pipes.get(i);
			if(p instanceof SeriesPipes) {
				((SeriesPipes) p).removeTargetPipe();				
			}
			else if(p.useTarget){
				pipes.remove(i);
				i--;
			}
		}

	}
	
	public void addPipe(Pipe pipe)	{
		if (pipes == null)
			pipes = new ArrayList<Pipe>();
		pipes.add(pipe);
	}
}