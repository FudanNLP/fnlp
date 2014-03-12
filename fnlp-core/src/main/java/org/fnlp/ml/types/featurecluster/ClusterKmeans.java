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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusterKmeans extends AbstractCluster {
    private ArrayList<ClassData> datalist;
    private HashMap<Integer, Integer> map;
    private HashMap<Integer, ArrayList<Integer>> mapList;
    private AbstractDistance distance;
    private int feasize;
    private ArrayList<Double> distanceList;
    private ArrayList<Integer> idList;
    private TIntObjectHashMap<String> index = null;

    public ClusterKmeans(ArrayList<ClassData> datalist, AbstractDistance distance, int feasize) {
        this.datalist = datalist;
        this.distance = distance;
        this.feasize = feasize;
        map  = new HashMap<Integer, Integer>();
        mapList = new HashMap<Integer, ArrayList<Integer>>();
        distanceList = new ArrayList<Double>();
        idList = new ArrayList<Integer>();
        paraInit();
        setAllCount();
    }

    /**
     * @return the map
     */
    public HashMap<Integer, Integer> getMap() {
        return map;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(TIntObjectHashMap<String> index) {
        this.index = index;
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

    private void merge(int id1, int idd2) {    //id1:idList  idd2:dataList
        int idd1 = idList.get(id1);
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
    
    private void initStack() {
        for (int i = 0; i < feasize; i++) {
            idList.add(i);
            distanceList.add(0.0);
        }
    }

    private void updateDistance(int id) {
        ClassData cd = datalist.get(id);
        for (int i = 0; i < idList.size(); i++) {
            if (i == id)
                continue;
            int idDataList = idList.get(i);
            double distemp = distanceOfTwo(cd, datalist.get(idDataList));
            distanceList.set(i, distemp);
        }
    }

    private double distanceOfTwo(ClassData cd1, ClassData cd2) {
        if (index == null)
            return distance.cal(cd1, cd2);
        if (isSameTemplate(cd1, cd2))
            return distance.cal(cd1, cd2);
        else
            return Double.MAX_VALUE;
    }

    private boolean isSameTemplate(ClassData cd1, ClassData cd2) {
        String key1 = index.get(cd1.getKey());
        String key2 = index.get(cd2.getKey());
        return key1.charAt(0) == key2.charAt(0);
    }

    private int minId() {
        int id = -1;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < feasize; i++) {
            double temp = distanceList.get(i);
            if (temp < min) {
                id = i;
                min = temp;
            }
            if (min <= 0) {
                return id;
            }
        }
        return id;
    }

    public void process() {
        initStack();
        if (feasize >= datalist.size()) {
            System.out.println("Do not need feature cluster");
            return;
        }
        for (int i = feasize; i < datalist.size(); i++) {
            updateDistance(i);
            int id = minId();
            merge(id, i);
            if ((i + 1) % 10000 == 0)
                System.out.println(i);
        }
    }
}