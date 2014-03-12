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

package org.fnlp.util.exception;

/**
 * 不支持数据类型
 * @author xpqiu
 *
 */
public class NotImplementedException extends Exception {

	private static final long serialVersionUID = -7879174759276938120L;

	
	public NotImplementedException(String msg) {
		super(msg);
		printStackTrace();
	}


	public NotImplementedException() {
		super("该方法暂未实现");
		printStackTrace();
	}
}