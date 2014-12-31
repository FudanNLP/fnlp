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

package org.fnlp.nlp.pipe.templet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.util.MyFiles;


/**
 * 读入一组模板，每个模板对应一种类型的问题
 * @author xpqiu
 * @version 1.0
 * QuestionTemplateGroup
 */
public class RETemplateGroup implements Serializable {
	
	private static final long serialVersionUID = 3927868678845644573L;
	private static ChineseTrans tc = new ChineseTrans();
	String label;
	ArrayList<RETemplate> group;
	int count=0;
	private HashMap<String,Long> lastModTime;
	private String fileName;

	public RETemplateGroup(){
		group = new ArrayList<RETemplate>();
	}

	/**
	 * 构造函数
	 * @param str
	 */
	public RETemplateGroup(String str) {
		fileName = str;
		lastModTime = new HashMap<String, Long>();
		group = new ArrayList<RETemplate>();
		loadAll();
//		// 定期监视文件改动
//		Timer timer = new Timer(true);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				monitor();
//			}
//
//
//		}, new Date(System.currentTimeMillis() + 100000), 100000);
	}
	/**
	 * 查看模板文件是否改变，并重新读入
	 * 
	 * Jul 16, 2009
	 */
	private void monitor() {

		List<File> files  = MyFiles.getAllFiles(fileName,"templete.txt");

		try {
			for(int i=0;i<files.size();i++){

				Long newTime = files.get(i).lastModified();
				Long lastTime = lastModTime.get(files.get(i).toString());
				if(lastTime ==null || !lastTime.equals(newTime)){
					System.out.println("文件改变，重新读入模板");
					loadAll();
					break;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据句子得到匹配的模板
	 * @param str
	 * @return
	 */
	public List<RETemplate> getTemplate(String str){

		List<RETemplate> templates = new ArrayList<RETemplate>();
		Iterator<RETemplate> it = group.iterator();
		while(it.hasNext()){
			RETemplate qt = it.next();
			float w = qt.matches(str);
			if(w>0)
				templates.add(qt);
		}
		return templates;
	}

	

	/**
	 * 添加模板
	 * @param template
	 */
	public void add(RETemplate template) {
		//template.str2Reg(template.template);
		group.add(template);
	}

	/**
	 * 读入对应目录下所有模板
	 * Jul 16, 2009
	 */
	public synchronized void  loadAll(){
		group.clear();
		count=0;

		List<File> files = MyFiles.getAllFiles(fileName, ".txt");
		if(files==null||files.size()==0){
			System.err.println("模板为空");
		}
		try {
			for(int i=0;i<files.size();i++){
					read(files.get(i).toString());
					//记录文件修改时间
					lastModTime.put(files.get(i).toString(),files.get(i).lastModified());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("总模板数: " +count);
	}
	
	public void read(String fileName) throws Exception{
		Scanner scanner = new Scanner(new InputStreamReader (new FileInputStream(fileName),"utf-8"));

		boolean isNewType = true;
		RETemplate qt = null;
		while(scanner.hasNext()){

			String s = scanner.nextLine();
			if(s.equals("%%类别和样例"))
				break;
		}
		
		
		while(scanner.hasNext()){

			String s = scanner.nextLine();
			if(s.startsWith("##")||s.startsWith("%"))
				continue;
			Pattern p = Pattern.compile("\\((\\d+)\\)");
			Matcher m = p.matcher(s);
			int weight = 1;
			if(m.find()){
				String ws = m.group(1);
				weight = Integer.valueOf(ws);
			}
			
			s = s.replaceAll("\\(\\d+\\)", "").trim();		
			s = s.replaceAll(" ", "");
			if(s.trim().equals("")){
				isNewType = true;				
				continue;
			}	
			
			if(isNewType){	
				if(qt!=null)
					group.add(qt);
				qt = new RETemplate();
				
				qt.comment = s;
				isNewType = false;
				continue;
			}

			
			
			
			qt.addTemplate(s,weight);
			count++;
		}
		
		scanner.close();
	}

	/**
	 * 得到单个文件的模板
	 * @param fileName
	 * Jul 16, 2009
	 */
	public void load(String fileName){
		try {
			InputStreamReader read = new InputStreamReader (new FileInputStream(fileName),"utf-8");

			BufferedReader bin = new BufferedReader(read);

			RETemplate qt;
			qt = new RETemplate();
			qt.comment = fileName;
			StringBuilder sb;
			String line;			
			//		if(fileName.contains("歌曲 - 无修饰推荐"))
			//			errorLogger.debug("");

			//读入前缀、后缀
			String prefix="";
			String suffix="";
			while((line = bin.readLine()) != null){
				if(line.length()==0)
					break;
				if(line.charAt(0)=='@'){
					if(line.substring(1, 7).compareTo("PREFIX")==0)
						prefix = line.substring(8);
					if(line.substring(1, 7).compareTo("SUFFIX")==0)
						suffix = line.substring(8);
				}
			}
			//读入模板
			while((line = bin.readLine()) != null){
				if(line.length()==0)
					break;
				line = prefix + line + suffix;

				try {
					qt.addTemplate(line,1);
					count++;
				} catch (Exception e) {
					System.out.println(fileName);
					continue;
				}
			}

			group.add(qt);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	public static void main(String[] args){
		RETemplateGroup g = new RETemplateGroup("./train/intention.train.txt");

		g.loadAll();
		String str = "我要去公司";
		List<RETemplate> l = g.getTemplate(str);
		System.out.println(l);
		str = "我要去微软公司";
		l = g.getTemplate(str);
		System.out.println(l);
	}

	/**
	 * 处理问句的形式
	 * @param str
	 * @return
	 */
	private String normalise(String str) {
		str = str.replaceAll("\\s+", " ");
		str = tc.toSimp(str);
		str = ChineseTrans.toHalfWidth(str);
		return str;
	}

	/**
	 * @param templateFileName
	 */
	public void save(String templateFileName) {
		// TODO Auto-generated method stub

	}



}