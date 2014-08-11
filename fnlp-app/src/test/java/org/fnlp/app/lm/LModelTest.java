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

package org.fnlp.app.lm;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class LModelTest {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		//生成NGramPOIModel模型文件，保存到train/NGram.m
//		NGramModel model = new NGramModel();
//		model.build(3, "tmp/poi.dic","tmp/NGrammodel.m");
//		//model.decide_P("tmp/poi.dic","tmp/NGramodel.m");
//////		
//////		model.setProbabilityField(-7);
//		model.load("tmp/NGrammodel.m");
////		System.out.println("perplexity:" + model.computePerplexity("tmp/poi.dic"));
//		System.out.println("人民广场  " + model.compute("人民广场"));
//		System.out.println("人民广场   " + model.isPOI("人民广场"));
//		System.out.println("人民广场  " + model.compute("人民广场"));
//		System.out.println("去人民广场   " + model.isPOI("去人民广场"));
//		
//		System.out.println("--------------------------------------");
		
		
		//生成NGramPOIModel模型文件，保存到train/GoodTuringModel.m
		GTModel gtmodel = new GTModel();
		gtmodel.build(3, "tmp/poi.dic","tmp/GoodTuringModel.m");
//		gtmodel.decide_P("tmp/poi.dic","tmp/GoodTuringModel.m");
//		gtmodel.setProbabilityField(-7.0);
		gtmodel.load("tmp/GoodTuringModel.m");
//		System.out.println("perplexity:" + gtmodel.computePerplexity("tmp/poi.dic"));
		System.out.println("人民广场  " + gtmodel.compute("人民广场"));
		System.out.println("人民广场   " + gtmodel.contains("人民广场"));
		System.out.println("人民广场  " + gtmodel.compute("人民广场"));
		System.out.println("去人民广场   " + gtmodel.contains("去人民广场"));
	}
}