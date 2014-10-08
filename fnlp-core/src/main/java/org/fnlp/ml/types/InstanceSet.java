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

package org.fnlp.ml.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.fnlp.data.reader.Reader;
import org.fnlp.ml.types.alphabet.AlphabetFactory;
import org.fnlp.nlp.pipe.Pipe;
import org.fnlp.nlp.pipe.SeriesPipes;

/**
 * 样本集合
 * 
 * @author xpqiu
 * 
 */
public class InstanceSet extends ArrayList<Instance> {

    private static final long serialVersionUID = 3449458306217680806L;
    /**
     * 本样本集合默认的数据类型转换管道
     */
    private Pipe pipes = null;
    /**
     * 本样本集合对应的特征和标签索引字典管理器
     */
    private AlphabetFactory factory = null;
    
    
    public int numFeatures = 0;
    public String name = "";

    public InstanceSet(Pipe pipes) {
        this.pipes = pipes;
    }

    public InstanceSet(Pipe pipes, AlphabetFactory factory) {
        this.pipes = pipes;
        this.factory = factory;
    }

    public InstanceSet(AlphabetFactory factory) {
        this.factory = factory;
    }
    
    public InstanceSet() {
    }

    

    /**
     * 分割样本集，将样本集合中样本放随机放在两个集合，大小分别为i/n,(n-i)/n
     * 
     * @param i 第一个集合比例 
     * @param n 集合样本总数（相对于i）
     * @return
     */
    public InstanceSet[] split(int i, int n) {
        return split((float) i/(float)n);
    }
    
    /**
     * 分割样本集，将样本集合中样本放随机放在两个集合，大小分别为i/n,(n-i)/n
     * 
     * @param percent 分割比例 必须在0,1之间
     * @return
     */
    public InstanceSet[] split(float percent) {
        shuffle();
        int length = this.size();
        InstanceSet[] sets = new InstanceSet[2];
        sets[0] = new InstanceSet(pipes, factory);
        sets[1] = new InstanceSet(pipes, factory);
        int idx = (int) Math.round(percent*length);
        sets[0].addAll(subList(0, idx));
        if(idx+1<length)            
            sets[1].addAll(subList(idx+1, length));
        return sets;
    }

