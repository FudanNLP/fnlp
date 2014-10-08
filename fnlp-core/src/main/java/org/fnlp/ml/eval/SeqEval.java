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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.fnlp.util.MyCollection;

/**
 * 统计 实验结果的Precision,recall 和 FB1值
 * @author fxx
 */
public class SeqEval {

	private  String STRPERCENT = "%";
	private  String STRLINE = "\n";
	private  String sep = ""; 

	/**
	 * 存放实体类型
	 */
	private static Set<String> entityType;

	/**
	 * 存放正确的实体的容器
	 */
	private ArrayList<LinkedList<Entity>> entityCs = new ArrayList<LinkedList<Entity>>();
	/**
	 * 	存放估计的实体的容器
	 */
	private ArrayList<LinkedList<Entity>> entityPs = new ArrayList<LinkedList<Entity>>();	
	/**
	 * 存放估计中正确实体的容器
	 */
	private ArrayList<LinkedList<Entity>>  entityCinPs = new ArrayList<LinkedList<Entity>> ();

	/**
	 * 词典
	 */
	HashSet<String> dict;
	private boolean latex = false;
	public boolean NoSegLabel = false;

	public SeqEval() {
		if(latex){
			STRPERCENT = "\\%";
			STRLINE = "\\\\\\hline\n";
			sep = "&"; 
		}
	}


	/**
	 * 读取评测结果文件，并输出到outputPath
	 * @param filePath			待评测文件路径
	 * @param outputPath		评测结果的输出路径
	 * @throws IOException 
	 */
	public void NeEvl(String outputPath) throws IOException{


		String res = "";
		res += calcByLength()+"\n";
		res += calcByType()+"\n";
		res += calcByOOV()+"\n";
		res += calcByCOOV()+"\n";
		res += calcByOOV2()+"\n";
		res += calcByOOVRate()+"\n";



		if(outputPath != null ){
			File outFile =new File(outputPath);
			Writer out=new OutputStreamWriter(new FileOutputStream(outFile));
			out.write(res);
			out.close();
		}

	}

	private String calcByLength() {


		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mpc = new TreeMap<Integer,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mp = new TreeMap<Integer,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mc = new TreeMap<Integer,Double>();	

		/**
		 * OOV
		 */
		Map<String,Double> oov = new TreeMap<String,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			for(Entity entity:cList){
				int len = entity.getEndIndex() - entity.getStartIndex()+1;
				adjust(mc, len,1.0);
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(!dict.contains(s)){
						adjust(oov, len, 1.0);
					}
				}
			}

			for(Entity entity:pList){
				int len = entity.getEndIndex() - entity.getStartIndex()+1;
				adjust(mp, len,1.0);
			}

