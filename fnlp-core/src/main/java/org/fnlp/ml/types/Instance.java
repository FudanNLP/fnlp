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

package org.fnlp.ml.types;

import java.io.Serializable;

/**
 * 表示单个样本(x,y)。 x,y分别对应data,target.
 * 
 * @author xpqiu
 * 
 */
public class Instance implements Serializable{
	private static final long serialVersionUID = 4292036045536957058L;
	/**
	 * 样本值，相当于x
	 */
	protected Object data;
	/**
	 * 标签或类别，相当于y
	 */
	protected Object target;
	/**
	 * 数据来源等需要记录的信息
	 */
	protected Object clause;
	/**
	 * 保存数据的最原始版本
	 */
	private Object source;
	/**
	 * 临时数据，用来传递一些临时变量
	 */
	private Object tempData;
	/**
	 * 临时数据被占有标志
	 */
	private boolean tempDataLock = false;
	/**
	 * 字典数据
	 */
	private Object dicData;

	/**
	 * 样本权重
	 */
	private float weight=1;

	public Instance() {
	}

	public Instance(Object data) {
		this.data = data;
	}

	public Instance(Object data, Object target) {
		this.data = data;
		this.source = data;
		this.target = target;
	}

	public Instance(Object data, Object target, Object clause) {
		this.data = data;
		this.source = data;
		this.target = target;
		this.clause = clause;
	}

	public Object getTarget() {
		// 注释掉下面2行，可能会引起别的问题
		// if (target == null)
		// return data;

		return this.target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setClasue(String s) {
		this.clause = s;

	}

	public String getClasue() {
		return (String) this.clause;

	}

	public Object getSource() {
		return this.source;
	}

	public void setSource(Object source) {
		this.source = source;
	}
	/**
	 * 设置临时数据
	 * @param tempData
	 */
	public void setTempData(Object tempData) {
		if(tempDataLock){
			System.out.println("警告：临时数据已被占用");
		}
		this.tempData = tempData;
		tempDataLock = true;
	}
	/**
	 * 得到临时数据
	 * @return
	 */
	public Object getTempData() {
		if(!tempDataLock){
			System.out.println("临时数据没有被占用");
			return null;
		}
		return tempData;
	}
	/**
	 * 删除临时数据
	 */
	public void deleteTempData() {
		if(!tempDataLock){
			System.out.println("临时数据没有被占用");
			return;
		}
		tempData = null;
		tempDataLock = false;
	}

	/**
	 * 得到数据长度
	 * @return
	 */
	public int length() {
		int ret = 0;
		if (data instanceof int[])
			ret = 1;
		else if (data instanceof int[][])
			ret = ((int[][]) data).length;
		else if (data instanceof int[][][]) {
			ret = ((int[][][]) data)[0].length;
		}
		return ret;
	}

	public Object getDicData() {
		return dicData;
	}

	public void setDicData(Object dicData) {
		this.dicData = dicData;
	}

	/**
	 * 得到样本权重
	 * @return
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * 设置权重
	 * @param weight
	 */
	public void setWeight(float weight) {
		this.weight = weight;
		if(weight==0f){
			System.out.println("weight zero");
		}
	}

	public String toString(){
		StringBuilder sb= new StringBuilder();

		sb.append(data.toString());

		return sb.toString();

	}
}