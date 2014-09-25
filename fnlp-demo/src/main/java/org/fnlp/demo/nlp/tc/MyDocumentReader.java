package org.fnlp.demo.nlp.tc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.fnlp.data.reader.DocumentReader;
import org.fnlp.data.reader.Reader;
import org.fnlp.ml.types.Instance;
import org.fnlp.util.MyFiles;

public class MyDocumentReader extends Reader{

	List<File> files;
	Instance cur;
	Charset charset;
	public MyDocumentReader(String path) {
		this(path, "UTF-8");
	}
	public MyDocumentReader(String path, String charsetName) {
		files = MyFiles.getAllFiles(path,null);
		charset = Charset.forName(charsetName);
	}
	public boolean hasNext() {
		if (files.isEmpty())
			return false;
		nextDocument();
		return true;
	}
	public Instance next() {
		return cur;
	}
	void nextDocument() {
		StringBuffer buff = new StringBuffer();
		File f = files.remove(files.size()-1);
		try {
			BufferedReader cf = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), charset));
			String line = null;
			while((line = cf.readLine()) != null)	{
				buff.append(line);
				buff.append('\n');
			}
			cf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String path=f.getPath();
		int pos=path.lastIndexOf("\\");
		path=path.substring(0, pos);
		pos=path.lastIndexOf("\\");
		path=path.substring(pos+1);
		cur = new Instance(buff.toString(), path);
		cur.setTempData(f.getPath());
		buff = null;
	}
}
