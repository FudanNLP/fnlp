
/**
 * @author xpqiu
 *
 */
package org.fnlp.nlp.cn.anaphora.train;

/**
 * 训练步骤：
 * 1、通过DocFilter.java过滤掉不含有第三人称代词和指示代词的文件；
 * 2、通过MyDocumentWriter.java生成特征训练文件；
 * 3、通过ARClassifier.java对生成的特征训练文件进行训练，生成训练模型。
 */