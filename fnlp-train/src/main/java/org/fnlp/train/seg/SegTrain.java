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

package org.fnlp.train.seg;

import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.AlphabetFactory.Type;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.Target2Label;
import org.fnlp.nlp.pipe.WeightPipe;
import org.fnlp.nlp.pipe.seq.AddCharRange;
import org.fnlp.nlp.pipe.seq.Sequence2FeatureSequence;
import org.fnlp.nlp.pipe.seq.templet.BaseTemplet;
import org.fnlp.nlp.pipe.seq.templet.CharClassTemplet;
import org.fnlp.nlp.pipe.seq.templet.CustomTemplet;
import org.fnlp.nlp.pipe.seq.templet.Templet;
import org.fnlp.nlp.pipe.seq.templet.TempletGroup;
import org.fnlp.nlp.tag.AbstractTagger;
import org.fnlp.ontology.CharClassDictionary;

/**
 * 序列标注器训练和测试程序
 * 
 * @author xpqiu
 * 
 */
public class SegTrain extends AbstractTagger{

	@Override
	public Pipe createProcessor() throws Exception {

		if(cl!=null){
			factory = cl.getAlphabetFactory();
		}else{
			factory = AlphabetFactory.buildFactory();
			templets = new TempletGroup();
			templets.load(templateFile);
			for(Templet templet:templets){
				((BaseTemplet) templet).minLen = 0;
			}
			//Dictionary d = new Dictionary();
			// d.loadWithWeigth("D:/xpqiu/项目/自选/CLP2010/CWS/av-b-lut.txt",
			// "AV");
			// templets.add(new DictionaryTemplet(d, gid++, 0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -1,0, 1));
			// templets.add(new DictionaryTemplet(d, gid++, -2,-1,0, 1));
//			CharClassDictionary dsurname = new CharClassDictionary();
//			dsurname.load("../data/knowledge/百家姓.txt", "姓");
//			templets.add(new CharClassTemplet(templets.gid++, new CharClassDictionary[]{dsurname}));
//			templets.add(new CustomTemplet(templets.gid++));
		}


		labels = factory.DefaultLabelAlphabet();

		//TODO: 修改字典类型
		AlphabetFactory.defaultFeatureType = Type.String;
		// 将样本通过Pipe抽取特征
		features = factory.DefaultFeatureAlphabet();
		featurePipe = new Sequence2FeatureSequence(templets, features, labels);
		AddCharRange typePip = new AddCharRange();
		Pipe weightPipe = new WeightPipe(true);
		Pipe pipe = new SeriesPipes(new Pipe[] { new Target2Label(labels),  typePip, featurePipe, weightPipe  });
		return pipe;
	}
}