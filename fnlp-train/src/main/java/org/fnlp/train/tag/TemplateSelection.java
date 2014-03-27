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

package org.fnlp.train.tag;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 使用 Sequential Floating Backward Search (SFBS) Algorithm 挑选模板
 * 
 * @author zliu
 * 
 */
public class TemplateSelection {

	public String[] Templates;
	boolean[] CurSet;
	boolean[] LeftSet;
	Map<Integer, Double> AccuracyRecords;
	Map<Integer, Double> RecoveryAccuracyRecords;
	Set<Integer> AlreadyAdded;
	Set<Integer> AlreadyRemoved;
	final int MinSize = 4;
	int Count = -1;

	public TemplateSelection() {
		AccuracyRecords = new HashMap<Integer, Double>();
		RecoveryAccuracyRecords = new HashMap<Integer, Double>();
		AlreadyAdded = new HashSet<Integer>();
		AlreadyRemoved = new HashSet<Integer>();
	}

	final String train = "/home/zliu/corpus/sighan2007/ctb/train_seg.txt";
	final String test = "/home/zliu/corpus/sighan2007/ctb/test_seg.txt";
	final String outputdir = "/home/zliu/corpus/testTemplate/segmodels/";

	public void RecoveryFrom(String strdir) {
		if (Templates == null) {
			System.err.println("Initialize templates frist!");
		}
		File dir = new File(strdir);
		if (!dir.isDirectory())
			return;
		File[] filelist = dir.listFiles();
		for (int i = 0; i < filelist.length; i++) {
			String filename = filelist[i].getName();
			if (!(filename.contains(".gz") || filename.contains(".process"))) {
				boolean[] template = readTemplate(Templates,
						filelist[i].getPath());
				System.out.println("Recovery from " + filename + " ...");
				System.out.print("[Template]\t");
				for (int k = 0; k < template.length; k++) {
					if (template[k])
						System.out.print("1");
					else
						System.out.print("0");
				}
				System.out.println();
				double accuracy;
				if (RecoveryAccuracyRecords
						.containsKey(BooleanSet2Integer(template))) {
					accuracy = RecoveryAccuracyRecords
							.get(BooleanSet2Integer(template));
				} else {
					accuracy = getAccuracy(filelist[i].getPath() + ".process");
					RecoveryAccuracyRecords.put(BooleanSet2Integer(template),
							accuracy);
				}
				int num = Integer.parseInt(filename.replace("template", ""));
				if (num > Count)
					Count = num;
				System.out.println("[Accuracy]\t" + accuracy);
				System.out.println("Recovery ended.");
			}
		}
		Count++;
	}

	// 从文件中读出一个特定的模板组合
	private boolean[] readTemplate(String[] Templates, String tfile) {
		boolean[] template = new boolean[Templates.length];
		try {
			BufferedReader br = new BufferedReader(new FileReader(tfile));
			String line;
			while ((line = br.readLine()) != null) {
				for (int i = 0; i < Templates.length; i++) {
					if (line.equals(Templates[i])) {
						template[i] = true;
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return template;
	}

	private void InitTemplates(String templatefile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(templatefile));
		String line;
		List<String> templatelist = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (!line.equals(""))
				templatelist.add(line);
		}
		Templates = templatelist.toArray(new String[templatelist.size()]);
		br.close();
	}

	private void WriteTemplates(String templatefile, boolean[] subset)
			throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new FileOutputStream(templatefile),
				true);
		for (int i = 0; i < subset.length; i++) {
			if (subset[i])
				out.println(Templates[i]);
		}
		// 输出特征
		out.println("%y[-1]%y[0]");
		out.close();
	}

	private double getAccuracy(String file) {
		double accuracy = 0.0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Test Accuracy:")) {
					accuracy = Double.parseDouble(line.replace(
							"Test Accuracy:", ""));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accuracy;
	}

