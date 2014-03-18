package org.fnlp.train.prepare;

public class BatchTrain {

	public static void main(String[] args) throws Exception {
		long time0 = System.currentTimeMillis();
		PrepareSeg.main(null);
		args = new String[]{"Tag","../models/seg.m","1.0E-6f"};
		ModelOptimization.main(args);
		long time1 = System.currentTimeMillis();
		
		PreparePOS.main(null);
		args = new String[]{"Tag","../models/pos.m","1.0E-6f"};
		ModelOptimization.main(args);
		
		long time2 = System.currentTimeMillis();
		PrepareDep.main(null);
		args = new String[]{"Dep","../models/dep.m","1.0E-6f"};
		ModelOptimization.main(args);
		long time3 = System.currentTimeMillis();
		
		System.out.println("Done!");
		System.out.println("Seg Total Time: "+(time1-time0)/60000.0f+"min");
		System.out.println("Tag Total Time: "+(time2-time1)/60000.0f+"min");
		System.out.println("Dep Total Time: "+(time3-time2)/60000.0f+"min");
		

	}

}
