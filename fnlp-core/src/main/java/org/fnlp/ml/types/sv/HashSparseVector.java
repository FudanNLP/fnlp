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

package org.fnlp.ml.types.sv;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

import java.io.Serializable;
import java.util.ArrayList;

public class HashSparseVector implements ISparseVector {

	private static final float DefaultValue = 1.0f;

	private static final long serialVersionUID = 2797070318099414849L;

	public TIntFloatHashMap data = new TIntFloatHashMap();

	public HashSparseVector(float[] w){
		for (int i = 0; i < w.length; i++) {
			if (Math.abs((w[i]-0f))>Float.MIN_VALUE) {
				put(i, w[i]);
			}
		}
	}
	/**
	 * 
	 * @param w
	 * @param b 加入一个额外常数项
	 */
	public HashSparseVector(float[] w, boolean b) {
		for (int i = 0; i < w.length; i++) {
			if (Math.abs((w[i]-0f))>Float.MIN_VALUE) {
				put(i, w[i]);
			}
		}
		if(b)
			put(w.length,1.0f);
	}


	public HashSparseVector() {

	}

	public HashSparseVector(HashSparseVector v) {
		data = new TIntFloatHashMap(v.data);
	}

	public void minus(ISparseVector sv) {


	}

	@Override
	public void put(int i) {
		put(i,DefaultValue);
	}

	public void put(int id, float c) {
		data.adjustOrPutValue(id, c, c);
	}

	@Override
	public void put(int[] idx) {
		put(idx,DefaultValue);

	}

	public void put(int[] idx, float c) {
		for(int i=0;i<idx.length;i++){
			if(idx[i]!=-1)
				data.put(idx[i], c);
		}
	}


	/**
	 * v + sv
	 * @param sv
	 */
	public void plus(ISparseVector sv) {
		if(sv instanceof HashSparseVector){
			TIntFloatIterator it = ((HashSparseVector) sv).data.iterator();
			while(it.hasNext()){
				it.advance();
				data.adjustOrPutValue(it.key(), it.value(),it.value());
			}
		}else if(sv instanceof BinarySparseVector){
			TIntIterator it = ((BinarySparseVector) sv).data.iterator();
			while(it.hasNext()){
				int i = it.next();
				data.adjustOrPutValue(i,DefaultValue,DefaultValue);
			}
		}

	}

	/**
	 * v + sv*w
	 * @param sv
	 * @param w
	 */
	public void plus(ISparseVector sv, float w) {
		if(sv instanceof HashSparseVector){
			TIntFloatIterator it = ((HashSparseVector) sv).data.iterator();
			while(it.hasNext()){
				it.advance();
				float v = it.value()*w;
				data.adjustOrPutValue(it.key(), v,v);
			}
		}else if(sv instanceof BinarySparseVector){
			TIntIterator it = ((BinarySparseVector) sv).data.iterator();
			while(it.hasNext()){
				int i = it.next();
				data.adjustOrPutValue(i,w,w);
			}
		}
	}



	public int[] indices() {
		return data.keys();
	}

	/**
	 * cos cos(v,sv)
	 * @param sv
	 * @return
	 */
	public float cos(HashSparseVector sv) {
		float v =0f;
		if(sv.size() < data.size()){
			TIntFloatIterator it = sv.data.iterator();			
			while(it.hasNext()){
				it.advance();
				v += data.get(it.key())*it.value();
			}
		}else{
			TIntFloatIterator it = data.iterator();			
			while(it.hasNext()){
				it.advance();
				v += sv.data.get(it.key())*it.value();
			}
		}
		TIntFloatIterator it = sv.data.iterator();
		float sum=0.0f;
		while (it.hasNext()) {
			it.advance();
			if(it.key()==0)
				continue;
			sum+=it.value()*it.value();
		}
		v/=Math.abs(sum)<0.00001?1:Math.sqrt(sum);
		it = data.iterator();
		sum=0.0f;
		while (it.hasNext()) {
			it.advance();
			if(it.key()==0)
				continue;
			sum+=it.value()*it.value();
		}
		v/=Math.abs(sum)<0.00001?1:Math.sqrt(sum);
		return v;
	}

	/**
	 * 点积 v.*sv
	 * @param sv
	 * @return
	 */
	public float dotProduct(HashSparseVector sv) {
		float v =0f;
		if(sv.size() < data.size()){
			TIntFloatIterator it = sv.data.iterator();			
			while(it.hasNext()){
				it.advance();
				v += data.get(it.key())*it.value();
			}
		}else{
			TIntFloatIterator it = data.iterator();			
			while(it.hasNext()){
				it.advance();
				v += sv.data.get(it.key())*it.value();
			}
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see org.fnlp.ml.types.sv.ISparseVector#dotProduct(float[])
	 */
	@Override
	public float dotProduct(float[] vector) {
		float v =0f;
		TIntFloatIterator it = data.iterator();			
		while(it.hasNext()){
			it.advance();
			v += vector[it.key()]*it.value();
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see org.fnlp.ml.types.sv.ISparseVector#l2Norm2()
	 */
	public float l2Norm2() {
		TIntFloatIterator it = data.iterator();
		float norm = 0f;
		while(it.hasNext()){
			it.advance();
			norm += it.value()*it.value();
		}
		return norm;
	}




	public float get(int id) {
		return data.get(id);
	}




	public float remove(int id) {
		return data.remove(id);
	}


	public int size() {
		return data.size();
	}


	public boolean containsKey(int id) {
		return data.containsKey(id);
	}

	public void clear() {
		data.clear();		
	}

	/**
	 * 
	 * @param c
	 */
	public void scaleDivide(float c) {
		TIntFloatIterator it = data.iterator();
		while(it.hasNext()){
			it.advance();
			float v = it.value()/c;
			data.put(it.key(), v);
		}

	}
	/**
	 * 欧氏距离
	 * @param sv1
	 * @param sv2
	 * @return
	 */
	public static float distanceEuclidean(HashSparseVector sv1 ,HashSparseVector sv2) {
		float dist = 0.0f;
		TIntFloatIterator it1 = sv1.data.iterator();
		TIntFloatIterator it2 = sv2.data.iterator();
		int increa = 0;
		while(it1.hasNext()&&it2.hasNext()){
			if(increa==0){
				it1.advance();
				it2.advance();
			}else if(increa==1){
				it1.advance();
			}else if(increa==2){
				it2.advance();
			}
			if(it1.key()<it2.key()){
				dist += it1.value()*it1.value();
				increa = 1;
			}else if(it1.key()>it2.key()){
				dist += it2.value()*it2.value();
				increa = 2;
			}else{
				float t = it1.value() - it2.value();
				dist += t*t;
				increa = 0;
			}
		}
		while(it1.hasNext()){
			it1.advance();
			dist += it1.value()*it1.value();			
		}
		while(it2.hasNext()){
			it2.advance();
			dist += it2.value()*it2.value();

		}
		return dist;
	}

	/**
	 * 欧氏距离
	 * @param sv
	 * @return
	 */
	public float distanceEuclidean(HashSparseVector sv) {
		return distanceEuclidean(this,sv);
	}


	public String toString(){
		StringBuilder sb = new StringBuilder();
		TIntFloatIterator it = data.iterator();
		while(it.hasNext()){
			it.advance();
			sb.append(it.key());
			sb.append(":");
			sb.append(it.value());
			if(it.hasNext())
				sb.append(", ");
		}
		return sb.toString();
	}










}