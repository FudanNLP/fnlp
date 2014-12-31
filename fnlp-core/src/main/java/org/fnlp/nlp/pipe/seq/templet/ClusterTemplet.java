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

package org.fnlp.nlp.pipe.seq.templet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fnlp.ml.types.Instance;
import org.fnlp.ml.types.alphabet.IFeatureAlphabet;

/**
 * 类CRF模板 
 * 格式为： 
 *  0: %x[-1,0]%x[1,0]%y[0];
 *  1: %y[-1]%y[0]
 * @author Feng Ji
 */
public class ClusterTemplet implements Templet {

    private static final long serialVersionUID = 7543856094273600355L;
    private HashMap<String, String> keymap;
    Pattern parser = Pattern.compile("(?:%(x|y)\\[(-?\\d+)(?:,(\\d+))?\\])");
    String templet;
    /**
     * 模板阶数
     */
    int order;
    int id;
    /**
     * x相对位置
     */
    int[][] dims;
    /**
     * y相对位置
     */
    int[] vars;
    /**
     * 对于某些情况不返回特定信息
     */
    public static boolean isForceTrain = false;
    private static int minLen = 5;

    /**
     * 构造函数
     *
     * @param templet
     *            模板字符串 格式如：%x[0,0]%y[0]; %y[-1]%y[0]; %x[0,0]%y[-1]%y[0]
     */
    public ClusterTemplet(int id, String templet, HashMap<String, String> keymap) {
        this.id = id;
        this.templet = templet;
        this.keymap = keymap;
        Matcher matcher = parser.matcher(this.templet);
        /**
         * 解析y的位置
         */
        List<String> l = new ArrayList<String>();
        List<String> x = new ArrayList<String>();
        while (matcher.find()) {
            if (matcher.group(1).equalsIgnoreCase("y")) {
                l.add(matcher.group(2));
            } else if (matcher.group(1).equalsIgnoreCase("x")) {
                x.add(matcher.group(2));
                x.add(matcher.group(3));
            }
        }
        if(l.size()==0){//兼容CRF++模板
            vars = new int[]{0};
        }else{
            vars = new int[l.size()];
            for (int j = 0; j < l.size(); j++) {
                vars[j] = Integer.parseInt(l.get(j));
            }
        }
        order = vars.length - 1;
        l = null;

        dims = new int[x.size() / 2][2];
        for (int i = 0; i < x.size(); i += 2) {
            dims[i / 2][0] = Integer.parseInt(x.get(i));
            dims[i / 2][1] = Integer.parseInt(x.get(i + 1));
        }
        x = null;
    }

    /**
     * @see org.fnlp.nlp.pipe.seq.templet.Templet#getVars()
     */
    public int[] getVars() {
        return this.vars;
    }

    /**
     * @see org.fnlp.nlp.pipe.seq.templet.Templet#getOrder()
     */
    public int getOrder() {
        return this.order;
    }

    /**
     * {@inheritDoc}
     */
    public int generateAt(Instance instance, IFeatureAlphabet features, int pos, int... numLabels) throws Exception {
        String feaOri = getFeaString(instance, pos, numLabels);
        if (feaOri == null)
            return -1;
        int index = -1;
        if (keymap.containsKey(feaOri))
            index = features.lookupIndex(keymap.get(feaOri), (int) Math.pow(numLabels[0], order + 1));
        return index;
    }

    public String getFeaString(Instance instance, int pos, int... numLabels) { 
        assert (numLabels.length == 1);

        String[][] data = (String[][]) instance.getData();
        int len = data[0].length;

        if(order>0&& len==1) //对于长度为1的序列，不考虑1阶以上特征
            return null;
        
        if(isForceTrain){
            if(len<minLen && order>0 )//训练时，对于长度过小的句子，不考虑开始、结束特征
                return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(id);
        sb.append(':');
        for (int i = 0; i < dims.length; i++) {
            String rp = "";
            int j = dims[i][0]; //行号
            int k = dims[i][1]; //列号
            if (pos + j < 0 || pos + j >= len) {
                if(isForceTrain){
                    if(len<minLen )//对于长度过小的句子，不考虑开始、结束特征
                        return null;
                }
                if (pos + j < 0)
                    rp = "B_" + String.valueOf(-(pos + j) - 1);
                if (pos + j >= len)
                    rp = "E_" + String.valueOf(pos + j - len);

            } else {
                rp = data[k][pos + j]; //这里数据行列和模板中行列相反
            }
            
            sb.append(rp);
            sb.append("/");
        }
        return sb.toString();
    }

    public String toString() {
        return this.templet;
    }

    public int offset(int... curs) {
        return 0;
    }
}