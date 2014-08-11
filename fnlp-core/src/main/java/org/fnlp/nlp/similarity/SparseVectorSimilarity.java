package org.fnlp.nlp.similarity;

import java.io.Serializable;

import org.fnlp.ml.types.sv.HashSparseVector;



public class SparseVectorSimilarity  implements ISimilarity<HashSparseVector> ,Serializable{

	@Override
	public float calc(HashSparseVector item1, HashSparseVector item2) {
		//return item1.dotProduct(item2);
		return item1.cos(item2);
	}

}
