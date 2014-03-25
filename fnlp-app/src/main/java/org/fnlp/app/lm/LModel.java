package org.fnlp.app.lm;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.lang.Math;

public abstract class LModel {

	/**
	 * ngram元语言模型
	 */
	protected int ngram;
	protected int[] totalarray;

	/**
	 * 所有词对的出现次数
	 */
	protected TObjectFloatHashMap<String> strCountMap;
	protected TObjectFloatHashMap<String>[] strCountMapArray;
	/**
	 * 总词量，包括重复
	 */
	protected int total = 0;
	int chartype = 0;
	/**
	 * 计算得分在 P ~ 0 之间认为符合模型
	 */
	protected double probabilityField; 

	/**
	 * 训练数据，得到每个词对的出现次数
	 * @param ngram
	 * @param inputFile训练数据文件
	 * @param outputFile
	 * @throws IOException
	 */
	public void buildStrCountMapArray(int ngram, String[] inputFile) throws IOException {
		this.ngram = ngram;
		System.out.println("read file ..."); 
		totalarray = new int[ngram+1];
		strCountMapArray = new TObjectFloatHashMap[ngram+1];
		for (int i=1; i<=ngram; ++i) {
			strCountMapArray[i] = new TObjectFloatHashMap<String>();
		}
		TreeSet<String> wordset = new TreeSet<String>();
		for(String file:inputFile){
			Scanner scanner = new Scanner(new FileInputStream(file), "utf-8");
			while(scanner.hasNext()) { 
				wordset.add(scanner.next());
			}
			scanner.close();
		}
		System.out.println("file size " + wordset.size());
		System.out.println("count words ...");
		total = 0;
		for (String str : wordset) {
			ArrayList<String> ng = toOneToNCharlist(str);
			total += ng.size();
			for(int j = 0; j < ng.size(); j++) {
				int length = ng.get(j).length();
				strCountMapArray[length].adjustOrPutValue(ng.get(j), 1.0f, 1.0f);
				++totalarray[length];
			}
		}
		int distinct = 0;
		for (int k=1; k<=ngram; ++k){
			distinct += strCountMapArray[k].size();
		}
		chartype = strCountMapArray[1].size();
		System.out.println("words number " + total);
		System.out.println("words number (distinct) " + distinct);
		System.out.println("chartype " + chartype);
	}

	/**
	 * @return 返回str的ngram元词组合列表
	 */
	public static ArrayList<String> toNCharlist(String str, int ngram) {
		ArrayList<String> al = new ArrayList<String>();
		for(int i = 0; i < str.length(); i++) {
			al.add(str.substring(i, i + ngram > str.length() ? str.length() : i + ngram));
		}
		return al;	
	}

	/**
	 * @return str的1~ngram元词组合列表
	 */
	public ArrayList<String> toOneToNCharlist(String str) {
		ArrayList<String> al = new ArrayList<String>();
		for(int i = 1; i <= ngram; i++)
			for(int j = 0; j + i <= str.length(); j++) {
				al.add(str.substring(j, j + i));
			}
		return al;	
	}

