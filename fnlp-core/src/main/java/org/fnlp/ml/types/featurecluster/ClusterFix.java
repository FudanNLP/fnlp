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

package org.fnlp.ml.types.featurecluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class ClusterFix extends AbstractCluster {
    private ArrayList<ClassData> datalist;
    private HashMap<Integer, Integer> map;
    private HashMap<Integer, ArrayList<Integer>> mapList;
    private AbstractDistance distance;
    private int feasize;
    private ArrayList<ArrayList<Double>> distanceList;
    private ArrayList<Integer> idList;
    private TreeMap<Double, Set<String>> sortmap;

    public ClusterFix(ArrayList<ClassData> datalist, AbstractDistance distance, int feasize) {
        this.datalist = datalist;
        this.distance = distance;
        this.feasize = feasize;
        map  = new HashMap<Integer, Integer>();
        mapList = new HashMap<Integer, ArrayList<Integer>>();
        distanceList = new ArrayList<ArrayList<Double>>();
        idList = new ArrayList<Integer>();
        sortmap = new TreeMap<Double, Set<String>>();
        paraInit();
        setAllCount();
    }

    /**
     * @return the map
     */
    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    private void paraInit() {
        for (ClassData cd : datalist) {
            int key = cd.getKey();
            map.put(key, key);
            ArrayList<Integer> alist = new ArrayList<Integer>();
            alist.add(key);
            mapList.put(key, alist);
        }
        regular();
    }

    private void setAllCount() {
        int allCount = 0;
        for (ClassData cd : datalist) 
            allCount += cd.getCount();
        ClassData.allCount = allCount;
    }

    private void regular() {
        for (ClassData cd : datalist) {
            regular(cd);
        }
    }

    private void regular(ClassData cd) {
        double[] label = cd.getLabel();
        double sum = 0;
        for (double ele : label)
            sum += ele;
        for (int i = 0; i < label.length; i++) {
            label[i] = label[i] / sum;
        }
        cd.setLabel(label);
    }

    private void merge(int id1, int id2) {
        int idd1 = idList.get(id1);
        int idd2 = idList.get(id2);
        ClassData cd1 = datalist.get(idd1);
        ClassData cd2 = datalist.get(idd2);
        int count1 = cd1.getCount();
        int count2 = cd2.getCount();
        double ratio = (double) count1 / (double) (count1 + count2);
        double[] label1 = cd1.getLabel();
        double[] label2 = cd2.getLabel();
        int count = updateCount(count1, count2);
        double[] label = updateLabel(label1, label2, ratio);
        cd1.update(label, count);
        try {
            mapKey(cd2.getKey(), cd1.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int updateCount(int count1, int count2) {
        return count1 + count2;
    }

    private double[] updateLabel(double[] label1, double[] label2, double ratio) {
        int length = label1.length;
        double[] label = new double[length];
        for (int i = 0; i < length; i++) {
            label[i] = ratio * label1[i] + (1-ratio) * label2[i];
        }
        return label;
    }

    private void mapKey(int orikey, int key) throws Exception {
        int orivalue = map.get(orikey);
        int value = map.get(key);
        ArrayList<Integer> oriKeyList = mapList.get(orivalue);
        ArrayList<Integer> keyList = mapList.get(value);
        for (Integer temp : oriKeyList) {
            map.put(temp, value);
            keyList.add(temp);
        }
        mapList.remove(orivalue);
    }

    private void addClassData(int id, int idd) {
        idList.set(id, idd);
    }

    private void initDistanceAll() {
        for (int i = 0; i < feasize; i++) {
            ArrayList<Double> disId = new ArrayList<Double>();
            idList.add(i);
            for (int j = 0; j < feasize; j++) {
                double distemp =  distance.cal(datalist.get(i), datalist.get(j));
                disId.add(distemp);
            }
            distanceList.add(disId);
        }
    }

    private void updateDistance(int id) { //id of idList
        int idd = idList.get(id);
        ClassData cd = datalist.get(idd);
        for (int i = 0; i < idList.size(); i++) {
            if (i == id)
                continue;
            int idDataList = idList.get(i);
            double distemp = distance.cal(cd, datalist.get(idDataList));
            if (i < id) {
                double oridata = getDistance(i, id);
                updateSortMap(i, id, oridata, distemp);
            }
            else {
                double oridata = getDistance(id, i);
                updateSortMap(id, i, oridata, distemp);
            }
            setDistance(id, i, distemp);
            setDistance(i, id, distemp);
        }
    }
    
    private void setDistance(int i, int j, double value) {
        ArrayList<Double> disId = distanceList.get(i);
        disId.set(j, value);
    }

    private int[] minIdMap() {
        double key = sortmap.firstKey();
        Set<String> set = sortmap.get(key);
        Iterator<String> it = set.iterator();
        int[] id = string2Id(it.next());
//        System.out.println(key + " " + id[0] + " " + id[1]);
        return id;
    }

    public int[] minId() {
        int[] id = new int[]{0, 0};
        double min = Double.MAX_VALUE;
        for (int i = 0; i < feasize; i++) {
            for (int j = 0; j < feasize; j++) {
                if (j == i)
                    continue;
                else {
                    double temp = getDistance(i, j);
                    if (temp < min) {
                        min = temp;
                        id[0] = i;
                        id[1] = j;
                    }
                }
                if (min <= 0) {
//                    System.out.println("min: " + min);
                    return id;
                }
            }
        }
//        System.out.println("min: " + min);
        return id;
    }

    private void initSortMap() {
        for (int i = 0; i < feasize - 1; i++) {
            for (int j = i + 1; j < feasize; j++) {
                double temp = getDistance(i, j);
                Set<String> hashset;
                if (sortmap.containsKey(temp))
                    hashset = sortmap.get(temp);
                else
                    hashset = new HashSet<String>();
//                System.out.println(temp + " " + i + " " + j);
//                System.out.println(getDistance(i, j));
                hashset.add(id2String(i, j));
                sortmap.put(temp, hashset);
            }
        }
        System.out.println("Map init size: " + sortmap.size());
    }

    private void updateSortMap(int a, int b, double oridata, double updatedata) {
        deleteSortMap(a, b, oridata);
        addSortMap(a, b, updatedata);
    }

    private void deleteSortMap(int a, int b, double oridata) {
        Set<String> set = sortmap.get(oridata);
        if (set == null)
            return;
        if (set.size() == 1)
            sortmap.remove(oridata);
        else
            set.remove(id2String(a, b));
    }

    private void addSortMap(int a, int b, double updatedata) {
        if (sortmap.containsKey(updatedata)) {
            Set<String> set = sortmap.get(updatedata);
            set.add(id2String(a, b));
        }
        else {
            Set<String> set = new HashSet<String>();
            set.add(id2String(a, b));
            sortmap.put(updatedata, set);
        }
    }

    private double getDistance(int i, int j) {
        ArrayList<Double> disId = distanceList.get(i);
        return disId.get(j);
    }

    private String id2String(int a, int b) {
        String s = a + "$" + b;
        return s;
    }

    private int[] string2Id(String s) {
        String[] sid = s.split("\\$");
        int[] id = new int[2];
        id[0] = Integer.parseInt(sid[0]);
        id[1] = Integer.parseInt(sid[1]);
        return id;
    }

    public void process() {
        initDistanceAll();
        initSortMap();
        System.out.println("Finish distance & sortmap init");
        if (feasize >= datalist.size()) {
            System.out.println("Do not need feature cluster");
            return;
        }
        for (int i = feasize; i < datalist.size(); i++) {
            int[] id = minIdMap();
            merge(id[0], id[1]);
            addClassData(id[1], i);
            updateDistance(id[0]);
            updateDistance(id[1]);
            if ((i + 1) % 10000 == 0) {
                System.out.println(i);
                System.out.println(sortmap.size());
            }
        }
    }
}