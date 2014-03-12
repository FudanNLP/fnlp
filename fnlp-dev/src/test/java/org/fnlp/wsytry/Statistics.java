package org.fnlp.wsytry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.fnlp.ml.types.alphabet.LabelAlphabet;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.set.hash.TIntHashSet;

public class Statistics {
	class Parts{
		Parts(){
			key=0;
			value=0;
		}
		int key,value;
	}
	static int N=50;
	Parts prefix[][]=new Parts[N][N];
	Parts suffix[][]=new Parts[N][N];
	Parts total[]=new Parts[N];
	LabelAlphabet alpahbet = new LabelAlphabet();
	public void statistic(){
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++){
				prefix[i][j]=new Parts();
				prefix[i][j].key=j;
				prefix[i][j].value=0;
				suffix[i][j]=new Parts();
				suffix[i][j].key=j;
				suffix[i][j].value=0;
			}
		for(int i=0;i<N;i++){
			total[i]=new Parts();
			total[i].key=i;
			total[i].value=0;
		}
		
		String fileName="./tmp/POS_tag_dataset.txt";
		File file=new File(fileName);
		FileReader fileReader=null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		
		BufferedReader reader=new BufferedReader(fileReader);

		String str;
		try {
			while((str=reader.readLine())!=null){
				System.out.println(str);
				List<String> posList=new ArrayList<String>();
				posList.add("BEGIN");
				int l=-1;
				while(l<str.length()-1){
					int r;
					r=str.indexOf(' ', l+1);
					if(r==-1)
						r=str.length();
					if(r-l>1)
						posList.add(str.substring(l+1, r));
					l=r;
				}
				posList.add("END");
				int preIdx=-1;
				for(int i=0;i<posList.size();i++){
					int idx;
					String c = String.valueOf(posList.get(i));
					idx = alpahbet.lookupIndex(c);
					
					total[idx].value++;
					if(i>0){
						prefix[idx][preIdx].value++;
						suffix[preIdx][idx].value++;
					}
					preIdx=idx;
				}
			}
			reader.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		int num=alpahbet.size();
		for(int i=0;i<num;i++)
			for(int j=i+1;j<num;j++)
				if(total[i].value<total[j].value){
					Parts temp=total[i];
					total[i]=total[j];
					total[j]=temp;
				}
		int sum=0;
		for(int i=0;i<num;i++)
			sum+=total[i].value;
		for(int t=0;t<num;t++){
			for(int i=0;i<num;i++)
				for(int j=i+1;j<num;j++)
					if(prefix[t][i].value<prefix[t][j].value){
						Parts temp=prefix[t][i];
						prefix[t][i]=prefix[t][j];
						prefix[t][j]=temp;
					}

			for(int i=0;i<num;i++)
				for(int j=i+1;j<num;j++)
					if(suffix[t][i].value<suffix[t][j].value){
						Parts temp=suffix[t][i];
						suffix[t][i]=suffix[t][j];
						suffix[t][j]=temp;
					}
		}
        DecimalFormat df = new DecimalFormat("#.000");
		try {
			fileName="./tmp/statistics.txt";
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					fos, "UTF8"));
			for(int i=0;i<num;i++){
				int v=total[i].key;
				if(total[i].value==0)
					break;
				bout.write(alpahbet.lookupString(v)+" "+total[i].value+"("+df.format((double)total[i].value/sum)+")\nprefix:\n");
				for(int j=0;j<(num<10?num:10);j++){
					bout.write(alpahbet.lookupString(prefix[v][j].key)+"("+df.format((double)prefix[v][j].value/total[i].value)+"), ");
					if(j==4)
						bout.write("\n");
				}
				bout.write("\nsuffix:\n");
				for(int j=0;j<(num<10?num:10);j++){
					bout.write(alpahbet.lookupString(suffix[v][j].key)+"("+df.format((double)suffix[v][j].value/total[i].value)+"), ");
					if(j==4)
						bout.write("\n");
				}
				bout.write("\n\n");
			}
			bout.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void statisticBeginAndEndWithPU(){
		for(int i=0;i<N;i++)
			for(int j=0;j<N;j++){
				prefix[i][j]=new Parts();
				prefix[i][j].key=j;
				prefix[i][j].value=0;
				suffix[i][j]=new Parts();
				suffix[i][j].key=j;
				suffix[i][j].value=0;
			}
		for(int i=0;i<N;i++){
			total[i]=new Parts();
			total[i].key=i;
			total[i].value=0;
		}
		
		String fileName="./tmp/POS_tag_dataset.txt";
		File file=new File(fileName);
		FileReader fileReader=null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		
		BufferedReader reader=new BufferedReader(fileReader);

		String str;
		try {
			while((str=reader.readLine())!=null){
				System.out.println(str);
				List<String> posList=new ArrayList<String>();
				posList.add("PU");
				int l=-1;
				while(l<str.length()-1){
					int r;
					r=str.indexOf(' ', l+1);
					if(r==-1)
						r=str.length();
					if(r-l>1 && !(posList.size()==1 && str.substring(l+1, r).equals("PU")))
						posList.add(str.substring(l+1, r));
					l=r;
				}
				if(!posList.get(posList.size()-1).equals("PU"))
					posList.add("PU");
				
				int preIdx=-1;
				for(int i=0;i<posList.size();i++){
					int idx;
					String c = String.valueOf(posList.get(i));
					idx = alpahbet.lookupIndex(c);
					
					total[idx].value++;
					if(i>0){
						prefix[idx][preIdx].value++;
						suffix[preIdx][idx].value++;
					}
					preIdx=idx;
				}
			}
			reader.close();
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
		int num=alpahbet.size();
		for(int i=0;i<num;i++)
			for(int j=i+1;j<num;j++)
				if(total[i].value<total[j].value){
					Parts temp=total[i];
					total[i]=total[j];
					total[j]=temp;
				}
		int sum=0;
		for(int i=0;i<num;i++)
			sum+=total[i].value;
		for(int t=0;t<num;t++){
			for(int i=0;i<num;i++)
				for(int j=i+1;j<num;j++)
					if(prefix[t][i].value<prefix[t][j].value){
						Parts temp=prefix[t][i];
						prefix[t][i]=prefix[t][j];
						prefix[t][j]=temp;
					}

			for(int i=0;i<num;i++)
				for(int j=i+1;j<num;j++)
					if(suffix[t][i].value<suffix[t][j].value){
						Parts temp=suffix[t][i];
						suffix[t][i]=suffix[t][j];
						suffix[t][j]=temp;
					}
		}
        DecimalFormat df = new DecimalFormat("#.000");
		try {
			fileName="./tmp/statistics_PU.txt";
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(
					fos, "UTF8"));
			for(int i=0;i<num;i++){
				int v=total[i].key;
				if(total[i].value==0)
					break;
				bout.write(alpahbet.lookupString(v)+" "+total[i].value+"("+df.format((double)total[i].value/sum)+")\nprefix:\n");
				for(int j=0;j<(num<10?num:10);j++){
					bout.write(alpahbet.lookupString(prefix[v][j].key)+"("+df.format((double)prefix[v][j].value/total[i].value)+"), ");
					if(j==4)
						bout.write("\n");
				}
				bout.write("\nsuffix:\n");
				for(int j=0;j<(num<10?num:10);j++){
					bout.write(alpahbet.lookupString(suffix[v][j].key)+"("+df.format((double)suffix[v][j].value/total[i].value)+"), ");
					if(j==4)
						bout.write("\n");
				}
				bout.write("\n\n");
			}
			bout.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	static public void main(String args[]){
		Statistics sta=new Statistics();
		sta.statistic();
		sta.statisticBeginAndEndWithPU();
	}
}
