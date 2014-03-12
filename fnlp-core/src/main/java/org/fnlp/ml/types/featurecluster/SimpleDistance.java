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

public class SimpleDistance extends AbstractDistance {
    public double cal(ClassData cd1, ClassData cd2) {
        if (checkLabelLength(cd1, cd2)) 
            return calSimpleDistance(cd1, cd2);
        else
            return Double.MAX_VALUE;
    }

    private boolean checkLabelLength(ClassData cd1, ClassData cd2) {
        return cd1.getLabel().length == cd2.getLabel().length;
    }

    private double calSimpleDistance(ClassData cd1, ClassData cd2) {
        double[] label1 = cd1.getLabel();
        double[] label2 = cd2.getLabel();
        return calSimpleDistanceArray(label1, label2);
    }

    private double calSimpleDistanceArray(double[] label1, double[] label2) {
        int base = getBase(label1, label2);
        if (base == -1)
            return 1;
        double a1 = label1[base];
        double b1 = label2[base];
        for (int i = base + 1; i < label1.length; i++)
            if (checkZero(label1[i], label2[i]) || (label1[i] * b1 != label2[i] * a1))
                return 1;
        return 0;
    }

    private int getBase(double[] label1, double[] label2) {
        int i = 0;
        for (i = 0; i < label1.length; i++) {
            if (checkAllZero(label1[i], label2[i]))
                continue;
            else if (checkZero(label1[i], label2[i]))
                return -1;
            else 
                break;
        }
        return i;
    }

    private boolean checkZero(double a, double b) {
        return ((a == 0 && b !=0) || (a !=0 && b == 0));
    }

    private boolean checkAllZero(double a, double b) {
        return (a == 0 && b == 0);
    }

    public static void main(String[] args) {
        SimpleDistance distance = new SimpleDistance();
        System.out.println(distance.calSimpleDistanceArray(new double[]{0.0, 0.0, 1.0, 0.0}, new double[]{0.0, 0.0, 1.0, 0.0}));
        System.out.println(distance.calSimpleDistanceArray(new double[]{0, 0, 3}, new double[]{0, 0, 5}));
        System.out.println(distance.calSimpleDistanceArray(new double[]{0, 2, 3}, new double[]{0, 4, 6}));
        System.out.println(distance.calSimpleDistanceArray(new double[]{4, 2, 3}, new double[]{2, 4, 6}));
        System.out.println(distance.calSimpleDistanceArray(new double[]{1, 2, 3}, new double[]{2, 8, 6}));
    }
}