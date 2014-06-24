package org.fnlp.ml.eval;

import java.io.IOException;

import org.fnlp.util.MyFiles;

public class ScoreUsage {

	public static void main(String[] args) throws IOException {
		
		Score ss = new Score();
		
		int numofclass = 10;
		
		String str = MyFiles.loadString("../tmp/Sogou_SVM");
		
		 String[] s = str.split("\n");
	        Integer[] golden= new Integer[s.length];
	        Integer[] pred = new Integer[s.length];
	        for (int i = 0; i < s.length; i++) {
	            String[] ele = s[i].split("\\s");
	            int g = Integer.parseInt(ele[0]);
	            int p = Integer.parseInt(ele[1]);
	            golden[i] = g;
	            pred[i] = p;
	        }
	        String res = ss.score(pred, golden, numofclass);
	        System.out.println(res);

	}

}
