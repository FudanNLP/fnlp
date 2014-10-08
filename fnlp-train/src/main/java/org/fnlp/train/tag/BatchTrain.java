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

package org.fnlp.train.tag;

import org.fnlp.train.parsing.DepPrepare;
import org.fnlp.train.pos.POSPrepare;
import org.fnlp.train.seg.SegPrepare;


public class BatchTrain {

	public static void main(String[] args) throws Exception {
		long time0 = System.currentTimeMillis();
		SegPrepare.main(null);
		args = new String[]{"Tag","../models/seg.m","1.0E-6f"};
		ModelOptimization.main(args);
		long time1 = System.currentTimeMillis();
		
		POSPrepare.main(null);
		args = new String[]{"Tag","../models/pos.m","1.0E-6f"};
		ModelOptimization.main(args);
		
		long time2 = System.currentTimeMillis();
		DepPrepare.main(null);
		args = new String[]{"Dep","../models/dep.m","1.0E-6f"};
		ModelOptimization.main(args);
		long time3 = System.currentTimeMillis();
		
		System.out.println("Done!");
		System.out.println("Seg Total Time: "+(time1-time0)/60000.0f+"min");
		System.out.println("Tag Total Time: "+(time2-time1)/60000.0f+"min");
		System.out.println("Dep Total Time: "+(time3-time2)/60000.0f+"min");
		

	}

}