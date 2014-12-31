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

package org.fnlp.nlp.cn;

import java.util.EnumSet;

public enum PartOfSpeech {
	
	名词,
	
	专有名,
	人名,
	地名,
	机构名,
	实体名,
	型号名,
    事件名,
    网址,
    品牌名,
	
	形容词, 
	形谓词,
	副词,
	限定词,
	
	代词,
	人称代词, 
	指示词, 
	疑问代词,
	
	
	
    
    从属连词,
    并列连词,
    结构助词,
    介词,
    
    数词,
    序数词,
    量词,   
    
    
    动词,
    情态词,
    趋向词,
    被动词,
    把动词,
    
    时间短语, 
    惯用词, 	
	拟声词, 	 
	省略词, 
	语气词, 
    时态词,
    方位词,
    标点, 
    叹词,
	表情符,
    运算符,
    
    动态助词,
    
    未知
    ;
	
	

	public static PartOfSpeech[] valueOf(String[] pos) {
		if(pos==null)
			return null;
		PartOfSpeech[] epos = new PartOfSpeech[pos.length];
		
		for(int i=0;i<pos.length;i++){
			try {
				epos[i] = valueOf(pos[i]);
			} catch (Exception e) {
				System.out.println("未知词性:" +pos[i]);
				epos[i] = 未知;
			}
		}
		return epos;
	}
	
		
	
	static EnumSet<PartOfSpeech> Pronoun = EnumSet.noneOf(PartOfSpeech.class); 
	static{
		Pronoun.add(代词);
		Pronoun.add(人称代词);
		Pronoun.add(指示词);
		Pronoun.add(疑问代词);
	}
	/**
	 * 是否为代词
	 * @return
	 */
	public boolean isPronoun() {
		
		return Pronoun.contains(this);
	}
	
	/**
	 * 是否为标点
	 * @return
	 */
	public boolean isMark() {
		return this==标点;
	}   
    
	static EnumSet<PartOfSpeech> entities = EnumSet.noneOf(PartOfSpeech.class); 
	static{
		entities.add(人名);
		entities.add(地名);
		entities.add(机构名);
		entities.add(专有名);
		entities.add(实体名);
	}
	
	/**
	 * 判断词性是否为一个实体，包括：人名|地名|机构名|专有名。
	 */
	public boolean isEntiry() {
		return entities.contains(this);
	}
	/**
	 * 判断词性是否为一个实体，包括：人名|地名|机构名|专有名|实体名。
	 * @param pos
	 * @return
	 */
	public static boolean isEntiry(String pos) {
		PartOfSpeech p;
		try {
			p = valueOf(pos);
		} catch (Exception e) {
			System.err.println(pos+"不存在");
			return false;
		}		
		return p.isEntiry();
	}
    
    
	
	
}