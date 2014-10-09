package org.fnlp.nlp.parser.dep.analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResultReader {

	BufferedReader reader = null;
	AnalysisSentence next = null;
	List<String[]> carrier = new ArrayList<String[]>();

	public ResultReader(String filepath) throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(
				filepath), "UTF-8"));
		advance();
	}

	private void advance() throws IOException {
		String line = null;
		carrier.clear();
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.matches("^$"))
				break;
			carrier.add(line.split("\\t+|\\s+"));
		}

		next = null;
		if (!carrier.isEmpty()) {
			String[] forms = new String[carrier.size()];
			String[] tags = new String[carrier.size()];
			int[] goldhead = new int[carrier.size()];
			String[] goldrel = new String[carrier.size()];
			int[] predhead = new int[carrier.size()];
			String[] predrel = new String[carrier.size()];
			for (int i = 0; i < carrier.size(); i++) {
				String[] tokens = carrier.get(i);
				forms[i] = tokens[0];
				tags[i] = tokens[1];
				goldhead[i] = Integer.parseInt(tokens[2]);
				goldrel[i] = tokens[3];
				predhead[i] = Integer.parseInt(tokens[4]);
				predrel[i] = tokens[5];
			}

			next = new AnalysisSentence(forms, tags, goldhead, goldrel, predhead, predrel);
		}
	}

	public boolean hasNext() {
		return (next != null);
	}

	public AnalysisSentence next() {
		AnalysisSentence cur = next;
		try {
			advance();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cur;
	}
}
