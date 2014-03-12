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

package org.fnlp.nlp.cn.ner;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * 新版时间表达式识别的主要工作类，改进了timebase的工作方式以提高识别准确率，
 * 并支持获得推测后时间和推测前时间两种时间信息
 * 
 * @author 邬桐,曹零
 * @version 1.1 2010-4-28
 * @since FudanNLP 1.0
 */
public class TimeNormalizer   implements Serializable {

	private static final long serialVersionUID = 463541045644656392L;
	private String timeBase;
	private String oldTimeBase;
	private static Pattern patterns = null;
	private String target;
	private TimeUnit[] timeToken = new TimeUnit[0];

	public TimeNormalizer() {
	}

	public TimeNormalizer(String path){
		if(patterns == null){
			try {
				patterns = readModel(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.print("Read model error!");
			}
		}
	}
	/**
	 * TimeNormalizer的构造方法，根据提供的待分析字符串和timeBase进行时间表达式提取
	 * 在构造方法中已完成对待分析字符串的表达式提取工作
	 * 
	 * @param target 待分析字符串
	 * @param timeBase 给定的timeBase
	 * @return 返回值
	 */
	public TimeUnit[] parse(String target,String timeBase){
		this.target = target;
		this.timeBase = timeBase;
		this.oldTimeBase = timeBase;
		//字符串预处理
		preHandling();
		timeToken = TimeEx(this.target,timeBase);
		return timeToken;
	}

	/**
	 * 同上的TimeNormalizer的构造方法，timeBase取默认的系统当前时间
	 * 
	 * @param target 待分析字符串
	 * @return 时间单元数组
	 */
	public TimeUnit[] parse(String target){
		this.target = target;
		this.timeBase = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
		this.oldTimeBase = timeBase;
		preHandling();//字符串预处理
		timeToken = TimeEx(this.target,timeBase);
		return timeToken;
	}

	//


	/**
	 * timeBase的get方法
	 * 
	 * @return 返回值
	 */
	public String getTimeBase(){
		return timeBase;
	}

	/**
	 * oldTimeBase的get方法
	 * 
	 * @return 返回值
	 */
	public String getOldTimeBase(){
		return oldTimeBase;
	}

	/**
	 * timeBase的set方法
	 * 
	 * @param s timeBase
	 */
	public void setTimeBase(String s){
		timeBase = s;
	}

	/**
	 * 重置timeBase为oldTimeBase
	 * 
	 */
	public void resetTimeBase(){
		timeBase = oldTimeBase;
	}

	/**
	 * 时间分析结果以TimeUnit组的形式出现，此方法为分析结果的get方法 
	 * 
	 * @return 返回值
	 */
	public TimeUnit[] getTimeUnit(){
		return timeToken;
	}



	/**
	 * 待匹配字符串的清理空白符和语气助词以及大写数字转化的预处理
	 */
	private void preHandling(){
		target = stringPreHandlingModule.delKeyword(target, "\\s+"); //清理空白符
		target = stringPreHandlingModule.delKeyword(target, "[的]+"); //清理语气助词
		target = stringPreHandlingModule.numberTranslator(target);//大写数字转化
	}

	/**
	 *有基准时间输入的时间表达式识别
	 *
	 *这是时间表达式识别的主方法，
	 *通过已经构建的正则表达式对字符串进行识别，并按照预先定义的基准时间进行规范化
	 *将所有别识别并进行规范化的时间表达式进行返回，
	 *时间表达式通过TimeUnit类进行定义
	 *
	 *
	 * @param String 输入文本字符串
	 * @param String 输入基准时间
	 * @return TimeUnit[] 时间表达式类型数组
	 * 
	 */
	private TimeUnit[] TimeEx(String tar,String timebase)
	{
		Matcher match;
		int startline=-1,endline=-1;

		String [] temp = new String[99];
		int rpointer=0;
		TimeUnit[] Time_Result = null;

		match=patterns.matcher(tar);	
		boolean startmark=true;
		while(match.find())
		{
			startline=match.start();
			if (endline==startline) 
			{
				rpointer--;
				temp[rpointer]=temp[rpointer]+match.group();
			}
			else
			{
				if(!startmark)
				{
					rpointer--;
					//System.out.println(temp[rpointer]);
					rpointer++;	
				}	
				startmark=false;
				temp[rpointer]=match.group();
			}
			endline=match.end();
			rpointer++;
		}
		if(rpointer>0)
		{
			rpointer--;
			//System.out.println(temp[rpointer]);
			rpointer++;
		}
		Time_Result=new TimeUnit[rpointer];
		//	System.out.println("Basic Data is " + timebase); 
		for(int j=0;j<rpointer;j++)
		{
			Time_Result[j]=new TimeUnit(temp[j],this);
			//System.out.println(result[j]);
		}

		return Time_Result;
	}

	

	private Pattern readModel(InputStream is) throws Exception{
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (is)));
		return readModel(in);
	}
	private Pattern readModel(String file) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream 
				(new GZIPInputStream (new FileInputStream(file))));
		return readModel(in);
	}

	private Pattern readModel(ObjectInputStream in) throws Exception {

		Pattern p = (Pattern) in.readObject();
		//System.out.print(p.pattern());
		return p=Pattern.compile(p.pattern());
	}
	

}