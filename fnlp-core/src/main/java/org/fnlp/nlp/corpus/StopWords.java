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

package org.fnlp.nlp.corpus;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.lang.String;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 本类主要功能是过滤停用词
 * @author ltian
 *
 */

public class StopWords {

	TreeSet<String> sWord = new TreeSet<String>();
	String dicPath;
	HashMap<String, Long> lastModTime = new HashMap<String, Long>();

	public StopWords(){	
	}
	public StopWords(String dicPath1,boolean b){
		this.dicPath = dicPath1;
		// 定期监视文件改动
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				read(dicPath);
			}


		}, new Date(System.currentTimeMillis() + 10000), 24*60*60*1000);
	}
	/**
	 * 构造函数
	 * @param dicPath
	 *        stopword所在地址
	 */

	public StopWords(String dicPath) {		
		this.dicPath = dicPath;
		read(dicPath);		
	}

	/**
	 * 读取stopword
	 * @param dicPath
	 *       stopword所在地址
	 * @throws FileNotFoundException
	 */

	public void read(String dicPath) {

		File path = new File(dicPath);
		if(path.isDirectory()){
			String[] subdir = path.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if(name.toLowerCase().endsWith("txt"))
						return true;
					else
						return false;
				}
			});
			for(int i=0;i<subdir.length;i++){
				read(path+"/"+subdir[i]);
			}
			return;
		}
		Long newTime = path.lastModified();
		Long lastTime = lastModTime.get(dicPath);
		if(lastTime ==null || !lastTime.equals(newTime)){
			//路径是文件
			try{
				InputStreamReader read = new InputStreamReader(new FileInputStream(path), "UTF-8");
				BufferedReader in = new BufferedReader(read);
				String s;
				while ((s = in.readLine()) != null){ 
					s = s.trim();
					if (!s.matches("^$"))
						sWord.add(s);
				}
				in.close();
			}catch (Exception e) {
				System.err.println("停用词文件路径错误");
			}
		}
	}

	/**
	 * 删除stopword
	 * 将string字符串转换为List类型，并返回
	 * @param words
	 *       要进行处理的字符串 
	 * @return
	 *       删除stopword后的List类型
	 */

	public List<String> phraseDel(String[] words){
		List<String> list = new ArrayList<String>(); 
		String s;
		int length= words.length;
		for(int i = 0; i < length; i++){
			s = words[i];
			if(!isStopWord(s))
				list.add(s);
		}
		return list;
	}

	Pattern noise = Pattern.compile(".*["+CharSets.allRegexPunc+"\\d]+.*");
	
	/**
	 * 判断是否为停用词
	 * @param word
	 * @param minLen 最小长度
	 * @param maxLen 最大长度
	 * @return
	 */
	public boolean isStopWord(String word,int minLen, int maxLen) {
		if (word.length() < minLen || word.length()>maxLen)
			return true;

		if (noise.matcher(word).matches())
			return true;

		if (sWord.contains(word))
			return true;

		return false;
	}
	
	/**
	 * 判断是否为停用词
	 * @param word
	 * @return
	 */
	public boolean isStopWord(String word) {		
		
		if (sWord.contains(word))
			return true;

		return false;
	}
}