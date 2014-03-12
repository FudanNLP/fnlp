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

package org.fnlp.ml.nmf;

import java.util.Vector;

import org.fnlp.ml.types.sv.SparseMatrix;

import gnu.trove.iterator.TLongFloatIterator;


public class Nmf {
	int max_iter;
	float lambda;
	int m, n, r;
	float eps = 1e-10f;

	SparseMatrix v;
	SparseMatrix w;
	SparseMatrix h;

	public Nmf(int max_iter, float lambda, int r, SparseMatrix array) {
		this.max_iter = max_iter;
		this.lambda = lambda;
		this.r = r;
		m = array.size()[0];
		n = array.size()[1];
		v = array;
		int[] wdim = { m, r };
		int[] hdim = { r, n };
		w = SparseMatrix.random(wdim);
		h = SparseMatrix.random(hdim);
	}

	/**
	 * v与w*h对位相减计算误差
	 * 
	 * @param v
	 * @param w
	 * @param h
	 * @return 误差
	 */
	float computeObjective(SparseMatrix v, SparseMatrix w, SparseMatrix h) {
		SparseMatrix matrixWH = w.mutiplyMatrix(h);
		SparseMatrix diff = v.clone();
		diff.minus(matrixWH);
		return diff.l2Norm();

	}

	SparseMatrix updateH() {

		int[] dimWH = { m, n };
		int[] dimVWH = { m, n };
		int[] dimHWVWH = { r, n };

		SparseMatrix matrixWH = new SparseMatrix(dimWH);
		SparseMatrix matrixVWH = new SparseMatrix(dimVWH);
		SparseMatrix matrixHWVWH = new SparseMatrix(dimHWVWH);
		matrixWH = w.mutiplyMatrix(h);

		 TLongFloatIterator itV = v.vector.iterator();

		 TLongFloatIterator itH = h.vector.iterator();
		for (int i = v.vector.size(); i-- > 0;) {
			itV.advance();
			matrixVWH.set(itV.key(),
					itV.value() / (matrixWH.elementAt(itV.key()) + eps));
		}

		SparseMatrix matrixTranW = w.trans();

		SparseMatrix matrixWVWH = matrixTranW.mutiplyMatrix(matrixVWH);
		for (int i = h.vector.size(); i-- > 0;) {
			itH.advance();
			matrixHWVWH.set(itH.key(),
					itH.value() * matrixWVWH.elementAt(itH.key()));
		}
		return matrixHWVWH;
	}

	SparseMatrix updateW() {

		int[] dimVWH = { m, n };
		int[] dimWVWHH = { m, r };

		SparseMatrix matrixVWH = new SparseMatrix(dimVWH);
		SparseMatrix matrixWVWHH = new SparseMatrix(dimWVWHH);
		SparseMatrix matrixWH = w.mutiplyMatrix(h);
		 TLongFloatIterator itV = v.vector.iterator();
		TLongFloatIterator itW = w.vector.iterator();
		for (int i = v.vector.size(); i-- > 0;) {
			itV.advance();
			matrixVWH.set(itV.key(),
					itV.value() / (matrixWH.elementAt(itV.key()) + eps));
		}
		SparseMatrix matrixTranH = h.trans();

		SparseMatrix matrixVWHH = matrixVWH.mutiplyMatrix(matrixTranH);
		for (int i = w.vector.size(); i-- > 0;) {
			itW.advance();
			matrixWVWHH.set(itW.key(),
					itW.value() * matrixVWHH.elementAt(itW.key()));
		}
		return matrixWVWHH;
	}

	/**
	 * 矩阵归一化
	 * 
	 * @param matrix
	 * @return 归一化后矩阵
	 */
	SparseMatrix normalized(SparseMatrix matrix) {
		int ySize = matrix.size()[1];
		float ySum[] = new float[ySize];
		TLongFloatIterator it = matrix.vector.iterator();
		for (int i = matrix.vector.size(); i-- > 0;) {
			it.advance();
			ySum[matrix.getIndices(it.key())[1]] += it.value();
		}
		it = matrix.vector.iterator();
		for (int i = matrix.vector.size(); i-- > 0;) {
			it.advance();
			matrix.set(it.key(), it.value()
					/ (ySum[matrix.getIndices(it.key())[1]] + eps));
		}
		return matrix;
	}

	void calc() {
		int[] mrIndices = { m, r };
		int[] rnIndices = { r, n };
		w = SparseMatrix.random(mrIndices);
		w = normalized(w);
		h = SparseMatrix.random(rnIndices);
		float obj_old = computeObjective(v, w, h);

		for (int k = 1; k <= max_iter; k++) {
			h = updateH();
			w = updateW();
			w = normalized(w);
			float obj = computeObjective(v, w, h);
			float diff = obj - obj_old;
			System.out.printf("k = %d; obj=%f\t改变：%f\n", k, obj_old, diff);

			if (Math.abs(diff) <= lambda)
				break;
			obj_old = obj;
		}
	}

	public static void main(String[] args) {
		int[] dim = { 10, 10 };
		SparseMatrix matrix = new SparseMatrix(dim);
		Vector<int[]> vec = new Vector();
		for (int i = 0; i < dim[0]; i++)
			for (int j = 0; j < dim[1]; j++) {
				int[] indices = { j, i };
				vec.add(indices);
			}
		for (int i = 0; i < vec.size(); i++) {
			matrix.set(vec.get(i), i);
		}
		System.out.print("矩阵初始化结束\n");
		Long startTime = System.currentTimeMillis();
		Nmf nmf = new Nmf(1000, 0.0001f, 5, matrix);
		nmf.calc();
		Long endTime = System.currentTimeMillis();
		System.out.println("程序共计运行 " + (endTime - startTime) + " 毫秒");

	}
}