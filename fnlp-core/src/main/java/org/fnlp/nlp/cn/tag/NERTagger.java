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

package org.fnlp.nlp.cn.tag;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.PartOfSpeech;
import org.fnlp.nlp.cn.Tags;
import org.fnlp.util.MyCollection;

/**
 * 实体名标注器
 * 通过词性标注实现。
 * @author 邱锡鹏
 *
 */
public class NERTagger {


	private static POSTagger pos;

	public NERTagger(CWSTagger cws, String str) throws Exception {
		pos = new POSTagger(cws, str);
	}

	public NERTagger(String segmodel, String posmodel) throws Exception {
		pos = new POSTagger(segmodel,posmodel);
	}
	public NERTagger(POSTagger posmodel){
		pos = posmodel;
	}

	public HashMap<String, String> tag(String src) {
		HashMap<String, String> map = new HashMap<String, String>();
		tag(src,map);
		return map;
	}

	public void tag(String src,HashMap<String, String> map) {

		String[] sents = src.split("\\n+");
		try {
			for (int i = 0; i < sents.length; i++) {
				String[][] res = pos.tag2Array(sents[i]);
				if(res!=null){
					for(int j=0;j<res[0].length;j++){
						if(PartOfSpeech.isEntiry(res[1][j])){
							map.put(res[0][j], res[1][j]);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, String> tagFile(String input) {
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					input), "utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str = lbin.readLine();
			HashMap<String, String> map = new HashMap<String, String>();
			while (str != null) {
				tag(str,map);
				str = lbin.readLine();
			}
			lbin.close();
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	public void tagFile(String input,String output) {
		HashMap<String, String> map = tagFile(input);
		MyCollection.write(map.keySet(), output);
	}

	public static void main(String[] args) throws Exception {
		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("f", false, "segment file. Default string mode.");
		opt.addOption("s", false, "segment string");
		BasicParser parser = new BasicParser();
		CommandLine cl = parser.parse(opt, args);

		if (args.length == 0 || cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp(
					"Tagger:\n"
							+ "java edu.fudan.nlp.tag.NERTagger -f segmodel posmodel input_file output_file;\n"
							+ "java edu.fudan.nlp.tag.NERTagger -s segmodel posmodel string_to_segement",
							opt);
			return;
		}
		String[] arg = cl.getArgs();
		String segmodel;
		String posmodel;
		String input;
		String output = null;
		if (cl.hasOption("f") && arg.length == 4) {
			segmodel = arg[0];
			posmodel = arg[1];
			input = arg[2];
			output = arg[3];
		} else if (arg.length == 3) {
			segmodel = arg[0];
			posmodel = arg[1];
			input = arg[2];
		} else {
			System.err.println("paramenters format error!");
			System.err.println("Print option \"-h\" for help.");
			return;
		}
		NERTagger ner = new NERTagger(segmodel,posmodel);
		if (cl.hasOption("f")) {
			ner.tagFile(input,output);
		} else {
			 HashMap<String, String> map = ner.tag(input);
			System.out.println(map);
		}
	}



}