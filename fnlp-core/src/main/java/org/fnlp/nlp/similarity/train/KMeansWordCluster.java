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

package org.fnlp.nlp.similarity.train;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.ml.types.alphabet.LabelAlphabet;
import org.fnlp.ml.types.sv.HashSparseVector;
import org.fnlp.nlp.cn.ChineseTrans;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.hash.TIntFloatHashMap;

class TrainInstance implements Serializable {
	private static final long serialVersionUID = 1467092492463327579L;

    private String key;
    private HashSparseVector vector;

    public TrainInstance(String key, HashSparseVector vector) {
        this.key = key;
        this.vector = vector;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the vector
     */
    public HashSparseVector getVector() {
        return vector;
    }

    /**
     * @param vector the vector to set
     */
    public void setVector(HashSparseVector vector) {
        this.vector = vector;
    }
}

public class KMeansWordCluster implements Serializable {
	private static final long serialVersionUID = 1467092492463327579L;

    private LabelAlphabet alphabet = new LabelAlphabet();
    private HashMap<String, ArrayList<HashSparseVector>> trainData = new HashMap<String, ArrayList<HashSparseVector>>();
    private ArrayList<int[]> template = new ArrayList<int[]>();
    private int longestTemplate;
    private HashMap<Integer, ArrayList<String>> classOri = new HashMap<Integer, ArrayList<String>>();
    private HashMap<String, ArrayList<Integer>> classPerString = new HashMap<String, ArrayList<Integer>>();

    private ArrayList<HashSparseVector> classCenter = new ArrayList<HashSparseVector>();
    private ArrayList<Integer> classCount = new ArrayList<Integer>();
    private ArrayList<Float> baseDistList = new ArrayList<Float>();

    private String trainPath;

    public KMeansWordCluster(String templatePath, String dataPath,
            String classPath) {
        this.trainPath = dataPath;
        try {
            readTemplete(templatePath);
            readClass(classPath);
            initCluster();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KMeansWordCluster(String alphabetPath, String classCenterPath, String templatePath, String classPath) throws Exception {
        readTemplete(templatePath);
        readClass(classPath);
        LabelAlphabet alphabetRead = (LabelAlphabet)loadObject(alphabetPath);
        @SuppressWarnings("unchecked")
        ArrayList<HashSparseVector> classCenterRead = (ArrayList<HashSparseVector>)loadObject(classCenterPath);
        setAlphabet(alphabetRead);
        setClassCenter(classCenterRead);
        addClassCount();
        initBaseDist();
    }

    /**
     * @return the alphabet
     */
    public LabelAlphabet getAlphabet() {
        return alphabet;
    }

    /**
     * @param alphabet the alphabet to set
     */
    public void setAlphabet(LabelAlphabet alphabet) {
        this.alphabet = alphabet;
    }

    /**
     * @return the classCenter
     */
    public ArrayList<HashSparseVector> getClassCenter() {
        return classCenter;
    }

    /**
     * @param classCenter the classCenter to set
     */
    public void setClassCenter(ArrayList<HashSparseVector> classCenter) {
        this.classCenter = classCenter;
    }

    private void readClass(String path) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String s;
        int n = 0;
        while ((s = br.readLine()) != null) {
            String[] allc = s.split("\\s+");
            add2Class(allc, n);
            n++;
        }
        br.close();
        System.out.println("Finish load class!");
    }

    private void add2Class(String[] allc, int n) {
        for (String s : allc) {
            add2ClassOri(n, s);
            add2ClassPerString(s, n);
        }
    }

    void readData(String path) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String s;
        int n = 0;
        while ((s = br.readLine()) != null) {
            genFeatures(s);
            printTerminal(n, 10000, "raad");
            n++;
        }
        br.close();
        System.out.println("Finish load training data!");
    }

    private ArrayList<TrainInstance> genFeatures(String s) {
        ArrayList<TrainInstance> trainInstancelist = new ArrayList<TrainInstance>();
        String[] seq = string2StringSeq(s);
        for (int i = longestTemplate; i < longestTemplate + s.length(); i++) {
            String currentString = seq[i];
            int[] feaId = new int[template.size()];
            for (int j = 0; j < template.size(); j++) {
                int[] fea = template.get(j);
                String eleFea = j + ":" + perFea(i, seq, fea);
                int id = alphabet.lookupIndex(eleFea);
                feaId[j] = id;
            }
            HashSparseVector hsvector = new HashSparseVector();
            hsvector.put(feaId, 1.0f);
            trainInstancelist.add(new TrainInstance(currentString, hsvector));
        }
        return trainInstancelist;
    }

