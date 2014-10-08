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

package org.fnlp.nlp.corpus.ctbconvert;
/**
 * 生成依赖类型
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public  class DepClassProducter {
	
	private static final String VDD = "趋向词";
	
	private static boolean isCoreLeft(Node label){
		return label.getId()<label.getCore().getId();
	}
	
	private static boolean isROOT(Node label){
		return label.getCore().getId() == 0;
	}
	
	private static boolean isPMOD(Node label){
		return label.getCore().getTag().equals("P");
	}
	
	private static boolean isP(Node label){
		return label.getTag().equals("PU");
	}
	
	private static boolean is并列(Node label){
		return (label.isCoordinative&&(!label.getTag().equals("CC")));
	}
	private static boolean is并列2(Node label){
		return(label.getCore().getTag().equals("CS")
				&&label.getCore().getTag().equals("CS"));
	}
	
	private static boolean is数量(Node label){
		return label.getCore().getTag().equals("M")&&
				(label.getTag().equals("CD")||
						label.getTag().equals("OD"));
	}
	private static boolean is量词(Node label){
		return label.getTag().equals("M");
	}
	private static boolean isAMOD(Node label){
		return label.getCore().getTag().equals("M")||
				label.getCore().getTag().equals("CD")||
				label.getCore().getTag().equals("OD")||
				label.getCore().getTag().equals("JJ");
	}
	
	private static boolean isPRD(Node label){
		return(!isCoreLeft(label)&&label.getCore().getTag().equals("VC"));		
	}
	
	private static boolean isOBJ2(Node label){
//		if(label.getData().equals("的"))
//			System.out.println();
		return(!isCoreLeft(label)&&label.getCore().getTag().equals("VC")&&label.getData().equals("的"));		
	}
	
	private static boolean isSUB(Node label){
		return(isCoreLeft(label)&&
				(label.getCore().getTag().startsWith("V")||
						label.getCore().getTag().equals(VDD)||
						label.getCore().getTag().equals("AD")||
						label.getCore().getTag().equals("BA")||
						label.getCore().getTag().equals("LB")||
						label.getCore().getTag().equals("AS")||
						label.getCore().getTag().equals("MSP")||
						label.getCore().getTag().equals("SB")
						)&&(label.getTag().equals("NN")
						||label.getTag().equals("NR")
						||label.getTag().equals("PN")
						||label.getTag().equals("CD")));
	}
	
	private static boolean isOBJ(Node label){
		return!isCoreLeft(label)&&	
				(label.getCore().getTag().startsWith("V")||
						label.getCore().getTag().equals(VDD)||
						label.getCore().getTag().equals("AD")||
						label.getCore().getTag().equals("AS")||
						label.getCore().getTag().equals("BA")||
						label.getCore().getTag().equals("LB")||
						label.getCore().getTag().equals("SB")||
						label.getCore().getTag().equals("MSP"))
				&&(label.getTag().equals("NN")
						||label.getTag().equals("NR")
						||label.getTag().equals("PN"));
	}

	
	private static boolean isDEP(Node label){
		return label.getCore().getTag().equals("DT")
				||label.getCore().getTag().equals("ETC")
				||label.getCore().getTag().equals("SP")
				||label.getCore().getTag().equals("IJ")
				||label.getCore().getTag().equals("CC")
				||label.getCore().getTag().equals("URL")
				||label.getCore().getTag().equals("PU")
				||label.getCore().getTag().equals("LC")
				||((label.getCore().getTag().startsWith("DE")&&
						!label.getTag().startsWith("V")&&
						!label.getTag().equals(VDD)));
	}
	private static boolean isD(Node label){
		return label.getCore().getTag().equals("LC");
	}
	private static boolean is同位语(Node label){
		return((label.getCore().getTag().startsWith("N")
				||label.getCore().getTag().equals("PN"))&&
				(label.getTag().startsWith("N")
						||label.getTag().equals("PN"))&&
						(label.getCore().getId()-label.getId() ==1));
	}
	private static boolean isVMOD(Node label){
		return(!(label.getTag().equals("NN")
				||label.getTag().equals("NR")
				||label.getTag().equals("PN"))&&
				(label.getCore().getTag().startsWith("V")||
						label.getCore().getTag().equals(VDD)||
						label.getCore().getTag().equals("AD")||
						label.getCore().getTag().equals("AS")||
						label.getCore().getTag().equals("MSP")||
						label.getCore().getTag().equals("BA")||
						label.getCore().getTag().equals("SB")||
						label.getCore().getTag().equals("LB")))||
				(label.getCore().getCore().getId()==0&&label.getCore().getTag().equals("PU"));
	}
	private static boolean isNMOD(Node label){
		return(label.getCore().getTag().startsWith("N")
				||label.getCore().getTag().equals("PN"));
	}
	private static boolean is定语(Node label){
		return isCoreLeft(label)&&isNMOD(label);
	}
	private static boolean is状语(Node label){
		return isCoreLeft(label)&&isVMOD(label);
	}
	private static boolean is补语(Node label){
		return !isCoreLeft(label)&&(isNMOD(label)||isVMOD(label));
	}
	private static boolean is的字结构(Node label){
		return(label.getCore().getData().equals("的")
				&&label.getCore().getTag().startsWith("DE"));
	}
	
	private static boolean is得字结构(Node label){
		return(label.getCore().getData().equals("得")
				&&label.getCore().getTag().startsWith("DE"));
	}
	
	private static boolean is地字结构(Node label){
		return(label.getCore().getData().equals("地")
				&&label.getCore().getTag().startsWith("DE"));
	}
	private static boolean isSBAR(Node label){
		return((label.getTag().startsWith("V")||label.getTag().equals(VDD))&&
				(isCoreLeft(label)&&label.getCore().getTag().startsWith("DE"))||
				(!isCoreLeft(label)&&(label.getCore().getTag().equals("CS")||
						label.getCore().getTag().equals("AD"))));		
	}
	private static boolean is之字结构(Node label){
		return label.getCore().getTag().startsWith("DE")&&label.getCore().getData().equals("之");
	}
	private static boolean is语态(Node label){
		return(label.getTag().equals("SP"));
	}
	
	
	private static boolean is时态(Node label){
		return label.getTag().equals("AS");
	}
	private static boolean is感叹(Node label){
		return label.getTag().equals("IJ");
	}
	private static boolean is限定(Node label){
		return label.getTag().equals("DT");
	}
	private static boolean isPSub(Node label){
		return label.getCore().ctbClass.startsWith("PRD");
	}
	public static String getDepClass(Node label){
//		if(isROOT(label))
//			return "根节点";
//		if(isP(label))
//			return "标点";
		if(isSUB(label))
			return "主语";
//		if(is时态(label))
//			return "时态";
		if(isOBJ(label))
			return "宾语";
		if(isOBJ2(label))
			return "宾语";
//		if(is语态(label))
//			return "语气";
//		if(is感叹(label))
//			return "感叹";
		if(is限定(label))
			return "定语";
//		if(is的字结构(label))
//			return "的字结构";
//		if(is得字结构(label))
//			return "得字结构";
//		if(is地字结构(label))
//			return "地字结构";
//		if(is同位语(label))
//			return "同位语";
		if(is并列(label))
			return "并列";	
		if(is并列2(label))
			return "并列";	
		if(isPMOD(label))
			return "介宾";
		if(is数量(label))
			return "数量";
		if(is量词(label))
			return "定语";
		if(is定语(label))
			return "定语";
		if(is状语(label))
			return "状语";
		if(is补语(label))
			return "补语";
//		if(is之字结构(label)){
//			return "之字结构";
//		}
		if(isNMOD(label))
			return "NMOD";		
		if(isVMOD(label))
			return "VMOD";
		if(isPSub(label)&&isAMOD(label))
			return"主语";
		if(isAMOD(label))
			return "AMOD";
		
		if(isSBAR(label))
			return "补语";	
		if(isD(label))
			return "定语";
		if(isDEP(label))
			return "DEP";

		return "修饰";
	}
}