	/**
	 * 保存模型数据到saveModelFile
	 * @param saveModelFile
	 * @throws IOException
	 */
	public void save(String saveModelFile) throws IOException {
		System.out.println("save ...");
		ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new GZIPOutputStream(
						new FileOutputStream(saveModelFile))));
		out.writeObject(new Integer(ngram));
		out.writeObject(new Integer(chartype));
		out.writeObject(new Integer(total));
		out.writeObject(totalarray);
		out.writeObject(strCountMap);
		out.close();
		System.out.println("OK");
	}

	/**
	 * 加载模型数据文件
	 * @param loadModelFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void load(String modelFile) throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("加载N-Gram模型:" + modelFile);
		System.out.println("load ...");
		System.out.println(modelFile);
		setProbabilityField(7);
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new GZIPInputStream(new FileInputStream(modelFile))));
		ngram = (Integer) in.readObject(); System.out.println("ngram " + ngram);
		chartype = (Integer) in.readObject(); System.out.println("chartype " + chartype);
		total = (Integer) in.readObject(); System.out.println("total    " + total);
		totalarray = (int[]) in.readObject();
		for (int i = 1; i <= ngram; ++i)
			System.out.println("total[" + i + "] " + totalarray[i]);
		strCountMap = (TObjectFloatHashMap<String>) in.readObject();
		in.close();
		System.out.println("load ok");
	}

	//	/**
	//	 * 用模型文件loadModelFile来初始化
	//	 * @param loadModelFile
	//	 * @throws FileNotFoundException
	//	 * @throws IOException
	//	 * @throws ClassNotFoundException
	//	 */
	//	public static void init(String loadModelFile) throws FileNotFoundException, IOException, ClassNotFoundException {
	//		System.out.println("加载N-Gram模型:" + loadModelFile);
	//		load(loadModelFile);
	//		System.out.println("init ok");
	//	}

	/**
	 * 计算str的概率  P(A1A2.。。AN)<br>
	 * @param str
	 * @return
	 */
	public double computeP(String str) {
		if (str.length() <= ngram && strCountMap.contains(str))
			return Math.log(strCountMap.get(str) / totalarray[str.length()]);
		ArrayList<String> al = toNCharlist(str, ngram);
		double d = 0.0;
		for(int i = 0; i < al.size(); i++) {
			d += Math.log(getP(al.get(i)));
		}
		return d;
	}

	/**
	 * 利用模型文件中的概率信息来计算POI文件中各个POI出现的概率
	 * @param dataFile保存有POI文件
	 * @param modelFile模型文件
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void decide_P(String poiFile, String modelFile) throws IOException, ClassNotFoundException {
		System.out.println("参考一些例子，便于设定P值 (P值建议在ave到min之间)");
		System.out.println("read file ...");
		ArrayList<String> set = new ArrayList<String>();
		Scanner scanner = new Scanner(new FileInputStream(poiFile), "utf-8");
		while(scanner.hasNext()) {
			set.add(scanner.next());
		}
		scanner.close();
		load(modelFile);
		System.out.println("file size " + set.size());
		double min = 100000;
		double max = -100000;
		String mins = null, maxs = null;
		double ave = 0.0;
		int count = 0;
		int[] less = new int[20];
		for(int i = 0; i < set.size(); i++) {
			//System.out.println(i);
			String s = set.get(i);
			double d = compute(s);//ngram.computeP(s);
			//			System.out.println(s + " " + d);
			ave += d;
			count += s.length();
			if(d < min) {
				min = d;
				mins = s;
			}
			if(d > max) {
				max = d;
				maxs = s;
			}
			for(int k=0; k<less.length; k++){
				if(d < -(6 + (double)k/10))
					//				if(d < -k)
					less[k]++;
			}
		}
		System.out.println("ave " + ave / set.size());
		System.out.println("ave (函数getP的平均) " + ave / count);
		System.out.println("min " + min + " " + mins);
		System.out.println("max " + max + " " + maxs);
		for(int k=0; k<less.length; k++){
			System.out.println("得分小于-"+ (6 + (double)k/10) + "的比例     " + less[k] / (double)set.size());
			//			System.out.println("得分小于-"+ k + "的比例     " + less[k] / (double)set.size());
		}
	}


	/**
	 * 判断str是否为POI
	 * @param str
	 * @return
	 */
	public boolean contains(String str) {
		if(compute(str) >= probabilityField)
			return true;
		else
			return false;
	}

	/**
	 * str的概率/length
	 * @param str
	 * @return
	 */
	public double compute(String str) {
		double d = computeP(str);
		d = d / str.length(); //词有长有短,乘以一个系数
		return d;
	}

	/*
	 * 求测试文件中POI的困惑度
	 */
	public double computePerplexity(String testFile) throws FileNotFoundException{
		double perplexity = 0.0;
		ArrayList<String> list = new ArrayList<String>();
		Scanner scanner = new Scanner(new FileInputStream(testFile), "utf-8");
		while(scanner.hasNext()) {
			list.add(scanner.next());
		}
		scanner.close();
		TObjectFloatIterator<String> it = strCountMap.iterator();
		for (String str : list) {
			//perplexity += 1/Math.pow(Math.exp(computeP(str)), 1/str.length());
			//			System.out.println(str + "  " + compute(str));
			perplexity += Math.exp(-compute(str));
		}
		//		System.out.println(perplexity);
		System.out.println("list.size" + list.size());
		return perplexity/list.size();
	}

	public abstract double getP(String s);
	/*
	 * 观察decideP的概率分布，设置P的值，认为计算得分在 P ~ 0 之间认为符合模型
	 */
	public void setProbabilityField(double p){
		probabilityField = p;
	}

}
