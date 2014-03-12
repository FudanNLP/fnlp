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

package org.fnlp.nlp.pipe.seq.templet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 序列标注特征模板组，包含不同的特征生成方式
 * @author xpqiu
 *
 */
public class TempletGroup extends ArrayList<Templet> {
	
	private static final long serialVersionUID = 115444082750769279L;
	
	/**
	 * 模板标识
	 */
	public int gid;
	/**
	 * n阶状态空间映射数组，元素为每一阶对应的一维空间起始地址
	 * 以标记个数为进制
	 */
	public int[] base;
	/**
	 * 最大阶数
	 */
	public int maxOrder;
	/**
	 * 状态组合个数
	 * numStates = numLabels^(maxOrder+1)
	 */
	public int numStates;
	/**
	 * 模板阶数
	 */
	int[] orders;	
	
	
	/**
	 * 不同模板对应状态组合的相对偏移位置
	 */
	public int[][] offset;
	
	public TempletGroup() {
		super();
		gid = 0;
	}
	
	/**
	 * 从文件中读取
	 * @param file
	 * @throws Exception 
	 */
	public void load(String file) throws Exception {

		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str;
			while((str=lbin.readLine())!=null){
				if(str.length()==0)
					continue;
				if(str.charAt(0)=='#')
					continue;
				add(new BaseTemplet(gid++, str));
			}
			lbin.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("读入模板错误");
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("读入模板错误");
		}
	}
	

	public void load_pro(String file) throws Exception {

		try {
			InputStreamReader  read = new InputStreamReader (new FileInputStream(file),"utf-8");
			BufferedReader lbin = new BufferedReader(read);
			String str;
			while((str=lbin.readLine())!=null){
				if(str.length()==0)
					continue;
				if(str.charAt(0)=='#')
					continue;
				add(new ProTemplet(gid++, str));
			}
			lbin.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new Exception("读入模板错误");
		}catch (Exception e) {
			e.printStackTrace();
			throw new Exception("读入模板错误");
		}
	}
	/**
	 * 计算偏移位置
	 * @param numLabels 标记个数
	 */
	public void calc(int numLabels){
		//计算最大阶
		int numTemplets = size();
		this.orders = new int[numTemplets];
		for(int j=0; j<numTemplets; j++) {
			Templet t = get(j);
			this.orders[j] = t.getOrder();
			if (orders[j] > maxOrder)
				maxOrder = orders[j];
		}
		
		base = new int[maxOrder+2];
		base[0]=1;
		for(int i=1; i<base.length; i++) {
			base[i]=base[i-1]*numLabels;
		}
		this.numStates = base[maxOrder+1];
		offset = new int[numTemplets][numStates];
		
		for(int t=0; t<numTemplets; t++) {
			Templet tpl =  this.get(t);
			int[] vars = tpl.getVars();
			/**
			 * 记录每一阶的状态
			 */
			int[] bits = new int[maxOrder+1];
			int v;
			for(int s=0; s<numStates; s++) {
				int d = s;
				//对于一个n阶状态组合，计算每个成员状态
				for(int i=0; i<maxOrder+1; i++) {
					bits[i] = d%numLabels;
					d = d/numLabels;
				}
				//对于一个n阶状态组合，记录一个特征模板映射到特征空间中到基地址的偏移
				//TODO 是否可以和上面合并简化
				v = 0;						
				for(int i=0; i<vars.length; i++) {
					v = v*numLabels + bits[-vars[i]];
				}
				offset[t][s] = v;
			}
		}
	}

	public int[] getOrders()	{
		orders = new int[this.size()];
		for(int i = 0; i < orders.length; i++)	{
			orders[i] = this.get(i).getOrder();
		}
		return orders;
	}

	public int[] getOrders(int o)	{
		int cnt = 0;
		for(int i = 0; i < this.size(); i++)	{
			if (get(i).getOrder() == o)	{
				cnt++;
			}
		}
		int[] ret = new int[cnt];
		for(int i = 0, j = 0; i < this.size(); i++)	{
			if (get(i).getOrder() == o)	{
				ret[j++] = i;
			}
		}
		return ret;
	}
}