			for(Entity entity:cpList){
				int len = entity.getEndIndex() - entity.getStartIndex()+1;
				adjust(mpc, len,1.0);
			}
		}



		return toString("Length", mpc, mp, mc, oov);
	}

	private void adjust(Map mc, Object key, double d) {
		if(mc.containsKey(key)){
			mc.put(key, (Double)mc.get(key)+d);
		}else{
			mc.put(key, d);
		}
	}

	private String calcByOOV2() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mpc = new TreeMap<Integer,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mp = new TreeMap<Integer,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mc = new TreeMap<Integer,Double>();	
		/**
		 * OOV
		 */
		Map<Integer,Double> oov = new TreeMap<Integer,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			for(Entity entity:cList){
				String type;
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(dict.contains(s)){
						type="INV";
						adjust(oov, type, 0.0);
					}else{
						type="OOV";
						adjust(oov, type, 1.0);
					}					
					adjust(mc, type, 1.0);
				}
			}

			for(Entity entity:pList){
				String type;
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(dict.contains(s)){
						type="INV";
					}else{
						type="OOV";
					}
					adjust(mp, type, 1.0);
				}
			}

			for(Entity entity:cpList){
				String type;
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(dict.contains(s)){
						type="INV";
					}else{
						type="OOV";
					}
					adjust(mpc, type, 1.0);
				}
			}	

		}


		return toString("INV/OOV",mpc, mp, mc, oov);
	}

	private String calcByOOV() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mpc = new TreeMap<Integer,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mp = new TreeMap<Integer,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mc = new TreeMap<Integer,Double>();	
		/**
		 * OOV
		 */
		Map<Integer,Double> oov = new TreeMap<Integer,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			ArrayList<String> oovs = findOOV(cList);			
			int num = oovs.size();
			//			int num = findContinousOOV(cList);

			adjust(mc, num, cList.size());
			adjust(oov, num, num);

			adjust(mp, num, pList.size());

			adjust(mpc, num, cpList.size());	

		}


		return toString("OOV",mpc, mp, mc, oov);
	}

	private String calcByCOOV() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mpc = new TreeMap<Integer,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mp = new TreeMap<Integer,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Integer,Double> mc = new TreeMap<Integer,Double>();	
		/**
		 * OOV
		 */
		Map<Integer,Double> oov = new TreeMap<Integer,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);


			int num = findContinousOOV(cList);

			adjust(mc, num, cList.size());
			adjust(oov, num, num);

			adjust(mp, num, pList.size());

			adjust(mpc, num, cpList.size());	

		}


		return toString("COOV",mpc, mp, mc, oov);
	}

	private String calcByOOVRate() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Double,Double> mpc = new TreeMap<Double,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<Double,Double> mp = new TreeMap<Double,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<Double,Double> mc = new TreeMap<Double,Double>();	
		/**
		 * OOV
		 */
		Map<Double,Double> oov = new TreeMap<Double,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			ArrayList<String> oovs = findOOV(cList);			
			int num = oovs.size();
			double num1;
			if(num==0)
				num1=0;
			else
				num1 = Math.ceil(num/(double)cList.size()*20)/20;

			adjust(mc, num1, cList.size());
			adjust(oov, num1, num);

			adjust(mp, num1, pList.size());

			adjust(mpc, num1, cpList.size());	

		}


		return toString("OOVRate",mpc, mp, mc, oov);
	}

	private String toString(String mark, Map mpc,
			Map mp, Map mc, Map oov) {
		//输出统计数据
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df1 = new DecimalFormat("0");
		StringBuffer strOutBuf = new StringBuffer();
		String strInfo = mark
				+ "\t" + sep + "\t" + "Precision"
				+ "\t" + sep + "\t" + "Recall"
				+ "\t" + sep + "\t" + "FB1" 
				+ "\t" + sep + "\t" + "PCount"  
				+ "\t" + sep + "\t" + "CCount" 
				+ "\t" + sep + "\t" + "Correct" 
				+ "\t" + sep + "\t" + "OOVRate"
				;
		strOutBuf.append(strInfo + STRLINE);
		for(Object key:mc.keySet()){
			Double oovv = (Double) oov.get(key);
			if(oovv==null)
				oovv =0.0;
			double oovrate = oovv/(Double)mc.get(key);
			if(mpc.containsKey(key) && mp.containsKey(key)){
				double pre = (Double) mpc.get(key)/(Double) mp.get(key);
				double recall = (Double)mpc.get(key)/(Double)mc.get(key);				
				double FB1 = (pre*recall*2)/(recall+pre);
				String str = key 
						+ "\t" + sep + "\t"  + df.format(pre*100).replaceAll("\\.00$", "") +STRPERCENT
						+ "\t\t" + sep + "\t" + df.format(recall*100).replaceAll("\\.00$", "") + STRPERCENT
						+ "\t" + sep + "\t" + df.format(FB1*100).replaceAll("\\.00$", "") +STRPERCENT 
						+ "\t" + sep + "\t" + df1.format(mp.get(key))
						+ "\t" + sep + "\t" + df1.format(mc.get(key))
						+ "\t" + sep + "\t" + df1.format(mpc.get(key))
						+ "\t" + sep + "\t" + df.format(oovrate*100).replaceAll("\\.00$", "")+STRPERCENT;
				;
				strOutBuf.append(str + STRLINE	);
			}else{
				String str = key 
						+ "\t" + sep + "\t" + 0 + "%\t"
						+ "\t" + sep + "\t" + 0 + STRPERCENT
						+ "\t" + sep + "\t" + 0 + STRPERCENT
						+ "\t" + sep + "\t" + 0
						+ "\t" + sep + "\t" + df1.format(mc.get(key))
						+ "\t" + sep + "\t" + 0+ STRPERCENT
						+ "\t" + sep + "\t" + df.format(oovrate*100).replaceAll("\\.00$", "") +STRPERCENT;
				;
				strOutBuf.append(str + STRLINE);
			}
		}

		System.out.println(strOutBuf.toString());
		return strOutBuf.toString();
	}

	private int findContinousOOV(LinkedList<Entity> cList) {
		ArrayList<String> oovs  = new ArrayList<String>();
		int num = 0;
		int max = 0;
		if(dict!=null&&dict.size()>0){
			for(Entity e: cList){
				String s = e.getEntityStr();
				if(!dict.contains(s)){
					num++;
					if(num>max)
						max=num;
				}
				else{
					num=0;
				}
			}
		}
		if(oovs.size()>11)
			System.out.println(oovs);
		return max;
	}


	private ArrayList<String> findOOV(LinkedList<Entity> cList) {
		ArrayList<String> oovs  = new ArrayList<String>();
		if(dict!=null&&dict.size()>0){
			for(Entity e: cList){
				String s = e.getEntityStr();
				if(!dict.contains(s))
					oovs.add(s);
			}
		}
		//		if(oovs.size()>11)
		//			System.out.println(oovs);
		return oovs;
	}

	public String calcByType() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mpc = new TreeMap<String,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mp = new TreeMap<String,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mc = new TreeMap<String,Double>();	

		/**
		 * OOV
		 */
		Map<String,Double> oov = new TreeMap<String,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			for(Entity entity:cList){
				String type = entity.getType();

				adjust(mc, type, 1.0);
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(!dict.contains(s)){
						adjust(oov, type, 1.0);
					}
				}
			}

			for(Entity entity:pList){
				String type = entity.getType();
				adjust(mp, type, 1.0);
			}

			for(Entity entity:cpList){
				String type = entity.getType();
				adjust(mpc, type, 1.0);
			}	
		}


		return toString("Type",mpc, mp, mc,oov);
	}
	
	public String calcByType2() {

		/**
		 * 估计的中正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mpc = new TreeMap<String,Double>();
		/**
		 * 估计的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mp = new TreeMap<String,Double>();		
		/**
		 * 正确的，key是字符串长度，value是这种长度的个数
		 */
		Map<String,Double> mc = new TreeMap<String,Double>();	

		/**
		 * OOV
		 */
		Map<String,Double> oov = new TreeMap<String,Double>();	

		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			for(Entity entity:cList){
				String type = entity.getType();

				adjust(mc, type, 1.0);
				adjust(mc, "all", 1.0);
				if(dict!=null&&dict.size()>0){					
					String s = entity.getEntityStr();
					if(!dict.contains(s)){
						adjust(oov, type, 1.0);
						adjust(oov, "all", 1.0);
					}
				}
			}

			for(Entity entity:pList){
				String type = entity.getType();
				adjust(mp, type, 1.0);
				adjust(mp, "all", 1.0);
			}

			for(Entity entity:cpList){
				String type = entity.getType();
				adjust(mpc, type, 1.0);
				adjust(mpc, "all", 1.0);
			}	
		}


		return toString("Type",mpc, mp, mc,oov);
	}



	/**
	 * 从reader中提取实体，存到相应的队列中，并统计固定长度实体的个数，存到相应的map中
	 * @param reader		结果文件的流
	 * @throws IOException 
	 */
	public void read(String filePath) throws IOException{


		String line;
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> markP = new ArrayList<String>();
		ArrayList<String> typeP = new ArrayList<String>();
		ArrayList<String> markC = new ArrayList<String>();
		ArrayList<String> typeC = new ArrayList<String>();
		if(filePath == null)
			return;
		File file = new File(filePath);
		BufferedReader reader = null;
		entityType = new HashSet<String>();

		//按行读取文件内容，一次读一整行
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

		//从文件中提取实体并存入队列中

		while ((line = reader.readLine()) != null) {
			if(line.equals("")){

				newextract(words, markP, typeP, markC, typeC);


			}else{
				//判断实体,实体开始的边界为B-***或者S-***，结束的边界为E-***或N（O）或空白字符或B-***
				//predict
				String[] toks = line.split("\\s+");
				
				int ci = 1;
				int cj=2;
				
				if(toks.length>3){//如果列数大于三，默认取最后两列
					ci=toks.length-2;
					cj=toks.length-1;
				}

				String[] marktype = getMarkType(toks[ci]);
				words.add(toks[0]);
				markP.add(marktype[0]);
				typeP.add(marktype[1]);
				entityType.add(marktype[1]);	

				//correct
				marktype = getMarkType(toks[cj]);
				markC.add(marktype[0]);
				typeC.add(marktype[1]);	
				entityType.add(marktype[1]);	

			}
		}
		reader.close();
		if(words.size()>0){
			newextract(words, markP, typeP, markC, typeC);
		}
		//从entityPs和entityCs提取正确估计的实体，存到entityCinPs，并更新mpc中的统计信息
		extractCInPEntity();
	}

	private void newextract(ArrayList<String> words, ArrayList<String> markP,
			ArrayList<String> typeP, ArrayList<String> markC,
			ArrayList<String> typeC) {
		LinkedList<Entity> entitylist1 = extract(words,markP,typeP);
		entityPs.add(entitylist1);
		LinkedList<Entity> entitylist2 = extract(words,markC,typeC);
		entityCs.add(entitylist2);

		words.clear();
		markP.clear();
		typeP.clear();
		markC.clear();
		typeC.clear();
	}


	private LinkedList<Entity> extract(ArrayList<String> words, ArrayList<String> marks,
			ArrayList<String> types) {

		int entityStartIndexC = -1;				//正确的实体的起始位置
		LinkedList<Entity> entitylist = new LinkedList<Entity>();

		//记录的是否是估计实体开始的标志
		boolean in = true;
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<words.size();i++){
			if(isStart(marks,types,i)){
				in = true;
				sb = new StringBuilder();
				entityStartIndexC = i;
			}
			if(in)
				sb.append(words.get(i));
			if(isEnd(marks, types, i)||isStart(marks, types, i+1)){
				if(!in){
					System.err.println("E");
				}
				in = false;
				Entity entity = new Entity(entityStartIndexC,i, sb.toString().trim());
				entity.setType(types.get(i));
				entitylist.add(entity);
			}
		}
		return entitylist;
	}

	private boolean isStart(ArrayList<String> marks, ArrayList<String> types,
			int i) {
		
		if(NoSegLabel)
			return true;

		boolean start = false;
		String prevMark;
		if(i==0)
			prevMark= "O";
		else
			prevMark = marks.get(i-1);
		String curMark = marks.get(i);

		String prevType;
		if(i==0)
			prevType= "";
		else
			prevType = types.get(i-1);
		String curType = types.get(i);

		if(curMark.equalsIgnoreCase("B")||curMark.equalsIgnoreCase("S"))
			start = true;
		else if(prevMark.equalsIgnoreCase("E")&&curMark.equalsIgnoreCase("E"))
			start = true;
		else if(prevMark.equalsIgnoreCase("E")&&curMark.equalsIgnoreCase("M"))
			start = true;
		else if(prevMark.equalsIgnoreCase("S")&&curMark.equalsIgnoreCase("M"))
			start = true;
		else if(prevMark.equalsIgnoreCase("S")&&curMark.equalsIgnoreCase("E"))
			start = true;
		else if(prevMark.equalsIgnoreCase("O")&&curMark.equalsIgnoreCase("E"))
			start = true;
		else if(prevMark.equalsIgnoreCase("O")&&curMark.equalsIgnoreCase("M"))
			start = true;
		else if(!curMark.equalsIgnoreCase("O")&&!curType.equalsIgnoreCase(prevType))
			start = true;


		return start;
	}

	private boolean isEnd(ArrayList<String> marks, ArrayList<String> types,
			int i) {

		boolean end = false;
		String nextMark;
		if(i==marks.size()-1)
			nextMark= "O";
		else
			nextMark = marks.get(i+1);

		String curMark = marks.get(i);

		String nextType;
		if(i==types.size()-1)
			nextType= "";
		else
			nextType = types.get(i+1);
		String curType = types.get(i);

		if(curMark.equalsIgnoreCase("E")||curMark.equalsIgnoreCase("S"))
			end = true;
		else if(nextMark.equalsIgnoreCase("O"))
			end = true;
		else if(nextMark.equalsIgnoreCase("B"))
			end = true;
		else if(nextMark.equalsIgnoreCase("S"))
			end = true;

		else if(!curType.equalsIgnoreCase(nextType))
			end = true;



		return end;
	}
	
	

	/**
	 * 得到标记类型，BMES-后面的标记
	 * @param label
	 * @return
	 */
	private String[] getMarkType(String label) {
		String[] types = new String[2];
		
		if(NoSegLabel){
			types[0] = "";
			types[1] = label;
			return types;
		}
		
		int idx = label.indexOf('-');
		if(idx!=-1){
			types[0] = label.substring(0,idx);
			types[1] = label.substring(idx+1);
		}else{
			types[0] = label;
			types[1] = "";
		}

		return types;
	}

	/**
	 * 提取在估计中正确的实体，存到entityCinPs中，并将长度个数统计信息存到mpc中
	 */
	public void extractCInPEntity(){

		//得到在predict中正确的Pc
		for(int i=0;i<entityPs.size();i++){
			LinkedList<Entity> entityCstmp = new LinkedList<Entity>();;
			LinkedList<Entity> entityps = entityPs.get(i);
			LinkedList<Entity> entitycs = entityCs.get(i);
			LinkedList<Entity> entitycinps = new LinkedList<Entity>();

			for(Entity entityp:entityps){
				while(!entitycs.isEmpty()){
					Entity entityc = entitycs.peek();
					if(entityp.equals(entityc)){
						entitycinps.add(entityp);
						entityCstmp.offer(entitycs.poll());
						break;
					}else if(entityp.getStartIndex() == entityc.getStartIndex()){
						entityCstmp.offer(entitycs.poll());
						break;
					}
					else if(entityp.getStartIndex() > entityc.getStartIndex()){
						entityCstmp.offer(entitycs.poll());
					}else{
						break;
					}
				}
			}
			entityCinPs.add(entitycinps);

			for(Entity entityp:entityCstmp){
				entitycs.offer(entityp);
			}
		}	

	}

	public HashSet<String> readOOV(String path) throws IOException{
		dict = new HashSet<String>();
		BufferedReader bfr;

		bfr = new BufferedReader(new InputStreamReader(new FileInputStream(path),"utf8"));

		String line = null;		

		while ((line = bfr.readLine()) != null) {
			if(line.length()==0)
				continue;			
			dict.add(line);			
		}
		bfr.close();
		return dict;
	}

	public static void main(String[] args) throws IOException{

		String filePath = null;
		String outputPath = null;
		if(args.length >0){
			if(args[0].equals("-h")){
				System.out.println("NeSatistic.jar 要评测的文件	[输出到文件]");
			}else{
				filePath = args[0];
			}
		}

		if(args.length == 2){
			outputPath = args[1];
		}

		filePath ="./paperdata/ctb6-seg/work/ctb_三列式结果_0.txt";
		String dictpath = "./paperdata/ctb6-seg/train-dict.txt";

		//		filePath = "./example-data/sequence/seq.res";

		//读取评测结果文件，并输出到outputPath
		SeqEval ne1;
		ne1 = new SeqEval();
		ne1.readOOV(dictpath);
		ne1.read(filePath);
		//		ne1.getWrongOOV("./paperdata/ctb6-seg/wrong-dict.txt");
		ne1.getRightOOV("./paperdata/ctb6-seg/right-pattern.txt");
		ne1.NeEvl(null);

		//		ne1 = new NESatistic();
		//		ne1.readOOV("./paperdata/exp-data/msr_training_words.utf8");
		//		ne1.read("./paperdata/exp-data/msr_三列式结果_0.txt");
		//		ne1.getWrongOOV("./paperdata/exp-data/msr_OOV-Wrong.txt");
		//		ne1.NeEvl(null);
		//		
		//		ne1 = new NESatistic();
		//		ne1.readOOV("./paperdata/exp-data/as_training_words.utf8");
		//		ne1.read("./paperdata/exp-data/as_三列式结果_0.txt");
		//		ne1.getWrongOOV("./paperdata/exp-data/as_OOV-Wrong.txt");
		//		ne1.NeEvl(null);
		//		
		//		ne1 = new NESatistic();
		//		ne1.readOOV("./paperdata/exp-data/pku_training_words.utf8");
		//		ne1.read("./paperdata/exp-data/pku_三列式结果_0.txt");
		//		ne1.getWrongOOV("./paperdata/exp-data/pku_OOV-Wrong.txt");
		//		ne1.NeEvl(null);
		//		
		//		ne1 = new NESatistic();
		//		ne1.readOOV("./paperdata/exp-data/cityu_training_words.utf8");
		//		ne1.read("./paperdata/exp-data/cityu_三列式结果_0.txt");
		//		ne1.getWrongOOV("./paperdata/exp-data/cityu_OOV-Wrong.txt");
		//		ne1.NeEvl(null);

	}


	public void getWrongOOV(String string) {

		if(dict==null||dict.size()==0)
			return;

		TreeSet<String> set = new TreeSet<String>();
		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);
			TreeSet<String> set1 = new TreeSet<String>();
			TreeSet<String> set2 = new TreeSet<String>();
			for(Entity entity:cList){							
				String s = entity.getEntityStr();
				if(dict.contains(s)){
					set1.add(s);
				}else{
					set1.add(s);
				}
			}
			for(Entity entity:pList){							
				String s = entity.getEntityStr();
				if(dict.contains(s)){
					set2.add(s);
				}else{
					set2.add(s);
				}
			}
			for(Entity entity:cpList){
				String s = entity.getEntityStr();
				set1.remove(s);
				set2.remove(s);
			}	
			//			set.addAll(set1);
			set.addAll(set2);
		}
		MyCollection.write(set, string);

	}


	private void getRightOOV(String string) {

		if(dict==null||dict.size()==0)
			return;

		TreeMap<String,String> set = new TreeMap<String,String>();
		for(int i=0;i<entityCs.size();i++){
			LinkedList<Entity>  cList =  entityCs.get(i);
			LinkedList<Entity>  pList =  entityPs.get(i);
			LinkedList<Entity>  cpList =  entityCinPs.get(i);

			for(Entity entity:cpList){
				String e = entity.getEntityStr();
				//				if(dict.contains(e))
				//					break;
				int idx = cList.indexOf(entity);
				String s= " ... ";
				if(idx!=-1){
					if(idx>0)
						s = cList.get(idx-1).getEntityStr() + s;
					if(idx<cList.size()-1)
						s = s+ cList.get(idx+1).getEntityStr();
				}
				adjust(set, s, 1);
			}	

		}
		List<Entry> sortedposFreq = MyCollection.sort(set);		
		MyCollection.write(sortedposFreq, string, true);

	}


}