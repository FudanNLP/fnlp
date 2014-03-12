package org.fnlp.wsytry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.fnlp.data.reader.StringReader;
import org.fnlp.nlp.similarity.Cluster;
import org.fnlp.nlp.similarity.DrawTree;
import org.fnlp.nlp.similarity.train.WordCluster;

public class Test {
	static public  void main(String args[]){
		WordCluster wc=new WordCluster();
		//POSCluster wc = new POSCluster();
		wc.slotsize =38;
		/*
		String[] strs = new String[]{"猪肉","狗肉","狗头","鸡头","猪头","鸡肉","鸭头","鸭肉","猪腿","鸡腿","鸭腿"};
		StringReader r = new StringReader(strs );
		wc.read(r);
		*/

		String fileName="./tmp/POS_tag_dataset.txt";
		//fileName="./tmp/test.txt";
		//wc.readPOStag(fileName);
	//	wc.readPOStagBeginAndEndWithPU(fileName);
		System.out.println();
		try {
			Cluster root = wc.startClustering();
			System.out.println(wc.toString());
			DrawTree.printTree(root,"./tmp/t.png");
			wc.saveModel("./tmp/t.m");
			wc.saveTxt("./tmp/t.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}

}
