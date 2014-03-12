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
	private int[] featrue;
	private EntityGroup eGroup;
	private Instance inst;
	public FeatureGeter(Instance inst){
		this.inst =inst;
		this.eGroup = (EntityGroup) inst.getData();
		featrue = new int[19];
		this.doFeature();
	}
	public FeatureGeter(EntityGroup eGroup){
		featrue = new int[19];
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
			featrue[0] = 1;
		}
		else
			featrue[0] = 0;				
 
		if(ahead.getPosTag()==PartOfSpeech.人名)    //I为人称代词
			featrue[1] = 1;
		else
			featrue[1] = 0;
		if(ahead.getPosTag()==PartOfSpeech.名词)
			featrue[2] = 1;
		else
			featrue[2] = 0;
		if(ahead.getPosTag().isEntiry())
			featrue[3] = 1;
		else
			featrue[3] = 0;
		
		if(behind.getData().contains("他")||behind.getData().contains("她"))        //J为人称代词
			featrue[4] = 1;
		else
			featrue[4] = 0;
		if(behind.getData().contains("它"))        //J为人称代词
			featrue[5] = 1;
		else
			featrue[5] = 0;
		if(behind.getData().contains("我")||behind.getData().contains("你"))        //J为人称代词
			featrue[6] = 1;
		else
			featrue[6] = 0;
		if(behind.getData().contains("自己"))        //J为人称代词
			featrue[7] = 1;
		else
			featrue[7] = 0;
		if(behind.getPosTag().isPronoun())
			featrue[8] = 1;
		else
			featrue[8] = 0;
		if(behindData.contains("这")||behindData.contains("那")||
				behindData.contains("其")||behindData.contains("该"))  //J为指示性名词
			featrue[9] = 1;
		else
			featrue[9] = 0;
		if(ahead.getPosTag().isPronoun())
			featrue[8] = 1;
		else
			featrue[8] = 0;
		if(ahead.getPosTag()==PartOfSpeech.名词)
			featrue[9] = 1;
		else
			featrue[9] = 0;
		if(ahead.getPosTag().isEntiry())
				featrue[10] = 1;
			else
				featrue[10] = 0;
		//是否性别一致
		if(ahead.getSex()!=behind.getSex()&&ahead.getSex()!=Sex.UNKONW){
			featrue[11] = 1;
		}
		else
			featrue[11] = 0;
		if(ahead.getSex()==Sex.UNKONW||behind.getSex()==Sex.UNKONW)
			featrue[12] = 1;
		else 
			featrue[12] = 0;
		//是否单复数一致
		if(ahead.singular !=behind.singular&&ahead.singular!=Singular.UNKONW){
			featrue[13] = 1;
		}
		else
			featrue[13] = 0;
		if(ahead.singular==Singular.UNKONW||behind.singular==Singular.UNKONW)
			featrue[14] = 1;
		else 
			featrue[14] = 0;
		if(ahead.getGraTag()==FUNC.SUB)
			featrue[15]= 1;
		else
			featrue[15]= 0;
		if(behind.getGraTag()==FUNC.SUB)
			featrue[16]= 1;
		else
			featrue[16]= 0;
		if(ahead.getGraTag()==FUNC.OBJ)
			featrue[17]= 1;
		else
			featrue[17]= 0;
		if(behind.getGraTag()==FUNC.OBJ)
			featrue[18]= 1;
		else
			featrue[18]= 0;		
	
	}
	public Instance getInst(){
		return this.inst;
	}
	public int[]getFeatrue(){
		return this.featrue;
	}
	public EntityGroup getEgroup(){
		return this.eGroup;
	}
}