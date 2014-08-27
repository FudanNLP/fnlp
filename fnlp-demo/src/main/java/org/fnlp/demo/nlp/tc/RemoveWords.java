package org.fnlp.demo.nlp.tc;

import java.util.ArrayList;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.pipe.Pipe;

public class RemoveWords extends Pipe{
	String[] list=new String[]{"&nbsp;","&nbsp"};
	public void addThruPipe(Instance inst) {
		String data =  (String) inst.getData();
		for(int i=0;i<list.length;i++){
			String str=list[i];
			data=data.replace(str, "");
		}
		inst.setData(data);
	}
}
