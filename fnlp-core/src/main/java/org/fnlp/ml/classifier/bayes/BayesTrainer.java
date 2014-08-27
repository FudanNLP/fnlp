package org.fnlp.ml.classifier.bayes;

import gnu.trove.iterator.TIntFloatIterator;

import java.util.List;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.linear.AbstractTrainer;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
/**
 * 贝叶斯文本分类模型训练器
 * 输入训练数据为稀疏矩阵
 * @author sywu
 *
 */
public class BayesTrainer{

	public AbstractClassifier train(InstanceSet trainset) {
		AlphabetFactory af=trainset.getAlphabetFactory();
		SeriesPipes pp=(SeriesPipes) trainset.getPipes();
		pp.removeTargetPipe();
		return train(trainset,af,pp);
	}
	public AbstractClassifier train(InstanceSet trainset,AlphabetFactory af,Pipe pp) {
		ItemFrequency tf=new ItemFrequency(trainset,af);
		BayesClassifier classifier=new BayesClassifier();
		classifier.setFactory(af);
		classifier.setPipe(pp);
		classifier.setTf(tf);
		return classifier;
	}
}
