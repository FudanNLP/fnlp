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

package org.fnlp.nlp.cn.rl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 百度搜索
 * 
 * @author xpqiu
 * @version 1.0
 * @since 1.0
 */
public class SearchByBaidu {


	static String URL_BAIDU = "http://www.baidu.com/s?ie=utf-8&wd=${wd}&rn=100";
	static String URL_GOOGLE = "http://cn.bing.com/search?q=${wd}";

	public static String regEx_script = "<script[^>]*?>[\\s\\S]*?</script>";     
	public static String regEx_style = "<style[^>]*?>[\\s\\S]*?</style>";    
	public static String regEx_html = "<[^>]+>"; 
	
	public static boolean isUseBaidu = true;

	
	public static Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);   
	public static Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);   
	public static Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE); 
	
	/**
	 * 得到返回网页个数
	 * 
	 * @param url
	 *            搜索的URL
	 * @return
	 */
	static String search(String word) {
		word = word.replaceAll(" ", "+");
		String u;
		if(isUseBaidu)
			u = URL_BAIDU.replace("${wd}", word);
		else
			u = URL_GOOGLE.replace("${wd}", word);
		URL url;
		try {
			url = new URL(u);
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		}

		InputStream istream = null;
		while (true) {
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				istream = conn.getInputStream();
				break;
			} catch (IOException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {

				}
			}
		}

		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					istream, "utf-8"));
			while (true) {
				String buf = reader.readLine();
				if (null == buf)
					break;
				sb.append(buf);				
			}
		} catch (IOException e) {

		}
		String htmlStr =sb.toString();
		//stripping script tags whether the tag contains "/n" or "/r" or not.   
		Matcher m_script = p_script.matcher(htmlStr);   
		htmlStr = m_script.replaceAll("");   

		//stripping style tags whether the tag contains "/n" or "/r" or not.   
		Matcher m_style = p_style.matcher(htmlStr);   
		htmlStr = m_style.replaceAll("");   

		//stripping html tags but continue to have the "/n" and "/r" in right place.   
		Matcher m_html = p_html.matcher(htmlStr);   
		htmlStr = m_html.replaceAll("");   
		
		htmlStr = htmlStr.replaceAll("(&amp;|&nbsp;|&#9654|&copy|◆|&#12288;|&gt;|&lt;)+", " ");
		if(isUseBaidu)
			htmlStr = htmlStr.replaceAll("(百度|_|搜索|\\.\\.\\.|-|2012|2011)", " ");
		htmlStr = htmlStr.replaceAll(String.valueOf(((char)12288))," ");
		htmlStr = htmlStr.replaceAll(" +", " ");
		
		return htmlStr;   

	}

	public static void main(String[] args) {

		String s =search("周杰伦");
		System.out.println(s);
	}

}