package org.fnlp.app.drawTree;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.fudan.nlp.cn.CNFactory;
import edu.fudan.nlp.cn.CNFactory.Models;
import edu.fudan.nlp.parser.dep.DependencyTree;
import edu.fudan.util.exception.LoadModelException;

public class DrawTreeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDrawDependencyTreeString() throws LoadModelException {
		String path = "./tmp.png";
		String s  ="进行全面的分析和比较";
		CNFactory factory = CNFactory.getInstance("./models/", Models.ALL);
		DependencyTree tree = factory.parse2T(s);
		DrawTree dt = new DrawTree();
		try {
			dt.draw(tree,path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Test
	public void testDrawArrayListOfListOfStringString() {
		fail("Not yet implemented");
	}

}
