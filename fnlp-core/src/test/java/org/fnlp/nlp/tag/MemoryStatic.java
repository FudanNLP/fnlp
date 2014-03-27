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

package org.fnlp.nlp.tag;

public class MemoryStatic {
	private static final Runtime s_runtime = Runtime.getRuntime ();
	private static long start;
	private static long usedMemory ()
	{
		return s_runtime.totalMemory () - 
				s_runtime.freeMemory ();
	}
	private static void runGC ()
	{
		long usedMem1 = usedMemory (), usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i)
		{
			s_runtime.runFinalization ();
			s_runtime.gc ();
			Thread.currentThread ().yield ();
			usedMem2 = usedMem1;
			usedMem1 = usedMemory ();
		}
	}
	public static void start() {
		runGC();
		start = s_runtime.totalMemory() - s_runtime.freeMemory();		
	}
	public static long end() {
		runGC();
		long end = s_runtime.totalMemory() - s_runtime.freeMemory();
		long diff = end - start;
		return diff;
	}
	
	
}