    public InstanceSet[] randomSplit(float percent) throws Exception {
        if (percent > 1 || percent < 0)
            throw new Exception("Percent should be in [0, 1]");
//        shuffle();
        InstanceSet[] sets = new InstanceSet[2];
        sets[0] = new InstanceSet(pipes, factory);
        sets[1] = new InstanceSet(pipes, factory);
        int[] flag = labelFlag();
        List<ArrayList<Integer>> list = listLabel(flag);
        flag = randomSet(flag, list, percent);
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] < 0) 
                sets[0].add(this.get(i));
            else
                sets[1].add(this.get(i));
        }
        return sets;
    }

    public int[] randomSet(int[] flag, List<ArrayList<Integer>> list, float percent) {
        Random r = new Random();
        for(ArrayList<Integer> alist : list) {
            int allsize = Math.round(alist.size() * percent);
            int count = 0;
            while (true) {
                int randomInt = r.nextInt(alist.size());
                int index = alist.get(randomInt);
                if (flag[index] >= 0) {
                    flag[index] = -1;
                    count++;
                    if (count >= allsize)
                        break;
                }
            }
        }
        return flag;
    }

    public List<ArrayList<Integer>> listLabel(int[] flag) {
        List<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
        int classsize = classSize().size();
        for (int i = 0; i < classsize; i++) {
            List<Integer> ll = new ArrayList<Integer>();
            list.add((ArrayList<Integer>)ll);
        }
        for (int i = 0; i < flag.length; i++) {
            int ele = flag[i];
            ArrayList<Integer> l = list.get(ele);
            l.add(i);
        }
        return list;
    }

    public int[] labelFlag() {
        int length = this.size();
        int[] flag = new int[length];
        Map<Object,Integer> map = classSize();
        for (int i = 0; i < length; i++) {
            Object target = this.get(i).getTarget();
            int label = map.get(target);
            flag[i] = label;
        }
        return flag;
    }

    public Map<Object, Integer> classSize() {
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        int label = 0;
        for (Instance ins : this) {
            if (!map.containsKey(ins.getTarget())) {
                map.put(ins.getTarget(), label++);
            }
        }
        return map;
    }
    
    public InstanceSet subSet(int from,int end){
        InstanceSet set = new InstanceSet();
        set = new InstanceSet(pipes, factory);
        set.addAll(subList(from,end));
        return set;
    }

    /**
     * 用本样本集合默认的“数据类型转换管道”通过“数据读取器”批量建立样本集合
     * @param reader 数据读取器
     * @throws Exception
     */
    public void loadThruPipes(Reader reader) throws Exception {

        // 通过迭代加入样本
        while (reader.hasNext()) {
            Instance inst = reader.next();
            if (pipes != null)
                pipes.addThruPipe(inst);
            this.add(inst);
        }
    }

    /**
     * 分步骤批量处理数据，每个Pipe处理完所有数据再进行下一个Pipe
     * 
     * @param reader
     * @throws Exception
     */
    public void loadThruStagePipes(Reader reader) throws Exception {
        SeriesPipes p = (SeriesPipes) pipes;
        // 通过迭代加入样本
        Pipe p1 = p.getPipe(0);
        while (reader.hasNext()) {
            Instance inst = reader.next();
            if(inst!=null){
                if (p1 != null)
                    p1.addThruPipe(inst);
                this.add(inst);
               
            };
        }
        for (int i = 1; i < p.size(); i++)
            p.getPipe(i).process(this);
    }
    
    /**
     * 实验用, 为了MultiCorpus, 工程开发请忽略
     * 
     * 分步骤批量处理数据，每个Pipe处理完所有数据再进行下一个Pipe
     * 
     * @param reader
     * @throws Exception
     */
    public void loadThruStagePipesForMultiCorpus(Reader[] readers, String[] corpusNames) throws Exception {
        SeriesPipes p = (SeriesPipes) pipes;
        // 通过迭代加入样本
        Pipe p1 = p.getPipe(0);
        for(int i = 0; i < readers.length; i++) {
	        while (readers[i].hasNext()) {
	            Instance inst = readers[i].next();
	            inst.setClasue(corpusNames[i]);
	            if(inst!=null){
	                if (p1 != null)
	                    p1.addThruPipe(inst);
	                this.add(inst);
	            };
	        }
        }
        
        for (int i = 1; i < p.size(); i++)
            p.getPipe(i).process(this);
    }

    public void shuffle() {
        Collections.shuffle(this);
    }
    
    public void sortByWeights() {
        Collections.sort(this, new Comparator<Instance>() {
			@Override
			public int compare(Instance o1, Instance o2) {
				
				float f1 = o1.getWeight();
				float f2 = o2.getWeight();
				if(f1<f2)
					return 1;
				else if(f1>f2)
					return -1;
				else
					return 0;
			}
		});
    }

    public void shuffle(Random r) {
        Collections.shuffle(this, r);
    }
    
    public void shuffle(Random r1, Random r2, InstanceSet i){
    	Collections.shuffle(this, r1);
    	Collections.shuffle(i, r2);
    }

    public Pipe getPipes() {
        return pipes;
    }

    public Instance getInstance(int idx) {
        if (idx < 0 || idx > this.size())
            return null;
        return this.get(idx);
    }

    public AlphabetFactory getAlphabetFactory() {
        return factory;
    }

//    public void addAll(InstanceSet subset) {
//        this.addAll(subset);
//    }

    public void setPipes(Pipe pipes) {
        this.pipes = pipes;
    }

    public void setAlphabetFactory(AlphabetFactory factory) {
        this.factory = factory;
    }
    
    public String toString(){
    	StringBuilder sb= new StringBuilder();
    	for(int i=0;i<size();i++){
    		sb.append(get(i));
    		sb.append("\n");
    	}
    	return sb.toString();
    	
    }

}