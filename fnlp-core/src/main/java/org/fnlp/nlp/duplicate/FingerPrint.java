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

package org.fnlp.nlp.duplicate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.nlp.pipe.NGram;
/**
 * 创建指纹
 * @author xpqiu
 *
 */
public class FingerPrint {
	
	public static enum Type {Print,Char,NGram,WhiteSpace};
		
	public static void main(String[] args) {
		System.out.println(print("心  把那些挫折好好分析下  让自己的心态能更好  以后遇到事情也不会这么难受  别自己和自己过不去  那叫没事找事儿…  快睡吧"));
		System.out.println(print("明天我问他。你们早点睡觉。明天场地呢"));//这种情况fa提取不出
		System.out.println(print("自己的心态"));
	}
	
	
	private static Pattern p = Pattern.compile(".(\\pP|\\pS|　| |\\s|的|把|和|也)+.");
	
	public static TreeSet<String> print(String s) {
		Matcher matcher = p.matcher(s);
		TreeSet<String> set = new TreeSet<String>();
		while(matcher.find()) {
			String m = matcher.group();
			set.add(m);
		}
		return set;
	}

	public static String ngram(String ss,int n) {
		List l = NGram.ngramOnCharacter2List(ss, new int[]{n});
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<l.size();i++){
			sb.append(l.get(i));
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public static Set<String> ngramSet(String ss,int n) {
		return NGram.ngramOnCharacter2Set(ss, new int[]{n});		
	}

	public static String feature(String s, Type t) {
		if(t==Type.WhiteSpace){
			return whitespace(s);
		}else if(t==Type.NGram){
			return ngram(s,2);
		}else if(t==Type.Char){
			return ngram(s,1);
		}
		return null;
	}

	private static String whitespace(String s) {
			
		return s;
	}

	public static Set<String> featureset(String s, Type t) {
		if(t==Type.WhiteSpace){
			return whitespaceSet(s);
		}else if(t==Type.NGram){
			return ngramSet(s,2);
		}else if(t==Type.Char){
			return ngramSet(s,1);
		}
		return null;
	}

	private static Set<String> whitespaceSet(String s) {
		String[] toks = s.split("\\s+");
		Set<String> set = new HashSet<String>();
		for(int i=0;i<toks.length;i++)
			set.add(toks[i]);
		return set;
		
	}
}