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

public class ClusterOri extends AbstractCluster {
    private ArrayList<ClassData> datalist;
    private HashMap<Integer, Integer> map;
    private HashMap<Integer, ArrayList<Integer>> mapList;
    private ArrayList<Boolean> flag;
    private AbstractDistance distance;
    private ArrayList<ArrayList<Double>> distanceList;
    private int feasize;

    public ClusterOri(ArrayList<ClassData> datalist, AbstractDistance distance, int feasize) {
        this.datalist = datalist;
        this.distance = distance;
        this.feasize = feasize;
        distanceList = new ArrayList<ArrayList<Double>>();
        flag = new ArrayList<Boolean>();
        map = new HashMap<Integer, Integer>();
        mapList = new HashMap<Integer, ArrayList<Integer>>();
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
            flag.add(true);
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
        ClassData cd1 = datalist.get(id1);
        ClassData cd2 = datalist.get(id2);
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
        deleteClassData(id2);
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

    private void deleteClassData(int id) {
        flag.set(id, false);
    }

    private void initDistanceAll() {
        for (int i = 0; i < datalist.size(); i++) {
            ArrayList<Double> disId = new ArrayList<Double>();
            for (int j = 0; j < datalist.size(); j++) {
                double distemp =  distance.cal(datalist.get(i), datalist.get(j));
                disId.add(distemp);
            }
            distanceList.add(disId);
        }
    }

    private void updateDistance(int id) {
        ClassData cd = datalist.get(id);
        ArrayList<Double> disId = distanceList.get(id);
        for (int i = 0; i < datalist.size(); i++) {
            if (!flag.get(i))
                continue;
            else {
                double distemp = distance.cal(cd, datalist.get(i));
                disId.set(i, distemp);
            }
        }
    }

    private int[] minId() {
        int len1 = distanceList.size();
        int len2 = distanceList.get(0).size();
        int[] id = new int[]{0, 0};
        double min = Double.MAX_VALUE;
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
                if (!(flag.get(i)) || !(flag.get(j)) || j == i)
                    continue;
                else {
                    double temp = getDistance(i, j);
                    if (temp < min) {
                        min = temp;
                        id[0] = i;
                        id[1] = j;
                    }
                }
            }
        }
        return id;
    }

    private double getDistance(int i, int j) {
        ArrayList<Double> disId = distanceList.get(i);
        return disId.get(j);
    }

    public void process() {
        initDistanceAll();
        System.out.println("Finish distance init");
        int cycle = getFeaSize(feasize);
        for (int i = 0; i < cycle; i++) {
            int[] id = minId();
            System.out.println(id[0] + " " + id[1]);
            merge(id[0], id[1]);
            updateDistance(id[0]);
            System.out.println(i);
        }
    }

    private int getFeaSize(int feasize) {
        return datalist.size() - feasize;
    }
}