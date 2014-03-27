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