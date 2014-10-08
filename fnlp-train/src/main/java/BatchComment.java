

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
/**
 * 批量修改文件注释
 * @author Xipeng
 *
 */
public class BatchComment {

	static int count = 0;
	public static void commentFile(File file, String comments) {

		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				String[] children = file.list();
				for (int i = 0; i < children.length; i++) {
					File child = new File(file.getPath() + System.getProperty("file.separator") + children[i]);
					commentFile(child, comments);
				}
			} else {
				String filename = file.getName().toLowerCase();

				if (filename.endsWith(".java")&&!filename.equals("package-info.java")) {
					System.out.println(file.getName());                   
					count++;
					try {
						RandomAccessFile raFile = new RandomAccessFile(file, "rw");
						byte[] content = new byte[ (int) raFile.length()];
						raFile.readFully(content);
						String all = new String(content);
						all = all.trim();
						while (all.startsWith("\n")) {
							all = all.substring(1);
						}
						if (all.indexOf("package") != -1) {
							all = all.substring(all.indexOf("package"));
						}
						if (all.indexOf("import") != -1) {
							all = all.substring(all.indexOf("package"));
						}
						all = comments + "\n" + all;
						raFile.close();
						FileWriter writer = new FileWriter(file);
						writer.write(all);
						writer.close();
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String comFile = "PageHeader.txt";
		BufferedReader cf = new BufferedReader(new InputStreamReader(
				new FileInputStream(comFile), "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		sb.append("/**\n");
		while((line = cf.readLine()) != null)	{
			sb.append("*  ").append(line).append("\n");
		}
		sb.append("*/\n");
		cf.close();
		String comments= sb.toString();
		File f = new File("./fnlp-core/");
		commentFile(f, comments);

		System.out.print(count);

	}

}
