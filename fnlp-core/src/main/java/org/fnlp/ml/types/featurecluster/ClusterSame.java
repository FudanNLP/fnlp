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

public class ClusterSame extends AbstractCluster {
    private ArrayList<ClassData> datalist;
    private AbstractDistance distance;
    private HashMap<Integer, Integer> map;
    private ArrayList<Integer> listDiff;

    public ClusterSame(ArrayList<ClassData> datalist, AbstractDistance distance) {
        this.datalist = datalist;
        this.distance = distance;
        map = new HashMap<Integer, Integer>();
        listDiff = new ArrayList<Integer>();
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
        }
//        regular();
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

    public void process() {
        int flag = 0;
        listDiff.add(0);
        for (int i = 1; i < datalist.size(); i++) {
            if (i % 10000 == 0) {
                System.out.println(i + "\t" + listDiff.size());
            }
            flag = 0;
            ClassData cd1 = datalist.get(i);
//            if (cd1.getCount() > 3) {
//                listDiff.add(i);
//                continue;
//            }
            for (int j = 0; j < listDiff.size(); j++) {
                ClassData cd2 = datalist.get(listDiff.get(j));
                if (distance.cal(cd1, cd2) <= 0) {
                    int key1 = cd1.getKey();
                    int key2 = cd2.getKey();
                    map.put(key1, key2);
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                listDiff.add(i);
            }
        }
        System.out.println(listDiff.size());
    }
}