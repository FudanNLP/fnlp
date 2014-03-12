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
