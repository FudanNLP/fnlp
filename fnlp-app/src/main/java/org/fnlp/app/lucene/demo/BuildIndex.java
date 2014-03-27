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

package org.fnlp.app.lucene.demo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.fnlp.app.lucene.FNLPAnalyzer;
import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
import org.fnlp.util.exception.LoadModelException;


public class BuildIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws LoadModelException 
	 */
	public static void main(String[] args) throws IOException, LoadModelException {
		String indexPath = "../tmp/lucene";
		System.out.println("Indexing to directory '" + indexPath  + "'...");
		Date start = new Date();
		Directory dir = FSDirectory.open(new File(indexPath));//Dirctory dir-->FSDirectory
		//需要先初始化 CNFactory
		CNFactory factory = CNFactory.getInstance("../models",Models.SEG_TAG);
		Analyzer analyzer = new FNLPAnalyzer(Version.LUCENE_47);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		IndexWriter writer = new IndexWriter(dir, iwc);

		String[] strs = new String[]{
				"终端的保修期为一年。",
				"凡在保修期内非人为损坏，均可免费保修。",
				"人为损坏的终端将视情况收取维修费用。",
				"中国"
		};
		//Date start = new Date();
		for(int i=0;i<strs.length;i++){

			Document doc = new Document();

			Field field = new TextField("content", strs[i] , Field.Store.YES);
			doc.add(field);
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
				writer.addDocument(doc);
			} else {
				writer.updateDocument(new Term("content",strs[i]), doc);
			}
		}
		writer.close();
		
		//！！这句话是不是漏了
		//dir.close();
		//！！这句话是不是漏了

		Date end = new Date();
		System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	}

}