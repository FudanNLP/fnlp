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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.fnlp.ml.types.InstanceSet;
/**
 * 将成分句法树库转换成依赖树库
 * @author jszhao
 * @version 1.0
 * @since FudanNLP 1.5
 */
public class DependentTreeProducter {
	private HashMap<String,String[][]>  ruleList;
	private boolean isCoordinative;
	public boolean debug = false;

	public DependentTreeProducter(){
		ruleList = new HashMap<String,String[][]>();
	}

	/**
	 * 判断是否为被字句或者把字句，并找到其位置。
	 * @param root
	 * @return
	 */
	private boolean isBae(Tree<Node> root){
		boolean isBae = false;
		if(!root.isLeaf()){
			for(int i=0;i<root.children.size();i++){
				if(root.getChild(i).getLabel().getTag().equals("SB")||
						root.getChild(i).getLabel().getTag().equals("LB")||
						root.getChild(i).getLabel().getTag().equals("BA")){
					isBae = true;
					root.BaeLocation = i;
					break;
				}
			}
		}
		return isBae;
	}

	/**
	 * 判断是否是并列关系并找到并列关系的核心索引
	 * @param root
	 * @return
	 */
	private int getCoordinativeCore(Tree<Node> root){
		int core = -1;
		boolean flag = true;
//		for(int i=0;i<root.getChildren().size()-1;i++){
//			if(	!root.getChild(i).getLabel().getTag().equals("PU")&&
//					(root.getChild(i+1).getLabel().getTag().equals("CC")
//							||root.getChild(i+1).getLabel().getData().equals("、"))){
//				if(flag){
//					isCoordinative = true;
//					core = i;
//					for(int j=core+1;j<root.getChildren().size();j++){
//						if(root.getChild(j).isLeaf()){
//							root.getChild(j).getLabel().isCoordinative = true;
//						}
//						else
//							root.getChild(j).isCoor = true;
//					}
//				}
//				if(root.getChild(i+1).getLabel().getTag().equals("CC"))
//					root.getChild(i+1).getLabel().setDepClass("关联");
//				root.CCLocation.add(i+1);
//				flag = false;
//			}
//		}
		
		for(int i=root.getChildren().size()-1;i>0;i--){
			if(	!root.getChild(i).getLabel().getTag().equals("PU")&&
					(root.getChild(i-1).getLabel().getTag().equals("CC")
							||root.getChild(i-1).getLabel().getData().equals("、"))){
				if(flag){
					isCoordinative = true;
					core = i;
					for(int j=0;j<core;j++){
						if(root.getChild(j).isLeaf()){
							root.getChild(j).getLabel().isCoordinative = true;
						}
						else
							root.getChild(j).isCoor = true;
					}
				}
				if(root.getChild(i-1).getLabel().getTag().equals("CC"))
					root.getChild(i-1).getLabel().setDepClass("关联");
				root.CCLocation.add(i-1);
				flag = false;
			}
		}

		return core;
	}
	/**
	 * 不能做核心词的判断
	 * @param root
	 * @return 核心索引
	 */
	private boolean isNotCore(Tree<Node> root){
		return (root.label.getTag().equals("PU")
				||root.label.getTag().equals("SP")
				||root.label.getTag().equals("IJ")
				||root.label.getTag().equals("FLR"));
	}
	/**
	 * 找到核心索引
	 * @param root
	 * @return 核心索引
	 */
	public int getCore(Tree<Node> root){		
		isCoordinative = false;		
		int core = -1;
		String[][] ruleWords = null;		
		if(debug){
			System.out.println(root.getLabel().getData());
			System.out.println(root.getLabel().getTag());
		}
		ruleWords = ruleList.get(root.getLabel().getTag());
		if(ruleWords!=null&&root.children.size()>0&&!root.isLeaf()){
			core = getCoordinativeCore(root);
			if(!isCoordinative){			
				for(int i=0;i<ruleWords.length;i++){
					if(ruleWords[i].length==1&&ruleWords[i][0].equals("r")){
						core = root.children.size()-1;
						//标点不能做核心词
						while(core>=0&&isNotCore(root.children.get(core))){
							core--;
						}
						if(core<0)
							core = 0;
						break;
					}
					else if(ruleWords[i].length==1&&ruleWords[i][0].equals("l")){
						core = 0;
						//标点不能做核心词
						while(core<root.children.size()&&isNotCore(root.children.get(core))){
							core++;
						}
						if(core>=root.children.size())
							core = root.children.size()-1;
						break;
					}
					else if(ruleWords[i][0].equals("l")){
						for(int m=1;m<ruleWords[i].length;m++){
							for(int n=0;n<root.children.size();n++){
								Tree<Node> ch = root.children.get(n);
								if(ch.getLabel().getTag().equals(ruleWords[i][m])){
									core = n;
									break;
								}
							}
							if(core!=-1)
								break;
						}
						if(core!=-1)
							break;
					}
					else if(ruleWords[i][0].equals("r")){
						for(int m=1;m<ruleWords[i].length;m++){
							for(int n=root.children.size()-1;n>=0;n--){
								if(root.children.get(n).getLabel().getTag().equals(ruleWords[i][m])){
									core = n;
									break;
								}
							}
							if(core!=-1)
								break;
						}
						if(core!=-1)
							break;
					}
				}
			}
		}
		if(core!=-1)
			return core;
		else
			return 0;
	}
	/**
	 * 读入规则
	 * @param rulePath 规则文件路径
	 * @throws IOException
	 */
	public void ruleRead(String rulePath) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(rulePath)), Charset.forName("GBK")));
		String line = null;
		String[][] value = null;
		while((line = br.readLine()) != null)	{
			String[] tokens = line.split("\t");
			String key = tokens[0];
			String[] words = tokens[1].split(";");	
			value = new String[words.length][];
			for(int j=0;j<words.length;j++){				
				value[j] = words[j].split("\\s");
			}
			ruleList.put(key,value);
		}
		br.close();
	}

	/**
	 * 找到依赖的核心叶子节点
	 * @param root 
	 * @return
	 */
	private Tree<Node> getRoot(Tree<Node> root){
		if(!root.isLeaf()){
			int core = getCore(root);
			if(root.children.get(core).isLeaf()){
				root.children.get(core).getLabel().isCoordinative =root.isCoor;
				return root.children.get(core);
			}
			else{
				root.children.get(core).isCoor = root.isCoor;
				return getRoot(root.children.get(core));
			}
		}
		return root;
	}

	/**
	 * 确定树的所有依赖（同层次中只有一个最高级核心词）
	 * @param root
	 */
	public void findDependent(Tree<Node> root){
		if(!root.isLeaf()){
			int core = getCore(root);
			for(int i=0;i<root.getChildren().size();i++){
				if(i==core)
					continue;
				else{	
					getRoot(root.getChild(i)).getLabel().setCore(getRoot(root.getChild(core)).getLabel());
				}
			}
			for(int j=0;j<root.getChildren().size();j++){
				findDependent(root.getChild(j));
			}
		}
	}
	/**
	 * 并列情况中连词依赖后面的词顿号依赖前面的词
	 * @param root
	 */
