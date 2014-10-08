package org.fnlp.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MyFilesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testCombineStringStringArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testCombineStringFileArray() throws Exception {
		List<File> files = MyFiles.getAllFiles("../tmp/", ".cws");
		MyFiles.combine("../tmp/all.cws",files.toArray(new File[files.size()]));  
		System.out.println(new Date().toString());
		System.out.println("Done!");
	}

}