    protected void add2Data(String str, HashSparseVector hsvector) {
        if (trainData.containsKey(str)) {
            ArrayList<HashSparseVector> list = trainData.get(str);
            list.add(hsvector);
        } else {
            ArrayList<HashSparseVector> list = new ArrayList<HashSparseVector>();
            list.add(hsvector);
            trainData.put(str, list);
        }
    }

    private void add2ClassOri(int id, String chac) {
        if (classOri.containsKey(id)) {
            ArrayList<String> list = classOri.get(id);
            list.add(chac);
        } else {
            ArrayList<String> list = new ArrayList<String>();
            list.add(chac);
            classOri.put(id, list);
        }
    }

    private void add2ClassPerString(String chac, int id) {
        if (classPerString.containsKey(chac)) {
            ArrayList<Integer> list = classPerString.get(chac);
            list.add(id);
        } else {
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(id);
            classPerString.put(chac, list);
        }
    }

    private String perFea(int seqId, String[] seq, int[] fea) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fea.length; i++) {
            int id = fea[i];
            sb.append(seq[seqId + id]);
        }
        return sb.toString();
    }

    String perFea(int seqId, String s, int[] fea) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fea.length; i++) {
            int id = fea[i];
            sb.append(s.charAt(seqId + id));
        }
        return sb.toString();
    }

    private String[] string2StringSeq(String s) {
        int length = longestTemplate * 2 + s.length();
        String[] seq = new String[length];
        for (int i = 0; i < longestTemplate; i++) {
            seq[i] = "Begin" + i;
            seq[length-i-1] = "End" + i;
        }
        for (int i = 0; i < s.length(); i++)
            seq[i+longestTemplate] = String.valueOf(s.charAt(i));
        return seq;
    }

    private void readTemplete(String path) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), "UTF-8"));
        String s;
        while ((s = br.readLine()) != null) {
            String[] str = s.split(",");
            int[] fea = new int[str.length];
            for (int i = 0; i < fea.length; i++) {
                int id = 0;
                try {
                    id = Integer.parseInt(str[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fea[i] = id;
            }
            template.add(fea);
        }
        setLongestTemplate();
        br.close();
        System.out.println("Finish load template!");
    }

    private void setLongestTemplate() {
        int n = 0;
        for (int[] fea : template) {
            for (int i : fea) {
                if (Math.abs(i) > n)
                    n = Math.abs(i);
            }
        }
        longestTemplate = n;
    }

    private void initCluster() throws IOException {
        initVector();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(trainPath), "UTF-8"));
        String s;
        int n = 0;
        while ((s = br.readLine()) != null) {
            ArrayList<TrainInstance> trainData = genFeatures(s);
            initAddInstanceList(trainData);
            printTerminal(n, 10000, "init line");
            n++;
        }
        br.close();
        normalClassCenter();
        initBaseDist();
    }

    private void initVector() {
        for (int i = 0; i < classOri.size(); i++) {
            HashSparseVector hsvector = new HashSparseVector();
            classCenter.add(hsvector);
            classCount.add(0);
        }
        System.out.println("All cluster centers have been created!");
    }

    private void addClassCount() {
        for (int i = 0; i < classOri.size(); i++)
            classCount.add(1);
    }

    private void initAddInstanceList(ArrayList<TrainInstance> trainData) {
        for (TrainInstance ele : trainData) { 
            initAddInstance(ele);
        }
    }

    private void initAddInstance(TrainInstance instance) {
        String key = instance.getKey();
        HashSparseVector vector = instance.getVector();
        ArrayList<Integer> classNum = classPerString.get(key);
        if (classNum == null)
            return;
        for (int n : classNum) {
            int count = classCount.get(n);
            classCount.set(n, count+1);
            HashSparseVector vectorcenter = classCenter.get(n);
            vectorcenter.plus(vector);
        }
    }

    private void normalClassCenter() {
        for (int i = 0; i < classOri.size(); i++) {
            HashSparseVector vector = classCenter.get(i);
            int count = classCount.get(i);
            vector.scaleDivide(count);
            classCount.set(i, 1);
        }
    }

    protected void initPerClassCenter(int classNum) {
        ArrayList<String> stringlist = classOri.get(classNum);
        HashSparseVector hsvector = new HashSparseVector();
        int divide = 0;
        for (String s : stringlist) {
            ArrayList<HashSparseVector> vectorlist = trainData.get(s);
            if (vectorlist == null)
                continue;
            divide += vectorlist.size();
            for (HashSparseVector vector : vectorlist)
                hsvector.plus(vector);
        }
        hsvector.scaleDivide((float) divide);
        classCenter.add(hsvector);
        classCount.add(1);
    }

    public void cluster() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(trainPath), "UTF-8"));
        String s;
        int n = 0;
        while ((s = br.readLine()) != null) {
            ArrayList<TrainInstance> trainData = genFeatures(s);
            clusterList(trainData);
            printTerminal(n, 10000, "cluster line");
            if ((n+1) % 10000 == 0)
                saveObject("tmpdata/classCenterTemp", classCenter);
            n++;
        }
        br.close();
        updateAverageCenter();
    }

    private void updateAverageCenter() {
        for (int i = 0; i < classCount.size(); i++) {
            HashSparseVector center = classCenter.get(i);
            int count = classCount.get(i);
            center.scaleDivide((float) count);
        }
    }

    private void clusterList(ArrayList<TrainInstance> trainData) {
        for (TrainInstance ele : trainData) {
            String key = ele.getKey();
            HashSparseVector vector = ele.getVector();
            if (!classPerString.containsKey(key))
                continue;
            int minid = minClass(key, vector);
            updateCenter(minid, vector);
        }
    }

    private int minClass(TrainInstance instance) {
        return minClass(instance.getKey(), instance.getVector());
    }

    private int minClass(String key, HashSparseVector vector) {
        float min = Float.MAX_VALUE;
        int classid = 0;
        ArrayList<Integer> classlist = classPerString.get(key);
        if (classlist == null)
            return -1;
        for (Integer n : classlist) {
            float base = baseDistList.get(n);
            float distance = distanceEuclidean(n, vector, base);
            if (distance < min) {
                min = distance;
                classid = n;
            }
        }
        return classid;
    }

    private float distanceEuclidean(int n, HashSparseVector sv, float baseDistance) {
        HashSparseVector center = classCenter.get(n);
        int count = classCount.get(n);
        float dist = baseDistance / (count * count);
        TIntFloatHashMap data = center.data;
        TIntFloatIterator it = sv.data.iterator();
        while (it.hasNext()) {
            it.advance();
            int key = it.key();
            if (!data.containsKey(key)) {
                dist += it.value() * it.value();
            }
            else {
                float temp = data.get(key) / count;
                dist -= temp * temp;
                dist += (it.value() - temp) * (it.value() - temp);
            }
        }
        return dist;
    }

    float distanceEuclidean(HashSparseVector sv1 ,HashSparseVector sv2) {
        float dist = 0.0f;
        TIntFloatIterator it1 = sv1.data.iterator();
        TIntFloatIterator it2 = sv2.data.iterator();
        if (it1.hasNext() && it2.hasNext()) {
            it1.advance();
            it2.advance();
        }
        while (it1.hasNext() && it2.hasNext()) {
            if(it1.key()<it2.key()){
                dist += it1.value()*it1.value();
                it1.advance();
            }else if(it1.key()>it2.key()){
                dist += it2.value()*it2.value();
                it2.advance();
            }else{
                float t = it1.value() - it2.value();
                dist += t*t;
                it1.advance();
                it2.advance();
            }
        }
        while (it1.hasNext()) {
            it1.advance();
            dist += it1.value() * it1.value();
        }
        while (it2.hasNext()) {
            it2.advance();
            dist += it2.value() * it2.value();
        }
        return dist;
    }

    private void updateCenter(int classid, HashSparseVector vector) {
        int count = classCount.get(classid);
        classCount.set(classid, count+1);
        HashSparseVector vectorcenter = classCenter.get(classid);
        updateBaseDist(classid, vector);
        vectorcenter.plus(vector);
//        vectorcenter.scaleDivide((float)count / (float)(count+1));
    }

    private void updateBaseDist(int classid, HashSparseVector vector) {
        float base = baseDistList.get(classid);
        TIntFloatHashMap center = classCenter.get(classid).data;
        TIntFloatIterator it =  vector.data.iterator();
        while (it.hasNext()) {
            it.advance();
            if (!center.containsKey(it.key())) {
                base += it.value() * it.value();
            }
            else {
                float temp = center.get(it.key());
                base -= temp * temp;
                base += (it.value() - temp) * (it.value() - temp);
            }
        }
        baseDistList.set(classid, base);
    }

    private float getBaseDist(int classid) {
        float base = 0.0f;
        TIntFloatIterator it = classCenter.get(classid).data.iterator();
        while (it.hasNext()) {
            it.advance();
            base += it.value() * it.value();
        }
        return base;
    }

    private void initBaseDist() {
        for (int i = 0; i < classCenter.size(); i++) {
            float base = getBaseDist(i);
            baseDistList.add(base);
        }
        System.out.println("Finish init base distance list");
    }

    protected Object loadObject(String path) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                new GZIPInputStream(new FileInputStream(path))));
        Object obj = in.readObject();
        in.close();
        return obj;
    }

    protected void saveObject(String path, Object obj) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(path))));
        out.writeObject(obj);
        out.close();
    }

    private void printTerminal(int n, int divide, String s) {
        if ((n + 1) % divide == 0)
            System.out.println(s + " " + (n + 1));
    }

    public int classifier(String s) {
    	
        TrainInstance instance = genFeaForClassifier(s);
        return minClass(instance);
    }

    private TrainInstance genFeaForClassifier(String s) {
        int[] feaId = new int[template.size()];
        String[] seqFea = string2StringSeqWithBE(s);
        for (int i = 0; i < template.size(); i++) {
            int[] fea = template.get(i);
            String eleFea = i + ":" + perFea(1, seqFea, fea);
            int id  = alphabet.lookupIndex(eleFea);
            feaId[i] = id;
        }
        HashSparseVector hsvector = new HashSparseVector();
        hsvector.put(feaId, 1.0f);
        return new TrainInstance(seqFea[1], hsvector);
    }

    private String[] string2StringSeqWithBE(String s) {
        String[] seq = new String[3];
        if (s.startsWith("Begin0")) {
            seq[0] = "Begin0";
            seq[1] = String.valueOf(s.charAt(6));
            seq[2] = String.valueOf(s.charAt(7));
        }
        else if (s.endsWith("End0")) {
            seq[0] = String.valueOf(s.charAt(0));
            seq[1] = String.valueOf(s.charAt(1));
            seq[2] = "End0";
        }
        else 
            for (int i = 0; i < s.length(); i++)
                seq[i] = String.valueOf(s.charAt(i));
        return seq;
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 5) {
            KMeansWordCluster kmwc = new KMeansWordCluster(args[0], args[1], args[2]);
            kmwc.saveObject(args[4], kmwc.getAlphabet());
            kmwc.cluster();
            kmwc.saveObject(args[3], kmwc.getClassCenter());
        }
        if (args.length == 4) {
            KMeansWordCluster kmwc = new KMeansWordCluster(args[0], args[1], args[2], args[3]);
            System.out.println(kmwc.classifier("123"));
            System.out.println(kmwc.classifier("sdf"));
            System.out.println(kmwc.classifier("gjl"));
            System.out.println(kmwc.classifier("打日本"));
            System.out.println(kmwc.classifier("中日韩"));
            System.out.println(kmwc.classifier("几日呢"));
            System.out.println(kmwc.classifier("Begin0几日"));
            System.out.println(kmwc.classifier("几日End0"));
        }
        if(args.length==0){
        	KMeansWordCluster kmwc = new KMeansWordCluster(
					"./exp/featureCluster/alphabet","./exp/featureCluster/clusterCenter",
					"./exp/featureCluster/template","./exp/featureCluster/charsynset.txt");
//        	System.out.println(kmwc.classifier("新款intel处理器"));
        	System.out.println(kmwc.classifier("１２３"));
        	System.out.println(kmwc.classifier("123"));
            System.out.println(kmwc.classifier("sdf"));
            System.out.println(kmwc.classifier("ＡＢＢ"));
            System.out.println(kmwc.classifier("gjl"));
            System.out.println(kmwc.classifier("打日本"));
            System.out.println(kmwc.classifier("中日韩"));
            System.out.println(kmwc.classifier("几日呢"));
            System.out.println(kmwc.classifier("Begin0几日"));
            System.out.println(kmwc.classifier("几日End0"));
        }
    }
}