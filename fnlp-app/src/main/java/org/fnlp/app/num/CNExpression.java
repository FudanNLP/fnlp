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

package org.fnlp.app.num;

class Loc{
	Loc(){}
	public int v=0;
}
/**
 * 中文表达式类 中文表达式的各项处理
 * @author wsy
 *
 */
public class CNExpression {
	private static String strDigit=new String("一幺二两三叁四肆五伍六七八九零");
	private static String strDigitOut=new String("零一二三四五六七八九");
	private static String strUnit=new String("空十百千万亿");
	private static String strOp=new String("加减乘除以上掉去");
	private static String strSuff=new String("得等于的是答案多少几");
	private static String strPunctuation=new String("?？");
	private int num1,num2,op,ans;//表达式中的两项数字 符号 及结果，其中符号位加0，减1，乘2，除以3，除4
	public CNExpression(){
		num1=num2=op-1;
	}
	public int getAns(){
		return this.ans;
	}
	/**
	 * 获取中文答案
	 * @return 中文表达的计算结果
	 */
	public String getAnswerInChn(){
		if(this.ans<0){
			return new String("识别错误，请检查输入格式");
		}
		else {
			return this.num2Chn(this.ans);
		}
	}
	/**
	 * 输入中文表达式
	 * @param str 中文表达式
	 * @return 中文表达式是否可被识别的结果
	 */
	public boolean setExpr(String str){
		Loc loc=new Loc();
		loc.v=0;
		int num1,num2,op;
		num1=chn2Num(str,loc);
		if(num1<0)
			return false;
		op=chn2Op(str,loc);
		if(op<0)
			return false;
		num2=chn2Num(str,loc);
		if(num2<0)
			return false;
		if(!this.checkSuffix(str, loc))
			return false;
		this.num1=num1;
		this.num2=num2;
		this.op=op;
		return true;
	}
	/**
	 * 将单个中文字符转换为其所表示的数值
	 * @param chr 单个中文字符
	 * @return 其所表示的数值，若不能识别则返回-1；
	 */
	private int chn2Num(int chr){
		int temp;
		temp=this.strDigit.indexOf(chr);
		if(temp<0)
			return -1;
		if(temp<10)
			return temp/2+1;
		else if(temp<14)
			return temp-4;
		else return 0;
	}
	/**
	 * 将中文表达式字符串转换为其所表示的数值
	 * @param str 中文表达式字符串
	 * @return 其所表示的数值，如果字符串不被接受返回-1；
	 */
	public int chn2Num(String str){
		Loc loc=new Loc();
		loc.v=0;
		int w=chn2Num(str,loc);
		if(loc.v==str.length())
			return w;
		else return -1;
	}
	/**
	 * 将一个字符串指定位置开始的中文标示的数字转换为其所表示的数值
	 * @param str 需要识别的字符串
	 * @param loc 指定的位置
	 * @return 识别出的数值，若不能识别返回-1
	 */
	public int chn2Num(String str,Loc loc){
		int tempNum;
		int num=0,init=loc.v,init2=loc.v;
		int flag=63;//"111111"亿万千百十
		int len=str.length();
		int chr;
		while(init<len){
			//十的情况比较特殊，故单独判断。如十三  不必如二十三一般有前置数字；
			if((flag&2)>0 && str.charAt(init)=='十'){//flag>0即允许出现十的状态
				flag&=63-14;//出现十之后下个单位不可能是千百十
				tempNum=10;
				init++;
			}
			else{			
				//不接受数字则结束
				if((flag&1)==0){
					loc.v=init;
					break;
				}
				flag-=1;//置末位0
				//判断数字
				tempNum=chn2Num(str.charAt(init));
				if(tempNum<0){
					loc.v=init;
					break;//不能识别，结束
				}
				init++;
				if(tempNum==0){
					flag|=1;
					continue;
				}
				//判断单位字符是否存在
				if(init>=len){
					loc.v=init;
					num+=tempNum;
					break;
				}
				chr=str.charAt(init);
				//判断十
				if((flag&2)>0 && chr=='十'){
					flag&=63-14;//出现十之后下个单位不可能是千百十
					flag|=1;//可以接受个位
					tempNum*=10;
					init++;
				}
				//判断百
				else if((flag&4)>0 && chr=='百'){
					flag&=63-12;//出现百之后下个单位不可能是千百
					flag|=1;//可以接受个位
					tempNum*=100;
					init++;
				}
				//判断千
				else if((flag&8)>0 && chr=='千'){
					flag&=63-8;//出现百之后下个单位不可能是千
					flag|=1;//可以接受个位
					tempNum*=1000;
					init++;
				}
			}

			num+=tempNum;
			//判断单位字符是否存在 并识别万
			if(init<len){
				chr=str.charAt(init);
				if((flag&16)>0 && chr=='万'){
					flag=15;//万之后不再出现万，也不会有亿
					num*=10000;
					init++;
					loc.v=init;
					int numNext=this.chn2Num(str, loc);
					if(numNext>0)
						num+=numNext;
					break;
				}
				else if((flag&32)>0 && chr=='亿'){
					flag=31;//亿之后不再出现亿
					num*=100000000;
					init++;
					loc.v=init;
					int numNext=this.chn2Num(str, loc);
					if(numNext>0)
						num+=numNext;
					break;
				}
			}
			continue;
		}
		if(init2!=init)
			return num;
		
		else 
			return -1;
	}
	/**
	 * 讲一个字符串指定位置开始的中文标示的算式符号转换为预定的算符的标号
	 * @param str 输入字符串
	 * @param loc 指定的位置
	 * @return 算符的标号
	 */
	private int chn2Op(String str,Loc loc){
		int chr,op,len;
		len=str.length();
		if(loc.v>=len)
			return -1;
		chr=str.charAt(loc.v);
		op=this.strOp.indexOf(chr);
		if(op<0)
			return -1;
		if(op<4){
			loc.v++;
			if(loc.v>=len){
				if(op==3)
					op=4;
				return op;
			}
			chr=str.charAt(loc.v);
			if(this.strOp.indexOf(chr)>3){
				loc.v++;
			}
			else{
				if(op==3)
					op=4;
			}
		}
		return op;
	}
	/**
	 * 检查一个字符串从指定位置开始的中文标示是否符号一个算术表达式的后缀询问部分
	 * @param str 字符串
	 * @param loc 指定位置
	 * @return 符合则返回true 不符合返回false
	 */
	private boolean checkSuffix(String str,Loc loc){
		int len=str.length();
		while(loc.v<len && this.strSuff.indexOf(str.charAt(loc.v))>=0)
			loc.v++;
		while(loc.v<len && this.strPunctuation.indexOf(str.charAt(loc.v))>=0)
			loc.v++;
		if(loc.v<len)
			return false;
		else 
			return true;
	}
	/**
	 * 计算表达式结果
	 */
	public int calculate(){
		if(op<0|num1<0|num2<0)
			return -1;
		switch(op){
			case 0:
				ans=num1+num2;
				break;
			case 1:
				ans=num1-num2;
				break;
			case 2:
				ans=num1*num2;
				break;
			case 3:
				ans=num1/num2;
				break;
			case 4:
				ans=num2/num1;
				break;
			default:
				return -1;
		}
		return 0;
	}
	/**
	 * 将数字转换为中文表示
	 * @param num 数字
	 * @return 数字的中文表示
	 */
	public String num2Chn(int num){
		char s[]=new char[32],ch;
		int counter1,counter2;
		int chr,preChr=-1;
		int len=0;
		boolean suffixZero=true;
		counter1=0;
		counter2=0;
		while(num>0){
			chr=num%10;
			if(chr!=0){
				suffixZero=false;
				if(counter1>0){
					s[len]=this.strUnit.charAt(counter1);
					len++;
				}
				if(num/10!=0 || counter1!=1 || chr!=1){
					s[len]=this.strDigitOut.charAt(chr);
					len++;
				}
			}
			else{
				if(!suffixZero)
					if(preChr!=0){
						s[len]=this.strDigitOut.charAt(chr);
						len++;
					}
			}
			num=num/10;
			preChr=chr;
			counter1++;
			if(counter1==4){
				counter1=0;
				counter2++;
				suffixZero=true;
				if(num%10000>0){
					s[len]=this.strUnit.charAt(3+counter2);
					len++;
				}
			}
		}
		for(int i=0;i<len/2;i++){
			ch=s[i];
			s[i]=s[len-i-1];
			s[len-i-1]=ch;
		}
		if(len==0){
			s[len]=this.strDigitOut.charAt(0);
			len++;
		}
		return new String(s,0,len);
	}
	
}