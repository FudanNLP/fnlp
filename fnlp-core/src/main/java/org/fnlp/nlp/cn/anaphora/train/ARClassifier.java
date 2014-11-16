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

package org.fnlp.nlp.cn.anaphora.train;

	import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import org.fnlp.data.reader.ListReader;
import org.fnlp.data.reader.SimpleFileReader;
import org.fnlp.data.reader.SimpleFileReader.Type;
import org.fnlp.ml.classifier.linear.Linear;
import org.fnlp.ml.classifier.linear.OnlineTrainer;
import org.fnlp.ml.classifier.linear.inf.Inferencer;
import org.fnlp.ml.classifier.linear.inf.LinearMax;
import org.fnlp.ml.classifier.linear.update.LinearMaxPAUpdate;
import org.fnlp.ml.classifier.linear.update.Update;
import org.fnlp.ml.feature.Generator;
import org.fnlp.ml.feature.SFGenerator;
import org.fnlp.ml.loss.ZeroOneLoss;
import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;
import org.fnlp.nlp.pipe.StringArray2IndexArray;
import org.fnlp.nlp.pipe.Target2Label;
	/**
	 * 训练分类器
	 * @author jszhao
	 * @version 1.0
	 * @since FudanNLP 1.5
	 */
	public class ARClassifier {
 
		static InstanceSet train;
		static InstanceSet test;
		static AlphabetFactory factory = AlphabetFactory.buildFactory();
		static LabelAlphabet al = factory.DefaultLabelAlphabet();
		static String path = null;
		static Pipe pipe;
		/**
		 * 训练文件
		 */
		private String trainFile = "../tmp/ar-train.txt";
		
		/**
		 * 模型文件
		 */
		private static  String modelFile =  "../models/ar.m";

		public static void main(String[] args) throws Exception {

			ARClassifier tc = new ARClassifier();
			tc.train();
			Linear cl =Linear.loadFrom(modelFile);
			int i = 0;int j = 0;double ij = 0.0;int kk = 0;int jj = 0;int nn = 0;int n = 0;
			InstanceSet test = new InstanceSet(cl.getPipe(),cl.getAlphabetFactory());
			SimpleFileReader sfr = new SimpleFileReader("../tmp/ar-train.txt",true);
			
			ArrayList<Instance> list1 = new ArrayList<Instance>();
			while (sfr.hasNext())
			{
				list1.add(sfr.next());
			}
			List<String>[] str1 = new List[list1.size()];
			String[] str2 = new String[list1.size()];
			Iterator it = list1.iterator();
			while(it.hasNext()){
				Instance in = (Instance) it.next();
				str1[i] = (List<String>) in.getData();
				str2[i] = (String) in.getTarget();
				i++;
			}
			for(int k = 0;k<str2.length;k++)
			{
				if(str2[k].equals("1"))
					kk++;
			}
			String ss =null;
				test.loadThruPipes(new ListReader(str1));
				
				for(int ii=0;ii<str1.length;ii++){
					ss = cl.getStringLabel(test.getInstance(ii));
					if(ss.equals("1"))
						j++;
				
					if(ss.equals("1")&&ss.equals(str2[ii]))
						jj++;
					if(ss.equals("0")&&ss.equals(str2[ii]))
						n++;
					if(ss.equals(str2[ii]))
						nn++;
				}
						
			
			ij = (nn+0.0)/str2.length;
			System.out.print("整体正确率："+ij);System.out.print('\n');
			ij = (jj+0.0)/kk;
			System.out.print("判断为指代关系的正确率："+ij);System.out.print('\n');
			ij = (n+0.0)/(str2.length-kk);
			System.out.print("判断为非指代关系的正确率："+ij);System.out.print('\n');

			System.gc();
		}

		/**
		 * 训练
		 * @throws Exception
		 */
		public void train() throws Exception {

			//建立字典管理器

			
			Pipe lpipe = new Target2Label(al);
			Pipe fpipe = new StringArray2IndexArray(factory, true);
			//构造转换器组
			SeriesPipes pipe = new SeriesPipes(new Pipe[]{lpipe,fpipe});



			InstanceSet instset = new InstanceSet(pipe,factory);
			instset.loadThruStagePipes(new SimpleFileReader(trainFile," ",true,Type.LabelData)); 
			Generator gen = new SFGenerator();
			ZeroOneLoss l = new ZeroOneLoss();
			Inferencer ms = new LinearMax(gen, factory.getLabelSize());
			Update update = new LinearMaxPAUpdate(l);
			OnlineTrainer trainer = new OnlineTrainer(ms, update,l, factory, 50,0.005f);
			Linear pclassifier = trainer.train(instset,instset);
			pipe.removeTargetPipe();
			pclassifier.setPipe(pipe);
			factory.setStopIncrement(true);
			pclassifier.saveTo(modelFile);
		}
	
}