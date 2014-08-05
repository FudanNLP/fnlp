package org.fnlp.nlp.similarity;

import org.fnlp.ml.types.sv.HashSparseVector;



public class SparseVectorSimilarity  implements ISimilarity<HashSparseVector> {

	@Override
	public float calc(HashSparseVector item1, HashSparseVector item2) {
		return item1.dotProduct(item2);
	}

}
