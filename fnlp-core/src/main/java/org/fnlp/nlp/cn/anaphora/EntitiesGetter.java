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

import java.util.EnumSet;
import java.util.LinkedList;

import org.fnlp.nlp.cn.PartOfSpeech;
import org.fnlp.nlp.cn.anaphora.Entity.FUNC;
import org.fnlp.nlp.cn.anaphora.Entity.Singular;

/**
 * 检测文中的实体和代词
 * @author jszhao,xpqiu
 * @version 1.2
 * @since FudanNLP 1.5
 */
public class EntitiesGetter {

	static EnumSet<PartOfSpeech> NP = EnumSet.noneOf(PartOfSpeech.class); 
	static{
		NP.add(PartOfSpeech.专有名);
		NP.add(PartOfSpeech.人名);
		NP.add(PartOfSpeech.机构名);
		NP.add(PartOfSpeech.地名);
		NP.add(PartOfSpeech.专有名);
		NP.add(PartOfSpeech.序数词);
		NP.add(PartOfSpeech.数词);
		NP.add(PartOfSpeech.量词);
		NP.add(PartOfSpeech.形谓词);
		NP.add(PartOfSpeech.形容词);
		NP.add(PartOfSpeech.限定词);
		NP.add(PartOfSpeech.名词);
		NP.add(PartOfSpeech.代词);
		NP.add(PartOfSpeech.指示词);
		NP.add(PartOfSpeech.人称代词);
		NP.add(PartOfSpeech.疑问代词);
		
	}	

	public EntitiesGetter() {
		
	}
	/**
	 * 是实体的一部分
	 * @param pos
	 * @param word
	 * @return
	 */
	private boolean isPart(PartOfSpeech pos, String word){
		if(NP.contains(pos))
			return true;
		
		if(pos == PartOfSpeech.结构助词 && word.equals("的"))
			return true;
		
		return false;
	}


	public LinkedList<Entity> parse(String[][][] taggedstr) {	
		
		LinkedList<Entity> EntityList = new LinkedList<Entity>();
		Entity ey = null;	
		int distance = 0;
		int index =  0;
		int subDistance = 0;
		String strdata= null; 
		int flag = 0;
		
		
		
		

		for(int i=0;i<taggedstr.length;i++){
			String[] words = taggedstr[i][0];
			String[] pos = taggedstr[i][1];
			
			PartOfSpeech strtag = null;
			
			PartOfSpeech[] epos = PartOfSpeech.valueOf(pos);
			
			for(int j=0;j<words.length;j++){	
				index++;
				subDistance = 0;
				String headword = null;
				if(epos[j]==PartOfSpeech.标点&&((words[j].equals("，"))
						||(words[j].equals("：")))){
					subDistance++;
				} 
				
				
				if(isPart(epos[j],words[j])){
					int id = j;
					strdata = words[j];
					strtag = epos[j];
					headword = words[j];
					flag = 0;
					ey = new Entity();					
					ey.start = index;
					
					Singular isSing = Singular.UNKONW;
					
					while(j<words.length-1){
						boolean isModify = !(isNN(epos[j])&&words[j+1].equals("的"));
						if(isModify&&isPart(epos[j+1],words[j+1])){						
							if(epos[j]==PartOfSpeech.数词 &&(words[j].equals("一")||
									words[j].equals("半")||words[j].equals("1")))
								isSing = Singular.Yes;
							else if (epos[j]==PartOfSpeech.数词 &&!(words[j].equals("一")
									||words[j].equals("半")||words[j].equals("1"))){
								isSing = Singular.No;
							}
							strdata+= words[j+1];
							strtag = epos[j+1];
							headword = words[j+1];
							j++;		
							flag++;
						}
						else
							break;
					}
					if(strtag.isPronoun()||strdata.contains("这")||
							strdata.contains("那")||strdata.contains("该")){
						ey.setIsResolution(true);
					}
					else
						ey.setIsResolution(false); 
					int jj = j;
					while((!isNN(strtag))&&jj>=0){
						int ij = strdata.indexOf(words[jj]);
						if(ij>=0)
							strdata = strdata.substring(0,ij);
						else
							break;
						jj--;
						flag--;
						if(jj>=0)
							strtag = epos[jj];
					}
					if(strdata.length() == 0)
						continue;
					if(strdata.indexOf("的")==0){
						strdata = strdata.substring(1);
						ey.start = ey.start+1 ;
					}

					ey.setPosTag(strtag);
					ey.setData(strdata);
					ey.setHeadWord(headword);
					
					if(isSingular(ey.getData())){
						isSing = Singular.Yes;
					}
					else if(isNotSingular(ey.getData())){
						isSing = Singular.No;
					}


					if(this.isFemale(ey.getData())){
						ey.setFemale();
					}
					else if(this.isMale(ey.getData())){
						ey.setMale();
					}
					
					FUNC graTag = FUNC.SUB;
					while((j-flag-1)>=0&&!epos[j-flag-1].isMark()){
						if(isObj(epos[j-flag-1])){
							graTag = FUNC.OBJ;
							break;
						}
						flag++;
					}

					if(j<words.length-1&&pos[j+1].equals("DEG")&&
							words[j+1].equals("的")){
						graTag = FUNC.ADJ;
					}
					ey.setId(id);
					ey.setGraTag(graTag);
					ey.singular = isSing;
					ey.sentNo = i;		
					ey.setSubDistance(subDistance);
					ey.end = j;
					
					EntityList.add(ey);
				}

			}
		}
		return EntityList;
	}
	
