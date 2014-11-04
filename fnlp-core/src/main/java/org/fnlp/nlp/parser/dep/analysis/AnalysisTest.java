package org.fnlp.nlp.parser.dep.analysis;

import java.io.IOException;

public class AnalysisTest {
	private int errhead = 0;
	private int headsum = 0;
	private int errhead_dep = 0;
	private int head_depsum = 0;
	private int errsent = 0;
	private int sent_sum = 0;
	private int errroot = 0;
	private int root_sum = 0;

	public void test(String resultFile) throws IOException{
		ResultReader reader = new ResultReader(resultFile);		
		while(reader.hasNext()){
			AnalysisSentence sent = reader.next();			
			//分析一下UAS,LAS,CM,ROOT
			judgeUAS(sent);	
			judgeLAS(sent);
			judgeRoot(sent);
		}
		print();
	}
	
	private void judgeRoot(AnalysisSentence sent){
		for(int i=0; i<sent.length(); i++){
			if(sent.goldhead[i] == -1){
				root_sum++;				
				if(sent.goldhead[i] != sent.predhead[i]){
					errroot++;
				}
				continue;
			}
		}
	}
	
	private void judgeLAS(AnalysisSentence sent){
		for(int i=0; i<sent.length(); i++){
			if(isBiaodian(i, sent))
				continue;
			head_depsum++;
			if(sent.goldhead[i] == -1){
				if(sent.goldhead[i] != sent.predhead[i]){
					errhead_dep++;
				}
				continue;
			}
			if(sent.goldhead[i] != sent.predhead[i]
					|| !sent.goldrel[i].equals(sent.predrel[i])){
				errhead_dep++;
			}
		}
	}
	
	private void judgeUAS(AnalysisSentence sent){
		boolean isUEM = true;
		for(int i=0; i<sent.length(); i++){
			if(isBiaodian(i, sent))
				continue;
			headsum++;
			if(sent.goldhead[i] != sent.predhead[i]){
				errhead++;
				isUEM = false;
			}
		}
		if(!isUEM){
			errsent++;
		}
		sent_sum++;
	}
	
	private boolean isBiaodian(int i, AnalysisSentence sent){
		String[] posBiaodian = new String[]{",",".","``",":","''","$","-RRB-","-LRB-","#","SYM", "PU"};
		for(String s:posBiaodian){
			if(sent.tags[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	private void print(){
		System.out.println("***************************************");
		System.out.printf("rate(UAS):\t%.8f\ttotal(words):\t%d\n", 1 - 1.0
				* errhead / headsum, headsum);
		System.out.printf("rate(LAS):\t%.8f\ttotal(words):\t%d\n", 1 - 1.0
				* errhead_dep / head_depsum, head_depsum);
		System.out.printf("rate(root):\t%.8f\ttotal(roots):\t%d\n", 1 - 1.0
				* errroot / root_sum, root_sum);
		System.out.printf("rate(UEM):\t%.8f\ttotal(sent):\t%d\n", 1 - 1.0
				* errsent / sent_sum, sent_sum);
	}

}
