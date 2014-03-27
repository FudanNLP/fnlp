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

package org.fnlp.train.tag;

import org.fnlp.nlp.tag.Tagger;

public class TrainTagger {

	/*
	 * Tagger可以用来训练分词、词性标注、实体名识别等
	 * 训练需要模板和训练语料，这些内容都存放在 \\10.141.200.3\Datasets\TrainData 中
	 * TrainData中有三个目录 segmentation postagged ner 分别是分词、词性标注和实体名识别
	 * 将相应的语料拷贝到指定的文件夹，修改下列参数就可以进行训练了
	 */
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// 指定训练语料和模型存储的位置
		String template = "./tmpdata/template.sighan2005";
		String corpus = "./tmpdata/as_training.utf8";
		String model = "./tmpdata/cws.m";
		
		// 如果在训练过程中没有测试文件请保持testfile为""
		String testfile = "";
		
		if(testfile != ""){
			Tagger.main(new String[]{"-train",template,corpus,model,testfile});
		}else{
			Tagger.main(new String[]{"-train",template,corpus,model});
		}
		

	}

}