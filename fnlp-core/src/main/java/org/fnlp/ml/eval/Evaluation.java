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

package org.fnlp.ml.eval;

import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.fnlp.ml.classifier.AbstractClassifier;
import org.fnlp.ml.classifier.TPredict;
import org.fnlp.ml.classifier.hier.Tree;
import org.fnlp.ml.types.InstanceSet;
import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.util.MyArrays;
/**
 * 评测类
 * 给定数据集，计算分类器的分类性能。
 * @author xpqiu
 *
 */
public class Evaluation  {
	DecimalFormat df = new DecimalFormat("##.00");
	private int[] golden;
	private int numofclass;
	private Tree tree;
	private int totnum;
	private InstanceSet test;
	private LabelAlphabet labels;

	public Evaluation(InstanceSet test) {
		this.test = test;
		totnum=test.size();
		golden = new int[totnum];
		for(int i=0;i<totnum;i++){
			golden[i] = (Integer) test.getInstance(i).getTarget();
		}
		

	}

	public Evaluation(InstanceSet test,Tree tree) {
		this(test);		
		if(tree!=null){
			numofclass=tree.size;
			this.tree = tree;
		}
	}

	public void eval2File(AbstractClassifier cl,String path){
		PrintWriter pw;
		try {
			pw = new PrintWriter(path, "utf8");

			String s = eval(cl);
			pw.write(s);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param cl 
	 * @param nbest top n性能
	 */
	public void eval(AbstractClassifier cl,int nbest){
		labels = cl.getAlphabetFactory().DefaultLabelAlphabet();
		numofclass = labels.size();
		TPredict[] pred = cl.classify(test,nbest);
		int[] acc = new int[nbest];
		for(int i=0;i<totnum;i++){
			boolean cor =false;
			for(int j=0;j<nbest;j++){
				if(golden[i]==(Integer) pred[i].getLabel(j)){
					acc[j]++;
					break;
				}					
			}			
		}
		int[] accc = MyArrays.accumulate(acc);
		for(int i =0;i<nbest;i++){
			System.out.println("Top "+i+" Accuracy:" + accc[i]/(float)totnum);
		}
	}

	/**
	 * 评测分类性能
	 * 得到：Accuracy	Macro F-measure	Macro Precision	Macro Recall	Tree Induced Error
	 * @param cl 分类器
	 */
	public String eval(AbstractClassifier cl) {
		TPredict[] res = cl.classify(test,1);
		int[] pred = new int[res.length];
		for(int i=0;i<res.length;i++){
			pred[i] = (Integer) res[i].getLabel(0); //Get the first Label
		}
		float Accuracy;
		float MarcoF;
		float MacroPrecision = 0;
		float MacroRecall = 0;
		float Treeloss;


		float leafcor=0;
		float loss=0;
		float[] ttcon=new float[10];

		float[] truePositive=new float[numofclass];
		float[] falseNegative=new float[numofclass];
		float[] numberclass =new float[numofclass];
		float[] falsePositive=new float[numofclass];

		for(int i=0;i<totnum;i++){

			if(golden[i]==pred[i]){//正确
				leafcor++;
				truePositive[golden[i]]++;

			}
			else{	
				falsePositive[pred[i]]++;
				falseNegative[golden[i]]++;
				if(tree!=null){
					loss+=tree.dist(golden[i], pred[i]);
				}

			}
		}
        for (int i = 0; i < totnum; i++) { 
            if (golden[i] < numofclass)
                numberclass[golden[i]]++;
            else
                System.out.println("Not Format");
        }
		Treeloss=loss/totnum;
		Accuracy=leafcor/totnum;

		System.out.println(" Accuracy:" + Accuracy);

		float count1=0;
		float count2=0;
		float[] precision = new float[numofclass];
		float[] recall = new float[numofclass];
		float[] f = new float[numofclass];


		for(int i=0;i<numofclass;i++){
			float base = truePositive[i]+falsePositive[i]; 

			if(base>0)
				precision[i]= truePositive[i]/base;
			else{
				count1++;	
			}
			base = truePositive[i]+falseNegative[i]; 
			if(base>0)
				recall[i] = truePositive[i]/base;
			else{
				count2++;
			}

			f[i] = 2*precision[i]*recall[i]/(precision[i]+recall[i]+Float.MIN_VALUE);
		}


		//计算宏平均
		MacroPrecision= MyArrays.sum(precision)/(numofclass-count1);
		MacroRecall=MyArrays.sum(recall)/(numofclass-count2);	

		MarcoF=2*MacroPrecision*MacroRecall/(MacroPrecision+MacroRecall+Float.MIN_VALUE);		

		StringBuilder sb= new StringBuilder();


		sb.append("===========评测结果===========\n");
		sb.append("--------------------微平均---------------------");
		sb.append("\n");
		sb.append("Accuracy:" + Accuracy);
		sb.append("\n");
		sb.append("--------------------宏平均---------------------\n");
		sb.append("Accuracy\t\tPrecision\t\tRecall \t\tF1");
		sb.append("\n");
		sb.append(df.format(Accuracy*100)+"\t\t"+ df.format(MacroPrecision*100)+"\t\t"+ df.format(MacroRecall*100) + "\t\t"
				+ df.format(MarcoF*100)+"\t\t"+ df.format(Treeloss));
		sb.append("\n");		
		sb.append("各类分析：");
		sb.append("\n");
		sb.append("Class\t\tNumberClass\t\tPrecision\t\tRecall \t\tF1");
		sb.append("\n");
		for(int i=0;i<numofclass;i++){
			sb.append(labels.lookupString(i)+"\t\t"+ (int)numberclass[i] + "\t\t" + df.format(precision[i]*100)+"\t\t"+ df.format(recall[i]*100)+"\t\t"+ df.format(f[i]*100));
			sb.append("\n");
		}



		int i=0;
		while(ttcon[i]!=0){
			ttcon[i] = Float.parseFloat(df.format(ttcon[i]*100));
			sb.append(""+i+"th level accurary: "+(float)ttcon[i]/totnum);
			i++;
		}
		sb.append("===========评测结果 END===========");
		return sb.toString();
	}

}