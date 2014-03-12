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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * 大写数字转化模块功能说明by曹零
大写数字转化模块是将汉字表示的数字转化为阿拉伯数字的功能模块，具体说明如下：
1.该模块服务于时间表达式提取的预处理阶段，但在功能上完全独立于时间表达式提取，可支持复用。
2.该模块目前支持的正确转化范围是0-99999999的整形数。
3.该模块可将字符串中所有大写数字无差别全部转化，如：
"这里有一千两百个人，六百零五个来自中国"可以转化为"这里有1200个人，605个来自中国"。
4.该模块添加支持部分不规则表达的大写数字转化，如：
两万零六百五可转化为20650
两百一十四和两百十四都可以转化为214
一六零加一五八可以转化为160加158

 */

/**
 * 
 * @author xpqiu
 *
 */
public class TimeUnit
{
	/*
	 * 新版本中将根据上下文相关信息动态获得timeBase,故取消参数Time_Initial
	 * 及相关构造方法，添加参数normalizer。
	 * 
	 * modified by 曹零
	 */
	public String Time_Expression=null;
	public String Time_Norm="";
	public int[] time_full;
	public int[] time_origin;
	private Date time;
	/*
	public String Time_Initial=null; 
	modified by 曹零
	*/
	TimeNormalizer normalizer = null;
	public TimePoint _tp=new TimePoint();
	public TimePoint _tp_origin=new TimePoint();
	/**
	 * 时间表达式单元规范化的内部类
	 *
	 * 时间表达式单元规范化对应的内部类,
	 * 对应时间表达式规范化的每个字段，
	 * 六个字段分别是：年-月-日-时-分-秒，
	 * 每个字段初始化为-1
	 * 
	 * @author 邬桐 072021156
	 * @version 1.0 2009-02-12
	 *  
	*/
	public class TimePoint
	{
		int [] tunit={-1,-1,-1,-1,-1,-1};
	}
	/**
	 * 时间表达式单元构造方法
     * 该方法作为时间表达式单元的入口，将时间表达式字符串传入
     * @param exp_time 时间表达式字符串 
	 * @param n
	 */
	
	public TimeUnit(String exp_time, TimeNormalizer n)
	{
		Time_Expression=exp_time;
		normalizer = n;
		/*
		 * modified by 曹零
		SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss"); 
		Time_Initial  = tempDate.format(new java.util.Date());
		*/
		Time_Normalization();
	}
	
	/**
	 * return the accurate time object
	 */
    public Date getTime() {
        return time;
    }
    
