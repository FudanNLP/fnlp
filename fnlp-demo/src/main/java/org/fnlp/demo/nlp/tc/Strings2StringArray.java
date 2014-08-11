package org.fnlp.demo.nlp.tc;

import java.util.ArrayList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.Pipe;

public class Strings2StringArray extends Pipe{
	public Strings2StringArray() {
	}

	@Override
	public void addThruPipe(Instance inst) {
		String[] data =  (String[]) inst.getData();
		ArrayList<String> newdata=new ArrayList<String>();
		for(int i=0;i<data.length;i++)
			newdata.add(data[i]);
		inst.setData(newdata);
	}
}