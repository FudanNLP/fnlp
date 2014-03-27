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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.fnlp.app.lucene.FNLPAnalyzer;
import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
import org.fnlp.util.exception.LoadModelException;


public class Search {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws LoadModelException 
	 */
	public static void main(String[] args) throws IOException, ParseException, LoadModelException {
		String indexPath = "../tmp/lucene";
		System.out.println("Index directory '" + indexPath);
		Date start = new Date();
		Directory dir = FSDirectory.open(new File(indexPath));
		//需要先初始化 CNFactory
		CNFactory factory = CNFactory.getInstance("../models",Models.SEG_TAG);
		Analyzer analyzer = new FNLPAnalyzer(Version.LUCENE_47);
		// Now search the index:
		DirectoryReader ireader = DirectoryReader.open(dir);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		// Parse a simple query that searches for "text":
		QueryParser parser = new QueryParser(Version.LUCENE_47, "content", analyzer);
		Query query = parser.parse("保修费用");
		ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
		
		System.out.println("Hello World");
		// Iterate through the results:
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("content"));
			System.out.println(hits[i].score);
			
		}
		
		
		
		ireader.close();
		dir.close();
	}
}