	/**
     *年-规范化方法
     *
     *该方法识别时间表达式单元的年字段
     * 
     */
	public void norm_setyear()
	{
		String rule="[0-9]{2}(?=年)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[0]=Integer.parseInt(match.group());
			if(_tp.tunit[0] >= 0 && _tp.tunit[0] < 100){
				if(_tp.tunit[0]<30)
					_tp.tunit[0] += 2000;
				else
					_tp.tunit[0] += 1900;
			}
			
		}
		/*
		 * 不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份
		 * modified by 曹零
		 */
		rule="[0-9]?[0-9]{3}(?=年)";
		
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[0]=Integer.parseInt(match.group());
		}
	}
	/**
     *月-规范化方法
     *
     *该方法识别时间表达式单元的月字段
     * 
     */
	public void norm_setmonth()
	{
		String rule="((10)|(11)|(12)|([1-9]))(?=月)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[1]=Integer.parseInt(match.group());
		}	
	}
	/**
     *日-规范化方法
     *
     *该方法识别时间表达式单元的日字段
     * 
     */
	public void norm_setday()
	{
		String rule="((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号))";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[2]=Integer.parseInt(match.group());
		}	
	}
	/**
     *时-规范化方法
     *
     *该方法识别时间表达式单元的时字段
     * 
     */
	public void norm_sethour()
	{
		/*
		 * 清除只能识别11-99时的bug
		 * modified by 曹零
		 */
		String rule="(?<!(周|星期))([0-2]?[0-9])(?=(点|时))";
		
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[3]=Integer.parseInt(match.group());
		}	
		/*
		 * 对关键字：中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确时间计算
		 * 规约：
		 * 1.中午/午间0-10点视为12-22点
		 * 2.下午/午后0-11点视为12-23点
		 * 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点
		 * 4.0-11点pm/PM视为12-23点
		 * 
		 * add by 曹零
		 */
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
		}
		
		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
		}
		
		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if(_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
		}
	}
	/**
     *分-规范化方法
     *
     *该方法识别时间表达式单元的分字段
     * 
     */
	public void norm_setminute()
	{
		/*
		 * 添加了省略“分”说法的时间
		 * 如17点15
		 * modified by 曹零
		 */
		String rule="([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))";
		
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			if(match.group().equals("")){
				
			}
			else
			_tp.tunit[4]=Integer.parseInt(match.group());
		}
		/*
		 * 添加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）
		 * 
		 * add by 曹零
		 */
		rule = "(?<=[点时])[1一]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 15;
		}
		
		rule = "(?<=[点时])半";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 30;
		}
		
		rule = "(?<=[点时])[3三]刻(?!钟)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			_tp.tunit[4] = 45;
		}
		
	}
	/**
     *秒-规范化方法
     *
     *该方法识别时间表达式单元的秒字段
     * 
     */
	public void norm_setsecond()
	{
		/*
		 * 添加了省略“分”说法的时间
		 * 如17点15分32
		 * modified by 曹零
		 */
		String rule="([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])";
		
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			_tp.tunit[5]=Integer.parseInt(match.group());
		}	
	}
	/**
     *特殊形式的规范化方法
     *
     *该方法识别特殊形式的时间表达式单元的各个字段
     * 
     */
	public void norm_setTotal()
	{
		String rule;
		Pattern pattern;
		Matcher match;
		String[] tmp_parser;
		String tmp_target;
		
		/*
		 * 修改了函数中所有的匹配规则使之更为严格
		 * modified by 曹零
		 */
		rule="(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split(":");
			_tp.tunit[3]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[4]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[5]=Integer.parseInt(tmp_parser[2]);
		}
		/*
		 * 添加了省略秒的:固定形式的时间规则匹配
		 * add by 曹零
		 */
		else{
			rule="(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]";
			pattern=Pattern.compile(rule);
			match=pattern.matcher(Time_Expression);
			if(match.find())
			{
				tmp_parser=new String[2];
				tmp_target=match.group();
				tmp_parser=tmp_target.split(":");
				_tp.tunit[3]=Integer.parseInt(tmp_parser[0]);
				_tp.tunit[4]=Integer.parseInt(tmp_parser[1]);
			}
		}
		/*
		 * 增加了:固定形式时间表达式的
		 * 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM
		 * 的正确时间计算，规约同上
		 * add by 曹零
		 */
		rule = "(中午)|(午间)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
				_tp.tunit[3] += 12;
		}
		
		rule = "(下午)|(午后)|(pm)|(PM)";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
		}
		
		rule = "晚";
		pattern = Pattern.compile(rule);
		match = pattern.matcher(Time_Expression);
		if(match.find()){
			if(_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
				_tp.tunit[3] += 12;
			else if(_tp.tunit[3] == 12)
				_tp.tunit[3] = 0;
		}
		
		
		rule="[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("-");
			_tp.tunit[0]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[2]);
		}
		
		rule="((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("/");
			_tp.tunit[1]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[0]=Integer.parseInt(tmp_parser[2]);
		}
		
		/*
		 * 增加了:固定形式时间表达式 年.月.日 的正确识别
		 * add by 曹零
		 */
		rule="[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			tmp_parser=new String[3];
			tmp_target=match.group();
			tmp_parser=tmp_target.split("\\.");
			_tp.tunit[0]=Integer.parseInt(tmp_parser[0]);
			_tp.tunit[1]=Integer.parseInt(tmp_parser[1]);
			_tp.tunit[2]=Integer.parseInt(tmp_parser[2]);
		}
	}
	
	/**
	 * 设置以上文时间为基准的时间偏移计算
	 * 
	 * add by 曹零
	 */
	public void norm_setBaseRelated(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		int[] ini = new int[6];
		for(int i = 0 ; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1]-1, ini[2], ini[3], ini[4], ini[5]);
		calendar.getTime();
		
		boolean[] flag = {false,false,false};//观察时间表达式是否因当前相关时间表达式而改变时间
		

		String rule="\\d+(?=天[以之]?前)";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, -day);
		}
		
		rule="\\d+(?=天[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int day = Integer.parseInt(match.group());
			calendar.add(Calendar.DATE, day);
		}
		
		rule="\\d+(?=(个)?月[以之]?前)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, -month);
		}
		
		rule="\\d+(?=(个)?月[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			int month = Integer.parseInt(match.group());
			calendar.add(Calendar.MONTH, month);
		}
		
		rule="\\d+(?=年[以之]?前)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, -year);
		}
		
		rule="\\d+(?=年[以之]?后)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			int year = Integer.parseInt(match.group());
			calendar.add(Calendar.YEAR, year);
		}
		
		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if(flag[0]||flag[1]||flag[2]){
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
		}
		if(flag[1]||flag[2])
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		if(flag[2])
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
	}
	
	/**
	 * 设置当前时间相关的时间表达式
	 * 
	 * add by 曹零
	 */
	public void norm_setCurRelated(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getOldTimeBase().split("-");
		int[] ini = new int[6];
		for(int i = 0 ; i < 6; i++)
			ini[i] = Integer.parseInt(time_grid[i]);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(ini[0], ini[1]-1, ini[2], ini[3], ini[4], ini[5]);
		calendar.getTime();
		
		boolean[] flag = {false,false,false};//观察时间表达式是否因当前相关时间表达式而改变时间
		
		String rule="前年";
		Pattern pattern=Pattern.compile(rule);
		Matcher match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, -2);
		}
		
		rule="去年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, -1);
		}
		
		rule="今年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 0);
		}
		
		rule="明年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 1);
		}	
		
		rule="后年";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[0] = true;
			calendar.add(Calendar.YEAR, 2);
		}	
		
		rule="上(个)?月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, -1);
			
		}
		
		rule="(本|这个)月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, 0);
		}
		
		rule="下(个)?月";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[1] = true;
			calendar.add(Calendar.MONTH, 1);
		}
		
		rule="大前天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -3);
		}
		
		rule="(?<!大)前天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -2);
		}
		
		rule="昨";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, -1);
		}
		
		rule="今(?!年)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 0);
		}
		
		rule="明(?!年)";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 1);
		}
		
		rule="(?<!大)后天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 2);
		}
		
		rule="大后天";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			calendar.add(Calendar.DATE, 3);
		}
		
		rule="(?<=(上上(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		
		rule="(?<=((?<!上)上(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, -1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		
		rule="(?<=((?<!下)下(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 1);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		
		rule="(?<=(下下(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 2);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		
		rule="(?<=((?<!(上|下))(周|星期)))[1-7]";
		pattern=Pattern.compile(rule);
		match=pattern.matcher(Time_Expression);
		if(match.find())
		{
			flag[2] = true;
			int week = Integer.parseInt(match.group());
			if(week == 7)
				week = 1;
			else 
				week++;
			calendar.add(Calendar.WEEK_OF_MONTH, 0);
			calendar.set(Calendar.DAY_OF_WEEK, week);
		}
		
		String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
		String[] time_fin = s.split("-");
		if(flag[0]||flag[1]||flag[2]){
			_tp.tunit[0] = Integer.parseInt(time_fin[0]);
		}
		if(flag[1]||flag[2])
			_tp.tunit[1] = Integer.parseInt(time_fin[1]);
		if(flag[2])
			_tp.tunit[2] = Integer.parseInt(time_fin[2]);
			
	}

	/**
	 * 该方法用于更新timeBase使之具有上下文关联性
	 */
	public void modifyTimeBase(){
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		
		String s = "";
		if(_tp.tunit[0] != -1)
			s += Integer.toString(_tp.tunit[0]);
		else
			s += time_grid[0];
		for(int i = 1; i < 6; i++){
			s += "-";
			if(_tp.tunit[i] != -1)
				s += Integer.toString(_tp.tunit[i]);
			else
				s += time_grid[i];
		}
		normalizer.setTimeBase(s);
	}
	/**
     *时间表达式规范化的入口
     *
     *时间表达式识别后，通过此入口进入规范化阶段，
     *具体识别每个字段的值
     * 
     */
	public void Time_Normalization()
	{
		norm_setyear();
		norm_setmonth();
		norm_setday();
		norm_sethour();
		norm_setminute();
		norm_setsecond();
		norm_setTotal();
		norm_setBaseRelated();
		norm_setCurRelated();
		modifyTimeBase();
		
		_tp_origin.tunit = _tp.tunit.clone();
		
		String [] time_grid=new String[6];
		time_grid=normalizer.getTimeBase().split("-");
		
		int tunitpointer=5;
		while (tunitpointer>=0 && _tp.tunit[tunitpointer]<0)
		{
			tunitpointer--;
		}
		for (int i=0;i<tunitpointer;i++)
		{
			if (_tp.tunit[i]<0)
				_tp.tunit[i]=Integer.parseInt(time_grid[i]);
		}
		String[] _result_tmp=new String[6];
		_result_tmp[0]=String.valueOf(_tp.tunit[0]);
		if (_tp.tunit[0]>=10 &&_tp.tunit[0]<100)
		{
			_result_tmp[0]="19"+String.valueOf(_tp.tunit[0]);
		}
		if (_tp.tunit[0]>0 &&_tp.tunit[0]<10)
		{
			_result_tmp[0]="200"+String.valueOf(_tp.tunit[0]);
		}
		
		for (int i = 1; i < 6; i++) {
            _result_tmp[i] = String.valueOf(_tp.tunit[i]);
        }

        Calendar cale = Calendar.getInstance();			//leverage a calendar object to figure out the final time
        cale.clear();
        if (Integer.parseInt(_result_tmp[0]) != -1) {
            Time_Norm += _result_tmp[0] + "年";
            cale.set(Calendar.YEAR, Integer.valueOf(_result_tmp[0]));
            if (Integer.parseInt(_result_tmp[1]) != -1) {
                Time_Norm += _result_tmp[1] + "月";
                cale.set(Calendar.MONTH, Integer.valueOf(_result_tmp[1]) - 1);
                if (Integer.parseInt(_result_tmp[2]) != -1) {
                    Time_Norm += _result_tmp[2] + "日";
                    cale.set(Calendar.DAY_OF_MONTH, Integer.valueOf(_result_tmp[2]));
                    if (Integer.parseInt(_result_tmp[3]) != -1) {
                        Time_Norm += _result_tmp[3] + "时";
                        cale.set(Calendar.HOUR_OF_DAY, Integer.valueOf(_result_tmp[3]));
                        if (Integer.parseInt(_result_tmp[4]) != -1) {
                            Time_Norm += _result_tmp[4] + "分";
                            cale.set(Calendar.MINUTE, Integer.valueOf(_result_tmp[4]));
                            if (Integer.parseInt(_result_tmp[5]) != -1) {
                                Time_Norm += _result_tmp[5] + "秒";
                                cale.set(Calendar.SECOND, Integer.valueOf(_result_tmp[5]));
                            }
                        }
                    }
                }
            }
        }
        time = cale.getTime();
		
		time_full = _tp.tunit.clone();
		time_origin = _tp_origin.tunit.clone();
	}
	
	public String toString(){
		return Time_Expression+" ---> "+ Time_Norm;
	}
}