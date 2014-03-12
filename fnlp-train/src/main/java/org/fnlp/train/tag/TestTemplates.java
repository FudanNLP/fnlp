package org.fnlp.train.tag;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.fnlp.nlp.tag.Tagger;

/**
 * 提供多套模板，对每套模板训练并得到测试结果
 * 
 * @author zliu
 * 
 */
public class TestTemplates {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String corpusDir = "/home/zliu/corpus/sighan2007/ctb/";
		String templateDir = "/home/zliu/corpus/testTemplate/templates/";
		String modelDir = "/home/zliu/corpus/testTemplate/models/";
		File dir = new File(templateDir);
		if (dir.isDirectory()) {
			File[] filelist = dir.listFiles();
			for (File file : filelist) {
				System.out.println("Starting train template " + file.getName() + " ...");
				String templateName = file.getName();
				String templateFile = file.getPath();
				String trainFile = corpusDir + "train.txt";
				String testFile = corpusDir + "test.txt";
				String modelFile = modelDir + templateName + ".gz";
				String prcessOutputFile = modelDir + templateName + ".result";
				String resultFile = modelDir + "result";
				// 重定向
				PrintStream out = new PrintStream(new BufferedOutputStream(
						new FileOutputStream(prcessOutputFile)), true);
				System.setOut(out);
				String[] arguments = new String[]{"-train",templateFile,trainFile,modelFile,testFile,resultFile};
				Tagger.main(arguments);
				out.close();
				System.setOut(System.out);
				System.out.println("Train template " + file.getName() + "ended.");
				System.out.println();

			}
		} else {
			System.out.println(templateDir + "is not a directory!");
			return;
		}
	}

}
