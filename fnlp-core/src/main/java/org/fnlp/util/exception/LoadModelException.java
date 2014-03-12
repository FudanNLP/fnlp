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

import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadModelException extends Exception {
	
	private static final long serialVersionUID = -3933859344026018386L;

	public LoadModelException(Exception e, String file) {
		super(e);
		if( e instanceof FileNotFoundException) {
			System.out.println("模型文件不存在： "+ file);
		} else if (e instanceof ClassNotFoundException) {			
			System.out.println("模型文件版本错误。");
		} else if (e instanceof IOException) {
			System.out.println("模型文件读入错误： "+file);
			
		}
		e.printStackTrace();
	}

	public LoadModelException(String msg) {
		super(msg);
		printStackTrace();
	}
}