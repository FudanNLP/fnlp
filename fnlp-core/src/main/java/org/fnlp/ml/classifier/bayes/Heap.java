package org.fnlp.ml.classifier.bayes;

import java.util.ArrayList;
/**
 * 堆
 * @author sywu
 *
 * @param <T> 存储的数据类型
 */
public class Heap<T>{
	private boolean isMinRootHeap;
	private ArrayList<T> datas;
	private double[] scores;
	private int maxsize;
	private int size;

	public Heap(int max,boolean isMinRootHeap) {
		this.isMinRootHeap=isMinRootHeap;
		maxsize = max;
		scores = new double[maxsize+1];
		datas= new ArrayList<T>();
		size = 0;
		datas.add(null);
		scores[0]=0;
		
	}
	public Heap(int max) {
		this(max,true);
	}
	

	private int leftchild(int pos) {
		return 2 * pos;
	}

	private int rightchild(int pos) {
		return 2 * pos + 1;
	}

	private int parent(int pos) {
		return pos / 2;
	}

	private boolean isleaf(int pos) {
		return ((pos > size / 2) && (pos <= size));
	}
	
	private boolean needSwapWithParent(int pos){
		return isMinRootHeap?
				scores[pos] < scores[parent(pos)]:
				scores[pos] > scores[parent(pos)];
	}

	private void swap(int pos1, int pos2) {
		double tmp;
		tmp = scores[pos1];
		scores[pos1] = scores[pos2];
		scores[pos2] = tmp;
		T t1,t2;
		t1=datas.get(pos1);
		t2=datas.get(pos2);
		datas.set(pos1, t2);
		datas.set(pos2, t1);
	}


	public void insert(double score,T data) {
		if(size<maxsize){
			size++;
			scores[size] = score;
			datas.add(data);
			int current = size;
			while (current!=1&&needSwapWithParent(current)) {
				swap(current, parent(current));
				current = parent(current);
			}
		}
		else {
			if(isMinRootHeap?
					score>scores[1]:
					score<scores[1]){
				scores[1]=score;
				datas.set(1, data);
				pushdown(1);
			}
		}
	}


	public void print() {
		int i;
		for (i = 1; i <= size; i++)
			System.out.println(scores[i] + " " +datas.get(i).toString());
		System.out.println();
	}


//	public int removemin() {
//		swap(1, size);
//		size--;
//		if (size != 0)
//			pushdown(1);
//		return score[size + 1];
//	}
	private int findcheckchild(int pos){
		int rlt;
		rlt = leftchild(pos);
		if(rlt==size)
			return rlt;
		if (isMinRootHeap?(scores[rlt] > scores[rlt + 1]):(scores[rlt] < scores[rlt + 1]))
			rlt = rlt + 1;
		return rlt;
	}

	private void pushdown(int pos) {
		int checkchild;
		while (!isleaf(pos)) {
			checkchild = findcheckchild(pos);
			if(needSwapWithParent(checkchild))
				swap(pos, checkchild);
			else 
				return;			
			pos = checkchild;
		}
	}

	public ArrayList<T> getData(){
		return datas;
	}

	public static void main(String args[])
	{
		Heap<String> hm = new Heap<String>(6,true);
		hm.insert(1,"11");
		hm.insert(4,"44");
		hm.insert(2,"22");
		hm.insert(6,"66");
		hm.insert(3,"33");
		hm.insert(5,"55");
		hm.insert(9,"99");
		hm.insert(7,"77");
		hm.print();
		
	}
}