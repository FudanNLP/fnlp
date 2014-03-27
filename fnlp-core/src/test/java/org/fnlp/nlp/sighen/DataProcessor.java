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

package org.fnlp.nlp.sighen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class DataProcessor {
	public static int id = 1;
	public static TreeMap<String, String> dict = new TreeMap<String, String>();
	
	public static int c = 0, e = 0, nn = 0, vv = 0, va = 0, nr = 0, jj = 0;
	
	public static void tag(Scanner scanner, PrintWriter pw) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] sa = line.split("\\s+");
			for(int i = 0; i < sa.length; i++) {
				String[] wa = sa[i].split("/");
				for(int j = 0; j < wa[0].length(); j++) {
					String seg;
					if(wa[0].length() == 1)
						seg = "S";
					else if(j == 0)
						seg = "B";
					else if(j == wa[0].length() - 1)
						seg = "E";
					else
						seg = "M";
					pw.println(wa[0].charAt(j) + "\t" + seg + "-" + wa[1]); 
				}
			}
			pw.println();
		}
	}
	
	public static void tag2(Scanner scanner, PrintWriter pw, String corpus) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if(line.length() == 0)
				continue;
			String[] sa = line.split("\\s+");
			for(int i = 0; i < sa.length; i++) {
				String[] wa = sa[i].split("/");
				if(corpus == null)
					pw.println(wa[0] + "\t" + wa[1]);
				else
					pw.println(wa[0] + "\t" + wa[1] + "_" + corpus);
			}
			pw.println();
		}
	}
	

	public static void mergeResult(Scanner scanner, Scanner scanner2, PrintWriter pw) {
		while(scanner.hasNext() && scanner2.hasNext()) {
			String line = scanner.nextLine();
			String[] sa = line.split("\\s+");
			line = scanner2.nextLine();
			String[] sa2 = line.split("\\s+");
			if(line.trim().length() == 0) {
				pw.println();
				continue;
			}
			if(!(sa[0].equals(sa2[0]) && sa[1].equals(sa2[1])))
				System.out.println("error : " + sa[0] + " " + sa[1] + " / " + sa2[0] + sa2[1]);
			if(!(sa[1].equals(sa[2]) && sa2[1].equals(sa2[2]))) {
				pw.println(sa[0] + "\t" + sa[1] + "\t" + sa[2] + "\t" + sa2[2]);
			}
//			pw.println();
		}
	}
	
	public static void seg(Scanner scanner, PrintWriter pw) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] sa = line.split("\\s+");
			for(int i = 0; i < sa.length; i++) {
				String word = sa[i];
				for(int j = 0; j < word.length(); j++) {
					String seg;
					if(word.length() == 1)
						seg = "S";
					else if(j == 0)
						seg = "B";
					else if(j == word.length() - 1)
						seg = "E";
					else
						seg = "M";
					pw.println(word.charAt(j) + "\t" + seg); 
				}
			}
			pw.println();
		}
	}
	
	public static void tag_chinese_english(Scanner scanner, PrintWriter pw) {
		TreeMap<String, Integer> map_all = new TreeMap<String, Integer>();
		TreeMap<String, Integer> map_change = new TreeMap<String, Integer>();
		
		boolean lastIsEng = false;
		
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] sa = line.split("\\s+");
			for(int i = 0; i < sa.length; i++) {
				String[] wa = sa[i].split("/");
				if(wa[1].equals("NN") 
//						|| wa[1].equals("JJ")
//						|| wa[1].equals("NR")
//						|| wa[1].equals("VA")
						|| wa[1].equals("VV")) {
					addOne(map_all, wa[1]);
					if(Math.random() > 0.9) {
						if(lastIsEng == false) {
							addOne(map_change, wa[1]);
							pw.println("ENG" + "\tS-" + wa[1]); 
							lastIsEng = true;
							continue;
						} 
					}
				}
				
				lastIsEng = false;
				
				for(int j = 0; j < wa[0].length(); j++) {
					String seg;
					if(wa[0].length() == 1)
						seg = "S";
					else if(j == 0)
						seg = "B";
					else if(j == wa[0].length() - 1)
						seg = "E";
					else
						seg = "M";
					pw.println(wa[0].charAt(j) + "\t" + seg + "-" + wa[1]); 
				}
			}
			pw.println();
		}
		
		System.out.println(map_all + "\n" + map_change);
	}
	
	public static void addOne(TreeMap<String, Integer> map, String key) {
		Integer i = map.get(key);
		if(i == null)
			map.put(key, 1);
		else
			map.put(key, i + 1);
	}
	
	public static void ner(Scanner scanner, PrintWriter pw) {
		ArrayList<String> file = new ArrayList<String>();
		while(scanner.hasNext())
			file.add(scanner.nextLine());
		for(int i = 0; i < file.size(); i++) {
			String line = file.get(i);
			String[] lineArray = line.split("\\s");
			if(lineArray.length != 2) {
				pw.println(line);
				continue;
			} else if(lineArray[1].equals("N")) {
				pw.println(lineArray[0] + " " + "S-N");
				continue;
			}
			boolean isNewStart = false;
			if(i + 1 < file.size()) {
				String nl = file.get(i+1);
				String[] nextLine = nl.split("\\s");
				if(nl.trim().equals(""))
					isNewStart = true;
				else if(nextLine[1].equals("N") || nextLine[1].indexOf("B-") >= 0)
					isNewStart = true;
			} else 
				isNewStart = true;
			String[] label = lineArray[1].split("-");
			if(label[0].equals("B"))
				if(isNewStart == false)
					pw.println(line);
				else
					pw.println(lineArray[0] + " S-" + label[1]);
			else if(label[0].equals("I"))
				if(isNewStart == false)
					pw.println(lineArray[0] + " M-" + label[1]);
				else
					pw.println(lineArray[0] + " E-" + label[1]);
			else
				System.out.println("error : " + line);
		}
	}
	
	
	
	public static void eng(Scanner scanner, PrintWriter pw) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			if(line.contains("ENG"))
				pw.println(line);
		}
		pw.println();
	}

	public static void eng_oov(Scanner scanner, PrintWriter pw, Scanner scanner2, PrintWriter pw2) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
			String line2 = scanner2.nextLine();
			if(line.contains("ENG")) {
				String s = (line.split("\\s")[0]).split("_")[1];
				if(Integer.parseInt(s) > 5189) {
					pw.println(line);
					pw2.println(line2);
				}
			}
		}
		pw.println();
		pw2.println();
	}
	
	public static void tag_chinese_english(Scanner s_train, PrintWriter pw_train, PrintWriter pw_train2, 
			Scanner s_test, PrintWriter pw_test, PrintWriter pw_test2) {
		tag_chinese_english(s_train, pw_train, pw_train2, false);
		tag_chinese_english(s_test, pw_test, pw_test2, true);
	}
	
	public static void tag_chinese_english(Scanner s_train, PrintWriter pw_train, PrintWriter pw_train2, boolean isTestSet) {
		TreeMap<String, Integer> map_all = new TreeMap<String, Integer>();
		TreeMap<String, Integer> map_change = new TreeMap<String, Integer>();
		
		boolean lastIsEng = false;
		
		while(s_train.hasNext()) {
			String line = s_train.nextLine();
			String[] sa = line.split("\\s+");
			for(int i = 0; i < sa.length; i++) {
				String[] wa = sa[i].split("/");
				if(wa[1].equals("NN") 
//						|| wa[1].equals("JJ")
//						|| wa[1].equals("NR")
//						|| wa[1].equals("VA")
						|| wa[1].equals("VV")) {
					addOne(map_all, wa[1]);
					if(Math.random() > 0.95) {
						if(lastIsEng == false) {
							addOne(map_change, wa[1]);
							String tag = dict.get(wa[0]);
							if(tag == null) {
								pw_train.println("ENG_" + (id++) + "\tS-" + wa[1]); 
								if(isTestSet == false)
									dict.put(wa[0], "ENG_" + (id-1));
							} else
								pw_train.println(tag + "\tS-" + wa[1]); 
							
							pw_train2.println("ENG" + "\tS-" + wa[1]); 
							
							lastIsEng = true;
							continue;
						} 
					}
				}
				
				lastIsEng = false;
				
				for(int j = 0; j < wa[0].length(); j++) {
					String seg;
					if(wa[0].length() == 1)
						seg = "S";
					else if(j == 0)
						seg = "B";
					else if(j == wa[0].length() - 1)
						seg = "E";
					else
						seg = "M";
					pw_train.println(wa[0].charAt(j) + "\t" + seg + "-" + wa[1]); 
					pw_train2.println(wa[0].charAt(j) + "\t" + seg + "-" + wa[1]);
				}
			}
			pw_train.println();
			pw_train2.println();
		}
		
		System.out.println(map_all + "\n" + map_change);
		System.out.println((id-1) + " " + dict.size());
	}
	
	
	public static void real_ce(Scanner scanner, PrintWriter pw) {
		while(scanner.hasNext()) {
			char lastChar = ' ';
			StringBuffer sb = new StringBuffer();
			String line = scanner.nextLine().trim();
			if(line.equals(""))
				continue;
			char[] ca = line.toCharArray();
			for(int i = 0; i < ca.length; i++) {
				if(i != 0 && 
						(isSeperation(ca[i]) == true 
						|| isSeperation(ca[i]) != isSeperation(lastChar))) {
					print(sb, pw);
					sb = new StringBuffer();
				}
				sb.append(ca[i]);
				lastChar = ca[i];
			}
			print(sb, pw);
			pw.println();
		}
		System.out.println(c + " " + e + " NN=" + nn + " VV=" + vv + " VA=" + va + " NR=" + nr + " JJ=" + jj);
	}
	
	public static void print(StringBuffer sb, PrintWriter pw) {
		String s = sb.toString().trim();
		
		if(s.equals("") == false) {	
			if(isENG(s)) {
				e++;
				s = s.replaceAll("\\s", "_");
				String tag = s.substring(s.lastIndexOf("_") + 1, s.length());
				
				if(tag.equals("NN"))
					nn++;
				if(tag.equals("VV"))
					vv++;
				if(tag.equals("VA"))
					va++;
				if(tag.equals("NR"))
					nr++;
				if(tag.equals("JJ"))
					jj++;
				if(tag.equals("NN") || tag.equals("VV") || tag.equals("NR") || tag.equals("VA") || tag.equals("JJ"))
					pw.println("ENG" + "\tS-" + tag);	
//				else
//					System.out.println(sb.toString() + " " + tag);
//				pw.println(s + "\tS-NN");
			} else {
				c++;
				pw.println(s + "\tunknown");
			}
		} 
	}
	
	public static boolean isENG(String sb) {
		char[] ca = sb.toCharArray();
		for(int i = 0; i < ca.length; i++)
			if (Character.isLowerCase(ca[i]) || Character.isUpperCase(ca[i]))
				return true;
		return false;
	}
	
	public static boolean isSeperation(char c) {
		if (Character.isLowerCase(c) || Character.isUpperCase(c))
			return false;
		else if (Character.isDigit(c))
			return false;
		else if (Character.isWhitespace(c) || Character.isSpaceChar(c))
			return false;
		else if (Pattern.matches("\\pP|\\pS", c + "")) {
			return true;
		} else
			return true;
	}
	
	public static void addCorpusName(Scanner scanner, PrintWriter pw, String cn) {
		while(scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			if(line.length() == 0)
				pw.println();
			else
				pw.println(line + "_" + cn);
		}
	}
		
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Scanner scanner = new Scanner(new FileInputStream(args[0]), "utf-16");//, "utf-8");
		Scanner scanner_t = null;
		PrintWriter pw = new PrintWriter(args[1], "utf-8");
	
		
		PrintWriter pw2 = null;	
		PrintWriter pw_t = null;
		PrintWriter pw_t2 = null;
		if(args.length == 6) {
			pw2 = new PrintWriter(args[2], "utf-8");
			scanner_t = new Scanner(new FileInputStream(args[3]), "utf-16");
			pw_t = new PrintWriter(args[4], "utf-8");
			pw_t2 = new PrintWriter(args[5], "utf-8");
		} else if(args.length == 4) {
			scanner_t = new Scanner(new FileInputStream(args[2]), "utf-8");
			pw_t = new PrintWriter(args[3], "utf-8");
		} else if(args.length == 3) {
			scanner_t = new Scanner(new FileInputStream(args[2]), "utf-8");
		}
		
		tag(scanner, pw);	//utf-16
//		tag2(scanner, pw, "PKU");	//utf-16
//		mergeResult(scanner, scanner_t, pw);
//		addCorpusName(scanner, pw, "CTB"); 	//utf-8
//		seg(scanner, pw);	//utf-16
//		tag_chinese_english(scanner, pw);	//utf-16
//		ner(scanner, pw);	//utf-16
//		eng(scanner, pw);	//utf-8
//		eng_oov(scanner, pw, scanner_t, pw_t);	//utf-8
		
//		tag_chinese_english(scanner, pw, pw2, scanner_t, pw_t, pw_t2); //utf-16
//		real_ce(scanner, pw); //utf-8  处理 MixedTextOrigin.txt
		
		scanner.close();
		pw.close();
		
		if(args.length == 6) {
			pw2.close();
			scanner_t.close();
			pw_t.close();
			pw_t2.close();
		} else if(args.length == 4) {
			scanner_t.close();
			pw_t.close();
		}
	}
}