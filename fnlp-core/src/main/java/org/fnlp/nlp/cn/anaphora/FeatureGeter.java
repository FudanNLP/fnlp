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

package org.fnlp.nlp.cn.anaphora;

import org.fnlp.ml.types.Instance;
import org.fnlp.nlp.cn.PartOfSpeech;
import org.fnlp.nlp.cn.anaphora.Entity.FUNC;
import org.fnlp.nlp.cn.anaphora.Entity.Sex;
import org.fnlp.nlp.cn.anaphora.Entity.Singular;
/**
 * 用于训练模型的特征生成
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class FeatureGeter {
	private int[] feature;
	private EntityGroup eGroup;
	private Instance inst;
	public FeatureGeter(Instance inst){
		this.inst =inst;
		this.eGroup = (EntityGroup) inst.getData();
		feature = new int[19];
		this.doFeature();
	}
	public FeatureGeter(EntityGroup eGroup){
		feature = new int[19];
		this.eGroup = eGroup;
		this.doFeature();
	}

	private Boolean isSub(String str1,String str2){
		Boolean bl = true;
		for(int i=1;i<str2.length();i++){
			bl = bl&&str1.contains(str2.substring(i, i+1));
			if(i==1&&!bl){
				bl = true;
			}
		}	
		return bl;
	}

	private void doFeature(){   
		Entity ahead = this.eGroup.antecedent;
		Entity behind = this.eGroup.anaphor;
		String aheadData = ahead.getData();
		String behindData = behind.getData();
		Boolean bool = this.isSub(aheadData, behindData);		
		if(bool){                                   //中心词匹配
			feature[0] = 1;
		}
		else
			feature[0] = 0;				
 
		if(ahead.getPosTag()==PartOfSpeech.人名)    //I为人称代词
			feature[1] = 1;
		else
			feature[1] = 0;
		if(ahead.getPosTag()==PartOfSpeech.名词)
			feature[2] = 1;
		else
			feature[2] = 0;
		if(ahead.getPosTag().isEntiry())
			feature[3] = 1;
		else
			feature[3] = 0;
		
		if(behind.getData().contains("他")||behind.getData().contains("她"))        //J为人称代词
			feature[4] = 1;
		else
			feature[4] = 0;
		if(behind.getData().contains("它"))        //J为人称代词
			feature[5] = 1;
		else
			feature[5] = 0;
		if(behind.getData().contains("我")||behind.getData().contains("你"))        //J为人称代词
			feature[6] = 1;
		else
			feature[6] = 0;
		if(behind.getData().contains("自己"))        //J为人称代词
			feature[7] = 1;
		else
			feature[7] = 0;
		if(behind.getPosTag().isPronoun())
			feature[8] = 1;
		else
			feature[8] = 0;
		if(behindData.contains("这")||behindData.contains("那")||
				behindData.contains("其")||behindData.contains("该"))  //J为指示性名词
			feature[9] = 1;
		else
			feature[9] = 0;
		if(ahead.getPosTag().isPronoun())
			feature[8] = 1;
		else
			feature[8] = 0;
		if(ahead.getPosTag()==PartOfSpeech.名词)
			feature[9] = 1;
		else
			feature[9] = 0;
		if(ahead.getPosTag().isEntiry())
				feature[10] = 1;
			else
				feature[10] = 0;
		//是否性别一致
		if(ahead.getSex()!=behind.getSex()&&ahead.getSex()!=Sex.UNKONW){
			feature[11] = 1;
		}
		else
			feature[11] = 0;
		if(ahead.getSex()==Sex.UNKONW||behind.getSex()==Sex.UNKONW)
			feature[12] = 1;
		else 
			feature[12] = 0;
		//是否单复数一致
		if(ahead.singular !=behind.singular&&ahead.singular!=Singular.UNKONW){
			feature[13] = 1;
		}
		else
			feature[13] = 0;
		if(ahead.singular==Singular.UNKONW||behind.singular==Singular.UNKONW)
			feature[14] = 1;
		else 
			feature[14] = 0;
		if(ahead.getGraTag()==FUNC.SUB)
			feature[15]= 1;
		else
			feature[15]= 0;
		if(behind.getGraTag()==FUNC.SUB)
			feature[16]= 1;
		else
			feature[16]= 0;
		if(ahead.getGraTag()==FUNC.OBJ)
			feature[17]= 1;
		else
			feature[17]= 0;
		if(behind.getGraTag()==FUNC.OBJ)
			feature[18]= 1;
		else
			feature[18]= 0;		
	
	}
	public Instance getInst(){
		return this.inst;
	}
	public int[]getFeature(){
		return this.feature;
	}
	public EntityGroup getEgroup(){
		return this.eGroup;
	}
}