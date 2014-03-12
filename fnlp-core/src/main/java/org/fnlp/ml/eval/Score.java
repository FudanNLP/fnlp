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

import java.text.DecimalFormat;

import org.fnlp.util.MyArrays;
/**
 * 计算分类性能得分
 * @author xpqiu
 *
 */
public class Score {
	DecimalFormat df = new DecimalFormat("##.00");
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String score(Integer[] pred,Integer[] golden,int numofclass) {
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
		float[] falsePositive=new float[numofclass];
		float[] numperclass =new float[numofclass];
		int totnum = pred.length;
		for(int i=0;i<totnum;i++){

			if(golden[i]==pred[i]){//正确
				leafcor++;
				truePositive[golden[i]]++;

			}
			else{	
				falsePositive[pred[i]]++;
				falseNegative[golden[i]]++;

			}
		}
        for (int i = 0; i < totnum; i++) { 
            if (golden[i] < numofclass)
                numperclass[golden[i]]++;
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
					sb.append(i+"\t\t"+ (int)numperclass[i] + "\t\t" + df.format(precision[i]*100)+"\t\t"+ df.format(recall[i]*100)+"\t\t"+ df.format(f[i]*100));
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