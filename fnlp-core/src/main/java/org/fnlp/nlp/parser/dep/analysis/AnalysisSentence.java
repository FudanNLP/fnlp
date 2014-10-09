package org.fnlp.nlp.parser.dep.analysis;

public class AnalysisSentence {
	public String forms[];
	public String tags[];
	public int goldhead[];
	public String goldrel[];
	public int predhead[];
	public String predrel[];
	public AnalysisSentence(String[] forms, String[] tags, int[] goldhead,
			String[] goldrel, int[] predhead, String[] predrel) {
		super();
		this.forms = forms;
		this.tags = tags;
		this.goldhead = goldhead;
		this.goldrel = goldrel;
		this.predhead = predhead;
		this.predrel = predrel;
	}
	
	public int length(){
		return forms.length;
	}
}
