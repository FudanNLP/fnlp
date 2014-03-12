package org.fnlp.nlp.similarity;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.fnlp.nlp.cn.CNFactory;
import org.fnlp.nlp.cn.CNFactory.Models;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.ontology.graph.WordGraph;
import org.fnlp.util.MyStrings;

public class TreeKernelTest {
	
	CNFactory factory;
	TreeKernel tk;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		factory = CNFactory.getInstance("./models",Models.ALL);
		WordGraph wg = new WordGraph();
		
		wg.read("./models/wordgraph.txt");
		tk = new TreeKernel();
		tk.setWordGraph(wg);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalc() throws Exception {
		Vector<String[]> s = new Vector<String[]>();
		s.add(new String[]{"听歌","唱歌"});
		s.add(new String[]{"听歌","听个歌"});
		s.add(new String[]{"听下面的歌","听周杰伦的歌"});
		s.add(new String[]{"听吴世宇的歌","听周杰伦的歌"});
		s.add(new String[]{"要导航","我要导航"});
		s.add(new String[]{"我想去人民广场","我想到人民广场"});
		s.add(new String[]{"我要去肯德基","我要去麦当劳"});
		s.add(new String[]{"我要去肯德基","我要先去肯德基"});
		s.add(new String[]{"发短信","发一条短信"});
		s.add(new String[]{"我想去南京东路","我想去#POI#"});
		s.add(new String[]{"播放流行的歌","我想听流行的歌"});
		s.add(new String[]{"拍张照片","拍照"});
		for(int i = 0; i < s.size(); i++){
			float score = tk.calc(factory.parse2T(s.get(i)[0]), factory.parse2T(s.get(i)[1]));
			System.out.println(MyStrings.toString(s.get(i), " ") );
			System.out.println(score );
		}
		
	}

}
