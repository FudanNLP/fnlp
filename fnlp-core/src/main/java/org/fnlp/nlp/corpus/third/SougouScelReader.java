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

package org.fnlp.nlp.corpus.third;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fnlp.util.MyCollection;
import org.fnlp.util.MyFiles;

public class SougouScelReader {

	public SougouScelMdel read(File file) throws IOException {
		return read(new FileInputStream(file));
	}

	public SougouScelMdel read(URL url) throws IOException {
		return read(url.openStream());
	}

	protected ByteArrayOutputStream output=new ByteArrayOutputStream();

	protected String readString(DataInputStream input,int pos,int[] reads) throws IOException {
		int read=reads[0];
		input.skip(pos-read);
		read=pos;
		output.reset();
		while(true) {
			int c1 = input.read();
			int c2 = input.read();
			read+=2;
			if(c1==0 && c2==0) {
				break;
			} else {
				output.write(c1);
				output.write(c2);
			}
		}
		reads[0]=read;
		return new String(output.toByteArray(),encoding);
	}

	protected static String encoding = "UTF-16LE";

	public SougouScelMdel read(InputStream in) throws IOException {
		SougouScelMdel model = new SougouScelMdel();
		DataInputStream input = new DataInputStream(in);
		int read;
		try {
			byte[] bytes = new byte[4];
			input.readFully(bytes);
			assert (bytes[0] == 0x40 && bytes[1] == 0x15 && bytes[2] == 0 && bytes[3] == 0);
			input.readFully(bytes);
			int flag1 = bytes[0];
			assert (bytes[1] == 0x43 && bytes[2] == 0x53 && bytes[3] == 0x01);
			int[] reads=new int[]{8};
			model.setName(readString(input,0x130,reads));
			model.setType(readString(input,0x338,reads));
			model.setDescription(readString(input,0x540,reads));
			model.setSample(readString(input,0xd40,reads));
			read = reads[0];
			input.skip(0x1540 - read);
			read=0x1540;
			input.readFully(bytes);
			read += 4;
			assert (bytes[0] == (byte) 0x9D && bytes[1] == 0x01 && bytes[2] == 0 && bytes[3] == 0);
			bytes = new byte[128];
			Map<Integer, String> pyMap = new LinkedHashMap<Integer, String>();
			while (true) {
				int mark = readUnsignedShort(input);
				int size = input.readUnsignedByte();
				input.skip(1);
				read += 4;
				assert (size > 0 && (size % 2) == 0);
				input.readFully(bytes, 0, size);
				read += size;
				String py = new String(bytes, 0, size, encoding);
				//System.out.println(py);
				pyMap.put(mark, py);
				if ("zuo".equals(py)) {
					break;
				}
			}
			if (flag1 == 0x44) {
				input.skip(0x2628 - read);
			} else if (flag1 == 0x45) {
				input.skip(0x26C4 - read);
			} else {
				throw new RuntimeException("出现意外，联系作者");
			}
			StringBuffer buffer = new StringBuffer();
			Map<String, List<String>> wordMap = new LinkedHashMap<String, List<String>>();
			while (true) {
				int size = readUnsignedShort(input);
				if (size < 0) {
					break;
				}
				int count = readUnsignedShort(input);
				int len = count / 2;
				assert (len * 2 == count);
				buffer.setLength(0);
				for (int i = 0; i < len; i++) {
					int key = readUnsignedShort(input);
					buffer.append(pyMap.get(key)).append("'");
				}
				buffer.setLength(buffer.length() - 1);
				String py = buffer.toString();
				List<String> list = wordMap.get(py);
				if (list == null) {
					list = new ArrayList<String>();
					wordMap.put(py, list);
				}
				for (int i = 0; i < size; i++) {
					count = readUnsignedShort(input);
					if (count > bytes.length) {
						bytes = new byte[count];
					}
					input.readFully(bytes, 0, count);
					String word = new String(bytes, 0, count, encoding);
					//接下来12个字节可能是词频或者类似信息
					input.skip(12);
					list.add(word);
				}
			}
			//System.out.println(wordMap.size());
			model.setWordMap(wordMap);
			return model;
		} finally {
			in.close();
		}
	}

	protected final int readUnsignedShort(InputStream in) throws IOException {
		int ch1 = in.read();
		int ch2 = in.read();
		if ((ch1 | ch2) < 0) {
			return Integer.MIN_VALUE;
		}
		return (ch2 << 8) + (ch1 << 0);
	}

	public static void main(String[] strs) throws IOException{
		String path = "./data/FNLPDATA/dict-sogou-input/scel";
		String opath = "./data/FNLPDATA/dict-sogou-input/txt/";
		List<File> files = MyFiles.getAllFiles(path, "scel");
		for(File f:files){
			SougouScelMdel model = new SougouScelReader().read(f);
			System.out.println(model.getName()); //名称
			System.out.println(model.getType());  //类型
			System.out.println(model.getDescription()); //描述
			System.out.println(model.getSample());  //样例
			Map<String, List<String>> words = model.getWordMap(); //词<拼音,词>

			System.out.println(words.size());
			String name = f.getName();
			int idx = name.indexOf("-");
			if(idx==-1)
				continue;
			
			String pos = name.substring(0,idx-1);
			int maxlen = Integer.valueOf(name.substring(idx-1,idx));
			int idx2= name.lastIndexOf(".");
			name = name.substring(idx,idx2);
			name = pos+name;
			
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(opath+name+".txt"), "UTF-8"));
			Iterator<Entry<String, List<String>>> it1 = words.entrySet().iterator();
			while(it1.hasNext()){
				Entry<String, List<String>> entry = it1.next();				
				Iterator<String> it = entry.getValue().iterator();
				int i=0;
				while (it.hasNext()) {
					String en = it.next();
					if(en.length()<=maxlen){
						bout.write(en);
						i++;
						if(it.hasNext())
							bout.write("\n");
					}
				}
				if(i>0&&it1.hasNext())
					bout.write("\n");
			}
			bout.close();

		}
	}

}

//自行将此类提出来为public class
class SougouScelMdel {

	private Map<String, List<String>> wordMap;

	private String name;
	private String type;
	private String description;
	private String sample;

	public Map<String, List<String>> getWordMap() {
		return wordMap;
	}

	void setWordMap(Map<String, List<String>> wordMap) {
		this.wordMap = wordMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



}