//	private void makeCCDependent(Tree<Node> root){
//		if(!root.isLeaf()){
//			if(root.CCLocation.size()!=0){
//				for(int i=0;i<root.CCLocation.size();i++){
//					if(root.getChild(root.CCLocation.get(i)).label.getTag().equals("CC")){
//						if(root.CCLocation.get(i)+1<root.getChildren().size()){
//							if(root.getChild(root.CCLocation.get(i)+1).isLeaf())
//								root.getChild(root.CCLocation.get(i)).label.setCore(root.getChild(root.CCLocation.get(i)+1).label);
//							else
//								root.getChild(root.CCLocation.get(i)).label.setCore(getRoot(root.getChild(root.CCLocation.get(i)+1)).label);
//						}
//					}
//					else if(root.getChild(root.CCLocation.get(i)).label.getData().equals("、")){
//						if(root.CCLocation.get(i)>0){
//							if(root.getChild(root.CCLocation.get(i)-1).isLeaf())
//								root.getChild(root.CCLocation.get(i)).label.setCore(root.getChild(root.CCLocation.get(i)-1).label);
//							else
//								root.getChild(root.CCLocation.get(i)).label.setCore(getRoot(root.getChild(root.CCLocation.get(i)-1)).label);
//
//						}
//					}
//				}
//			}
//		}
//	}
	
	
	/**
	 * 并列情况中连词依赖后面的词顿号依赖前面的词
	 * @param root
	 */
	private void makeCCDependent(Tree<Node> root){
		if(!root.isLeaf()){
			if(root.CCLocation.size()!=0){
				for(int i=0;i<root.CCLocation.size();i++){
					if(root.getChild(root.CCLocation.get(i)).label.getTag().equals("CC")){
						if(root.CCLocation.get(i)>0){
							if(root.getChild(root.CCLocation.get(i)-1).isLeaf())
								root.getChild(root.CCLocation.get(i)).label.setCore(root.getChild(root.CCLocation.get(i)-1).label);
							else
								root.getChild(root.CCLocation.get(i)).label.setCore(getRoot(root.getChild(root.CCLocation.get(i)-1)).label);
						}
					}
					else if(root.getChild(root.CCLocation.get(i)).label.getData().equals("、")){
						if(root.CCLocation.get(i)>0){
							if(root.getChild(root.CCLocation.get(i)-1).isLeaf())
								root.getChild(root.CCLocation.get(i)).label.setCore(root.getChild(root.CCLocation.get(i)-1).label);
							else
								root.getChild(root.CCLocation.get(i)).label.setCore(getRoot(root.getChild(root.CCLocation.get(i)-1)).label);

						}
					}
				}
			}
		}
	}
	
	
	/**
	 * 确定把字句和被字句的依赖关系（紧跟的名词和动词都要依赖被或把）
	 * @param root
	 */
	private void makeBaeDependent(Tree<Node> root){
		if(this.isBae(root)){
			if(root.BaeLocation<root.children.size()-1){
				if(!root.getChild(root.BaeLocation+1).isLeaf()){
					if(root.getChild(root.BaeLocation+1).getFirstChild().isLeaf()){
						root.getChild(root.BaeLocation+1).getFirstChild().label.setCore(root.getChild(root.BaeLocation).label);
						root.getChild(root.BaeLocation+1).getFirstChild().label.setDepClass("宾语");
					}
					else{
						getRoot(root.getChild(root.BaeLocation+1).getFirstChild()).label.setCore(root.getChild(root.BaeLocation).label);
						getRoot(root.getChild(root.BaeLocation+1).getFirstChild()).label.setDepClass("宾语");
					}
				}
			}
		}
	}
	/**
	 * 确定VCD结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVCDDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VCD")){
			root.getLastChild().getLabel().setDepClass("并列");
		}
	}
	/**
	 * 确定VRD结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVRDDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VRD")){
			root.getFirstChild().getLabel().setCore(root.getLastChild().getLabel().getCore());
			root.getLastChild().getLabel().setCore(root.getFirstChild().getLabel());
			root.getLastChild().getLabel().setDepClass("补语");
			root.getLastChild().getLabel().setTag("趋向词");
		}
	}
	/**
	 * 确定VSB结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVSBDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VSB")){
			root.getFirstChild().getLabel().setCore(root.getLastChild().getLabel().getCore());
			root.getLastChild().getLabel().setCore(root.getFirstChild().getLabel());
			root.getLastChild().getLabel().setDepClass("连动");
		}
	}
	/**
	 * 确定VCP结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVCPDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VCP")){
			root.getFirstChild().getLabel().setCore(root.getLastChild().getLabel().getCore());
			root.getLastChild().getLabel().setCore(root.getFirstChild().getLabel());
			root.getLastChild().getLabel().setDepClass("补语");
		}
	}
	/**
	 * 确定VPT结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVPTDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VPT")){
			if(root.children.size()>2){
				root.getChild(1).getLabel().setCore(root.getLastChild().getLabel());
				root.getChild(1).getLabel().setDepClass("状语");
			}
			root.getLastChild().getLabel().setDepClass("补语");
		}
	}
	/**
	 * 确定VNV结构的依赖及其依赖类型
	 * @param root
	 */
	private void makeVNVDependent(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(str!=null&&str.equals("VNV")){
			if(root.children.size()>2){
				root.getChild(1).getLabel().setDepClass("状语");
			}
			root.getFirstChild().getLabel().setDepClass("疑问连动");
		}
	}
	/**
	 * 判断VP树的孩子是否含有“得”字，并找到其位置。
	 * @param root
	 * @return
	 */
	private boolean isDe(Tree<Node> root){
		String str = root.getLabel().getTag();
		boolean isDe = false;
		if(!root.isLeaf()&&str!=null){
			if(str.equals("VP")){
				for(int i=0;i<root.children.size();i++){
					if(root.getChild(i).getLabel().getData().equals("得")){
						isDe = true;
						root.DeLocation = i;
						break;
					}
				}
			}
		}
		return isDe;
	}
	/**
	 * 确定得字句的依赖关系（紧跟的名词要和动词都要依赖被或把）
	 * @param root
	 */
	private void makeDeDependent(Tree<Node> root){
		if(this.isDe(root)){
			if(root.DeLocation<root.children.size()-1){			
				if(root.getChild(root.DeLocation+1).isLeaf()){
					root.getChild(root.DeLocation+1).label.setCore(root.getChild(root.DeLocation).label);
				}
				else
					getRoot(root.getChild(root.DeLocation+1)).label.setCore(root.getChild(root.DeLocation).label);
			}
		}
	}
	/**
	 * 确定ADJP的核心节点的依赖关系为定语
	 * @param root
	 */
	private void makeADJPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(!root.isLeaf()&&str!=null){
			if(str.equals("ADJP")){
				getRoot(root).label.setDepClass("定语");
			}
		}
	}
	/**
	 * 确定CP的核心节点的依赖关系为定语
	 * @param root
	 */
	private void makeCPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(!root.isLeaf()&&str!=null){
			if(str.equals("CP")){
				getRoot(root).label.setDepClass("定语");
			}
		}
	}

	/**
	 * 确定ADVP的核心节点的依赖关系为状语
	 * @param root
	 */
	private void makeADVPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(!root.isLeaf()&&str!=null){
			if(str.equals("ADVP")){
				getRoot(root).label.setDepClass("状语");
			}
		}
	}
	/**
	 * 确定DVP的核心节点的依赖关系为状语
	 * @param root
	 */
	private void makeDVPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(!root.isLeaf()&&str!=null){
			if(str.equals("DVP")){
				getRoot(root).label.setDepClass("状语");
			}
		}
	}
	/**
	 * 确定DP的核心节点的依赖关系为定语
	 * @param root
	 */
	private void makeDPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		if(!root.isLeaf()&&str!=null){
			if(str.equals("DP")){
				getRoot(root).label.setDepClass("定语");
			}
		}
	}
	/**
	 * 确定DNP的核心节点的依赖关系为状语
	 * @param root
	 */
	private void makeDNPClass(Tree<Node> root){
		String str = root.getLabel().getTag();
		String ctbClass = root.getLabel().ctbClass;
		if(!root.isLeaf()&&str!=null){
			if(str.equals("DNP")&&ctbClass.contains("PRD")){
				getRoot(root).label.setDepClass("定语");
			}else if(str.equals("DNP")){
				getRoot(root).label.setDepClass("定语");
			}
		}
	}
	/**
	 * 通过ctb标注来确定依赖类型
	 * @param root
	 */
	private void makeCtbClass(Tree<Node> root){
		String str = root.getLabel().ctbClass;
		if(!root.isLeaf()&&str!=null){
			if(str.startsWith("SBJ")){
				if(getRoot(root).label.getDepClass()!=null&&
						!getRoot(root).label.getDepClass().equals("宾语"))
					getRoot(root).label.setDepClass("主语");
			}
			else if(str.startsWith("OBJ"))
				getRoot(root).label.setDepClass("宾语");
			else if(str.startsWith("ADV"))
				getRoot(root).label.setDepClass("状语");
			else if(str.startsWith("EXT"))
				getRoot(root).label.setDepClass("补语");
		}
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
	private static boolean is标点(Node label){
		return label.getTag().equals("PU");
	}
	/**
	 * 最先确定的依赖关系
	 * @param root
	 */
	private void makeFirstClass(Tree<Node> root){
		if(root.label.getCore()!=null){
			if(root.label.getCore().getId() == 0)
				root.label.setDepClass("核心词");
			else if(is标点(root.label))
				root.label.setDepClass("标点");
			else if(is语态(root.label))
				root.label.setDepClass("语态");
			else if(is时态(root.label))
				root.label.setDepClass("时态");
			else if(is感叹(root.label))
				root.label.setDepClass("感叹");
			else if(is得字结构(root.label))
				root.label.setDepClass("得字结构");
			else if(is的字结构(root.label))
				root.label.setDepClass("的字结构");
			else if(is地字结构(root.label))
				root.label.setDepClass("地字结构");
			else if(is之字结构(root.label))
				root.label.setDepClass("的字结构");
			else if(root.label.getCore().getTag().equals("P"))
				root.label.setDepClass("介宾");

		}
	}
	/**
	 * 确定树的所有依赖（同层次中不仅仅只有一个最高级核心词，还有次级的核心词）
	 * @param root
	 */
	public void makeFinalDependent(Tree<Node> root){	
		makeCCDependent(root);
		makeDeDependent(root);

		makeVCDDependent(root);
		makeVRDDependent(root);
		makeVSBDependent(root);
		makeVCPDependent(root);
		makeVPTDependent(root);
		makeVNVDependent(root);
		makeCtbClass(root);
		if(getRoot(root).label.getDepClass()==null){
			makeADJPClass(root);
			makeADVPClass(root);
			makeDVPClass(root);
			makeDNPClass(root);
			makeCPClass(root);
			makeDPClass(root);
		}
		makeBaeDependent(root);
		makeFirstClass(root);
		for(int j=0;j<root.getChildren().size();j++){
			makeFinalDependent(root.getChild(j));
		}		
	}
	/**
	 * 确定ctb标记的依赖类型
	 * @param node
	 * @return
	 */
	private String CTBDeClass(Node node){
		String depClass = null;
		if(node.ctbClass.startsWith("SBJ"))
			depClass = "主语";
		else if(node.ctbClass.startsWith("OBJ"))
			depClass = "宾语";
		else if(node.ctbClass.startsWith("ADV"))
			depClass = "状语";
		else if(node.ctbClass.startsWith("EXT"))
			depClass = "补语";
		return depClass;
	}
	/**
	 * 确定依赖类型
	 * @param node
	 * @return
	 */
	private String getDepClass(Node node){
		String depClass = null;
		if(debug )
			System.out.println(node.getData());
		if(node.getDepClass()!=null){
			depClass = node.getDepClass();
			if(debug )
				System.out.println("dd:"+depClass);
		}

		else{
			depClass = DepClassProducter.getDepClass(node);
			if(debug ){
				System.out.println("father: "+node.getCore());
				System.out.println("dd2:"+depClass);
			}
		}

		return depClass;
	}
	/**
	 * 将转换的依赖树写入文件
	 * @param ins 样本集合
	 * @param writePath 写入文件的路径
	 * @param rulePath 规则文件的路径
	 * @throws IOException
	 */
	public  void write(InstanceSet ins,String writePath,String rulePath) throws IOException{
		ruleRead(rulePath);
		File f = new File(writePath);
		Writer wr = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
		for(int i=0;i<ins.size();i++){
			System.out.println(i);
			Tree<Node> tr = (Tree<Node>)(ins.get(i).getData());
			clean(tr);
			findDependent(tr);
			makeFinalDependent(tr);
			Iterator it = tr.iterator();
			while(it.hasNext()){
				Tree<Node> te = (Tree<Node>) it.next();
				
				if(te.isLeaf()&&te.getLabel().getId()>0)
					//			if(te.getLabel().getCore().getId()!=-1)
					wr.write(te.getLabel().getId()+"\t"+te.getLabel().getData().trim()+"\t"+te.getLabel().getTag()+"\t"+te.getLabel().getCore().getId()+"\t"+this.getDepClass(te.getLabel())+"\n");
				//			else
				//				wr.write(te.getLabel().getId()+"\t"+te.getLabel().getData()+"\t"+te.getLabel().getTag()+"\t"+te.getLabel().getCore().getCore().getId()+"\t"+this.getDepClass(te.getLabel().getCore())+"\n");

			}
			wr.write("\n");
		}
		wr.close();
		System.out.print("已写入"+ins.size()+"棵依赖树。");
	}

	private void clean(Tree<Node> te) {

		boolean dirty = true;
		while(dirty){
			dirty = false;
			for(int i=0;i<te.children.size();i++)	{
				Tree<Node> child = te.children.get(i);					
				if(child.isLeaf()){
					if(debug)
					System.out.print(child);
					if(child.getLabel().getId()==-1){
						te.children.remove(i);
						i--;
						if(debug)
						System.out.print("Del:" +child);
						dirty = true;
					}
				}
				else{
//					if(child.getLabel().getTag().equals("FRAG")){
//						te.children.remove(i);
//						i--;
//						if(debug)
//						System.out.print("Del:" +child);
//						dirty = true;
//						continue;
//					}
					if(child.getLabel().getData().length()==0&&child.children.size()==0){
						te.children.remove(i);
						i--;
						if(debug)
						System.out.print("Del:" +child);
						dirty = true;
						continue;
					}
					int cnum = child.children.size();
					clean(child);
					int newcnum = child.children.size();
					if(newcnum!=cnum){
						dirty = true;
					}

				}
			}
		}

	}





}