	static EnumSet<PartOfSpeech> NN = EnumSet.noneOf(PartOfSpeech.class); 
	static{
		NN.add(PartOfSpeech.名词);
		NN.add(PartOfSpeech.专有名);
		NN.add(PartOfSpeech.人名);	
		NN.add(PartOfSpeech.地名);
		NN.add(PartOfSpeech.机构名);
		NN.add(PartOfSpeech.代词);
		NN.add(PartOfSpeech.人称代词);
		NN.add(PartOfSpeech.指示词);
		NN.add(PartOfSpeech.疑问代词);
	}
	
	/**
	 * 是否为NN
	 * @param pos
	 * @return
	 */
	public boolean isNN(PartOfSpeech pos) {
		return NN.contains(pos);
	}
	
	
	static EnumSet<PartOfSpeech> obj = EnumSet.noneOf(PartOfSpeech.class); 
	static{
		obj.add(PartOfSpeech.副词);
		obj.add(PartOfSpeech.动词);
		obj.add(PartOfSpeech.介词);
		obj.add(PartOfSpeech.形谓词);
		obj.add(PartOfSpeech.形容词);
	}
	
	/**
	 * 是否是宾语
	 * @param pos
	 * @return
	 */
	private boolean isObj(PartOfSpeech pos) {
		return obj.contains(pos);
	}
	
	
	private Boolean isSingular(String str){
		if(str.contains("这个")||str.contains("这种")||
				str.contains("每")||str.equals("他")||
				str.equals("它")||str.equals("她")){
			return true;
		}
		else
			return false;

	}
	
	private Boolean isNotSingular(String str){ 
		if(str.startsWith("各")||str.contains("群")||
				str.contains("多")||str.startsWith("二者")||
				str.startsWith("全体")||str.startsWith("所有")
				||str.contains("们")){
			return true;
		}
		else
			return false;

	}
	
	private Boolean isFemale(String str){
		if(str.contains("娘")||str.contains("妻")||
				str.contains("媳")||str.contains("姑")||
				str.contains("夫人")||str.contains("她")||
				str.contains("小姐")||str.contains("女")||
				str.contains("母")||str.contains("妞")||
				str.contains("妈")||str.contains("妇")||
				str.contains("婆")){
			return true;
		}
		else
			return false;

	}

	private Boolean isMale(String str){
		if(str.contains("先生")||str.contains("男")||
				str.contains("丈夫")||str.contains("父")||
				str.contains("兄")||str.contains("儿子")
				||str.contains("哥")){
			return true;
		}
		else
			return false;

	}

	public static void main(String args[]) throws Exception{
		EntitiesGetter ep = new EntitiesGetter();
		Entity ey = null;
		String str2 = "复旦大学创建于1905年,它位于上海市，这个大学培育了好多优秀的学生。";
		String str3[] = {"复旦","大学","创建","于","1905年","，","它","位于","上海市","，","这个","大学","培育","了","好多","优秀","的","学生","。"};
		String str4[] = {"专有名","名词","动词","介词","时间短语","标点","代词","动词","专有名","标点","限定词","名词","动词","动态助词","数词","形容词","结构助词","名词","标点"};
		String str5[][][] = new String[1][2][str3.length];
		str5[0][0] = str3;
		str5[0][1] = str4;		
		
		LinkedList<Entity> list = ep.parse(str5);
		System.out.println(list);
	}


}