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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.fnlp.nlp.cn.ChineseTrans;
import org.fnlp.nlp.similarity.ISimilarity;
import org.fnlp.nlp.similarity.JaccardSimilarity;

import gnu.trove.set.hash.THashSet;

public class WordSimilarity {

    private static ChineseTrans tc = new ChineseTrans();
    ISimilarity is = new JaccardSimilarity();
    ArrayList<THashSet<String>> hashlist = new ArrayList<THashSet<String>>();
    ArrayList<String> word = new ArrayList<String>();
    HashSet<String> allC = new HashSet<String>();
    HashMap<String, Integer> cmap = new HashMap<String, Integer>();
    ArrayList<ArrayList<String>> clusterResult = new ArrayList<ArrayList<String>>();
    ArrayList<THashSet<String>> clusterHashList = new ArrayList<THashSet<String>>();

    /**
     * @param hashlist the hashlist to set
     */
    public void setHashlist(ArrayList<THashSet<String>> hashlist) {
        this.hashlist = hashlist;
    }

    /**
     * @param word the word to set
     */
    public void setWord(ArrayList<String> word) {
        this.word = word;
    }

    /**
     * @param cmap the cmap to set
     */
    public void setCmap(HashMap<String, Integer> cmap) {
        this.cmap = cmap;
    }

    private void countAllC(String inputpath) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputpath), "UTF-8"));
        String s;
        while ((s = reader.readLine()) != null) {
            for (int i = 0; i < s.length(); i++) {
                allC.add(String.valueOf(s.charAt(i)));
            }
        }
        reader.close();
        set2List();
        System.out.println("Finished count all character");
        System.out.println("word size: " + word.size());
    }

    private void set2List() {
        Iterator<String> it = allC.iterator();
        int n = 0;
        while (it.hasNext()) {
            String s = it.next();
            word.add(s);
            cmap.put(s, n++);
        }
    }

    private void initHashSet() {
        for (int i = 0; i < word.size(); i++) {
            THashSet<String> hashset = new THashSet<String>();
            hashlist.add(hashset);
        }
    }

    public void dirSougouCAReader(String dirpath, String outpath)
            throws IOException {
        File idir = new File(dirpath);
        File[] files = idir.listFiles();
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outpath), "UTF-8"));
        for (File file : files) {
            String strFilePath = file.getAbsolutePath();
            SougouCA sca = new SougouCA(strFilePath);
            while (sca.hasNext()) {
                String s = (String) sca.next().getData();
                s = tc.normalize(s);
                if (s.length() == 0)
                    continue;
                bout.write(s + "\n");
            }
        }
        bout.close();
        System.out.println("Done!");
    }

    public void gramString(String inputpath) throws IOException {
        initHashSet();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputpath), "UTF-8"));
        String s;
        int count = 0;
        while ((s = reader.readLine()) != null) {
            if (++count % 100000 == 0)
                System.out.println(count);
            gramPerString(s);
        }
        reader.close();
        System.out.println("Finished load file");
    }
    
    @SuppressWarnings("unchecked")
    public void calSimilarity(String outpath) throws Exception {
        BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath), "UTF-8"));
        for (int i = 0; i < hashlist.size(); i++)
            bout.write("\t" + word.get(i));
        bout.write("\n");
        for (int i = 0; i < hashlist.size(); i++) {
            bout.write(word.get(i) + "\t");
            for (int j = 0; j < hashlist.size(); j++) {
                float f = is.calc((THashSet<String>)hashlist.get(i), (THashSet<String>)hashlist.get(j));
                f = (float)(Math.round(f * 1000)) / 1000;
                bout.write(f + "\t");
            }
            bout.write("\n");
        }
        bout.close();
    }

    private void gramPerString(String str) {
        String s = "^" + str + "*";
        for (int i = 1; i < s.length() - 1; i++) {
            String c = s.substring(i, i+1);
            if (cmap.containsKey(c)) {
                String temp = s.substring(i-1, i) + s.substring(i+1, i+2);
                int id = cmap.get(c);
                hashlist.get(id).add(temp);
            }
        }
    }

    private void cluster(int size) throws Exception {
        if (!checkValid(size)) {
            System.out.println("Do not need cluster");
            return;
        }
        initclusterResult(size);
        for (int i = size; i < word.size(); i++) {
            int id = clusterPerElement(i);
            merge(i, id);
            outputTerminal(i);
        }
    }

    private int clusterPerElement(int pos) throws Exception {
        THashSet<String> thashsetPos = hashlist.get(pos);
        float max = Float.MAX_VALUE;
        int id = 0;
        for (int i = 0; i < clusterHashList.size(); i++) {
            @SuppressWarnings("unchecked")
            float f = is.calc(thashsetPos, clusterHashList.get(i));
            if (f < max) {
                max = f;
                id = i;
            }
        }
        return id;
    }

    private void merge(int pos, int id) {
        THashSet<String> posSet = hashlist.get(pos);
        THashSet<String> idSet = clusterHashList.get(id);
        idSet.addAll(posSet);
        ArrayList<String> list = clusterResult.get(id);
        list.add(word.get(pos));
    }

    private boolean checkValid(int size) {
        return size < word.size() ? true : false;
    }

    private void initclusterResult(int size) {
        for (int i = 0; i < size; i++) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(word.get(i));
            clusterResult.add(list);
            THashSet<String> thashset = new THashSet<String>();
            thashset.addAll(hashlist.get(i));
            clusterHashList.add(thashset);
        }
    }

    private void outputTerminal(int pos) {
        if ((1 + pos) % 1000 == 0)
            System.out.println(1 + pos);
    }

    public void biList2File(String output) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        for (ArrayList<String> list : clusterResult) {
            for (String s : list) {
                bw.write(s + " ");
            }
            bw.write("\n");
        }
        bw.close();
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

    @SuppressWarnings("unchecked")
    public void read(String path) throws IOException, ClassNotFoundException {
        setHashlist((ArrayList<THashSet<String>>)loadObject(path + "hashlist"));
        setWord((ArrayList<String>)loadObject(path + "word"));
        setCmap((HashMap<String, Integer>)loadObject(path + "cmap"));
        System.out.println("Finished load model");
    }

    public void save(String path) throws IOException {
        saveObject(path + "hashlist", hashlist);
        saveObject(path + "word", word);
        saveObject(path + "cmap", cmap);
        System.out.println("Finished save to disk");
    }

    public static void main(String[] args) {
        WordSimilarity ws = new WordSimilarity();
        try {
//            ws.dirSougouCAReader("./tmpdata/SogouCa/", "./tmpdata/all.data");
//            ws.countAllC("./tmpdata/all.data");
//            ws.gramString("./tmpdata/all.data");
//            ws.save("./tmpdata/model/");
            ws.read("./tmpdata/model/");
            ws.cluster(100);
            ws.biList2File("./tmpdata/clusterResult");
//            ws.calSimilarity("./tmpdata/statics");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}