	public double Evaluate(String[] Templates, boolean[] subset)
			throws Exception {
		int key = BooleanSet2Integer(subset);
		if (AccuracyRecords.containsKey(key)) {
			System.err.println("Already calculated.");
			return AccuracyRecords.get(key);
		}
		if (RecoveryAccuracyRecords.containsKey(key)) {
			double value = RecoveryAccuracyRecords.get(key);
			AccuracyRecords.put(key, value);
			System.err.println("Recovery from previous files.");
			return value;
		}
		System.out.println("Evaluating...");
		System.out.print("[Template" + Count + "]\t");
		for (int i = 0; i < subset.length; i++) {
			if (subset[i])
				System.out.print("1");
			else
				System.out.print("0");
		}
		System.out.println();
		double accuracy = Double.MIN_VALUE;
		String templatefile = outputdir + "template" + Count;
		String modelfile = templatefile + ".gz";
		WriteTemplates(templatefile, subset);
		addedTagger tagger = new addedTagger();
		tagger.setFile(templatefile, train, modelfile, test);
		tagger.setUseLoss(false);
		tagger.setIter(100);
		// 重定向
		PrintStream console = System.out;
		PrintStream out = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(templatefile + ".process")), true);
		System.setOut(out);
		tagger.train();
		accuracy = tagger.result();
		AccuracyRecords.put(key, accuracy);
		out.close();
		System.setOut(console);
		Count++;
		System.out.println("[Accuracy]\t" + accuracy);
		System.out.println("Evaluating ended.");
		System.gc();
		return accuracy;
	}

	private int BooleanSet2Integer(boolean[] booleanSet) {
		int intTemplate = 0;
		for (int i = 0; i < booleanSet.length; i++) {
			if (booleanSet[i])
				intTemplate ^= (0x01 << i);
		}
		return intTemplate;
	}

	private String Integer2Binary(int integer) {
		StringBuffer sb = new StringBuffer();
		int size = Templates.length;
		for (int i = 0; i < size; i++) {
			if ((integer & (0x01 << i)) != 0)
				sb.append("1");
			else
				sb.append("0");
		}
		return sb.toString();
	}

	private int SetSize(boolean[] set) {
		int count = 0;
		for (boolean b : set) {
			if (b)
				count++;
		}
		return count;
	}

	private int SetSize(int integer) {
		int count = 0;
		for (int i = 0; i < Integer.SIZE; i++) {
			if ((integer & (0x01 << i)) != 0)
				count++;
		}
		return count;
	}

	private double BestAccuracyInSize(int size) {
		double accuracy = Double.MIN_VALUE;
		Set<Integer> keys = AccuracyRecords.keySet();
		for (int key : keys) {
			if (SetSize(key) == size && AccuracyRecords.get(key) > accuracy) {
				accuracy = AccuracyRecords.get(key);
			}
		}
		return accuracy;
	}

	private int WorstFeatureByRemoving(boolean[] CurSet) throws Exception {
		int worst = -1;
		double maxaccuracy = -Double.MAX_VALUE;
		for (int i = 0; i < CurSet.length; i++) {
			if (CurSet[i]) {
				boolean[] TestSet = CurSet.clone();
				TestSet[i] = false;
				double accuracy = Evaluate(Templates, TestSet);
				if (accuracy > maxaccuracy) {
					maxaccuracy = accuracy;
					worst = i;
				}
			}
		}
		// System.out.println("[Worst]\t" + worst);
		return worst;
	}

	private int BestFeatureByAdding(boolean[] CurSet, boolean[] LeftSet)
			throws Exception {
		int best = -1;
		double maxaccuracy = Double.MIN_VALUE;
		for (int i = 0; i < LeftSet.length; i++) {
			if (LeftSet[i]) {
				boolean[] TestSet = CurSet.clone();
				TestSet[i] = true;
				double accuracy = Evaluate(Templates, TestSet);
				if (accuracy > maxaccuracy) {
					maxaccuracy = accuracy;
					best = i;
				}
			}
		}
		// System.out.println("[Best]\t" + best);
		return best;
	}

	private void printAccuracyRecords(Map<Integer, Double> AccuracyRecords) {
		List<Map.Entry<Integer, Double>> sortlist = new ArrayList<Map.Entry<Integer, Double>>(
				AccuracyRecords.entrySet());
		Collections.sort(sortlist,
				new Comparator<Map.Entry<Integer, Double>>() {
					@Override
					public int compare(Entry<Integer, Double> arg0,
							Entry<Integer, Double> arg1) {
						return arg1.getValue().compareTo(arg0.getValue());
					}
				});
		for (Map.Entry<Integer, Double> kv : sortlist) {
			System.out.println(Integer2Binary(kv.getKey()) + "\t"
					+ kv.getValue());
		}
	}

	private void printAccuracyRecords(String serfile)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				serfile));
		Map<Integer, Double> AccuracyRecords = (Map<Integer, Double>) ois
				.readObject();
		ois.close();
		printAccuracyRecords(AccuracyRecords);
	}

	private void printAccuracy(boolean[] curset) {
		Integer theInt = BooleanSet2Integer(curset);
		for (boolean b : curset) {
			if (b)
				System.out.print("1");
			else
				System.out.print("0");
		}
		System.out.println(" " + AccuracyRecords.get(theInt));
	}

	private void printResult(String dir) {
		RecoveryFrom(dir);
		printAccuracyRecords(RecoveryAccuracyRecords);
	}

	/**
	 * SFFS alg.
	 * 
	 * @param templatefile
	 * @throws Exception
	 */
	public void SFFS() throws Exception {
		int NumTemplate = Templates.length;
		CurSet = new boolean[NumTemplate];
		LeftSet = new boolean[NumTemplate];
		for (int i = 0; i < NumTemplate; i++) {
			CurSet[i] = false;
			LeftSet[i] = true;
		}
		Evaluate(Templates, CurSet);
		boolean continueDelete = false;
		while (SetSize(CurSet) <= NumTemplate) {
			if (!continueDelete) {
				Integer theInt = BooleanSet2Integer(CurSet);
				int best = BestFeatureByAdding(CurSet, LeftSet);
				if (best != -1) {
					System.out.println("Add Best " + best);
					AlreadyAdded.add(theInt);
					CurSet[best] = true;
					LeftSet[best] = false;
					printAccuracy(CurSet);
				}
			}
			Integer theInt = BooleanSet2Integer(CurSet);
			// 被删除过的集合就不再删除了
			if (AlreadyRemoved.contains(theInt)) {
				continueDelete = false;
				continue;
			} else {
				int worst = WorstFeatureByRemoving(CurSet);
				boolean[] TestSet = CurSet.clone();
				TestSet[worst] = false;
				double accuracy = Evaluate(Templates, TestSet);
				if (accuracy >= AccuracyRecords.get(theInt)) {
					System.out.println("Remove Worst " + worst);
					AlreadyRemoved.add(theInt);
					CurSet[worst] = false;
					LeftSet[worst] = true;
					printAccuracy(CurSet);
					if (SetSize(CurSet) == 0) {
						continueDelete = false;
					} else
						continueDelete = true;
				} else {
					continueDelete = false;
					continue;
				}
			}
		}
		printAccuracyRecords(AccuracyRecords);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				"/home/zliu/corpus/testTemplate/SFFS.ser"));
		oos.writeObject(AccuracyRecords);
		oos.close();

	}

	/**
	 * SFBS alg.
	 * 
	 * @param templatefile
	 * @throws Exception
	 */
	public void SFBS() throws Exception {
		int NumTemplate = Templates.length;
		CurSet = new boolean[NumTemplate];
		LeftSet = new boolean[NumTemplate];
		for (int i = 0; i < NumTemplate; i++) {
			CurSet[i] = true;
			LeftSet[i] = false;
		}
		Evaluate(Templates, CurSet);
		boolean continueAdding = false;
		while (SetSize(CurSet) >= MinSize) {
			if (!continueAdding) {
				Integer theInt = BooleanSet2Integer(CurSet);
				int worst = WorstFeatureByRemoving(CurSet);
				AlreadyRemoved.add(theInt);
				System.out.println("Remove Worst " + worst);
				CurSet[worst] = false;
				LeftSet[worst] = true;
				printAccuracy(CurSet);
			}
			Integer theInt = BooleanSet2Integer(CurSet);
			// 如果之前添加过，就不再继续添加防止死循环
			if (AlreadyAdded.contains(theInt)) {
				continueAdding = false;
				continue;
			} else {
				int best = BestFeatureByAdding(CurSet, LeftSet);
				boolean[] TestSet = CurSet.clone();
				TestSet[best] = true;
				double accuracy = Evaluate(Templates, TestSet);
				if (accuracy >= AccuracyRecords.get(theInt)) {
					System.out.println("Add Best " + best);
					AlreadyAdded.add(theInt);
					CurSet[best] = true;
					LeftSet[best] = false;
					printAccuracy(CurSet);
					if (SetSize(CurSet) == Templates.length) {
						continueAdding = false;
					} else
						continueAdding = true;
				} else {
					continueAdding = false;
					continue;
				}
			}
		}
		printAccuracyRecords(AccuracyRecords);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				"/home/zliu/corpus/testTemplate/SFBS.ser"));
		oos.writeObject(AccuracyRecords);
		oos.close();
	}

	public static void main(String[] args) throws Exception {
		TemplateSelection ts = new TemplateSelection();
		ts.InitTemplates("/home/zliu/corpus/testTemplate/allTemplates.txt");
		ts.RecoveryFrom("/home/zliu/corpus/testTemplate/segmodels/");
		// ts.SFBS();
		ts.SFFS();
		ts.printResult("/home/zliu/corpus/testTemplate/segmodels/");
	}

}