package org.fnlp.demo.nlp;
import org.fnlp.nlp.cn.tag.POSTagger;
import org.fnlp.nlp.parser.dep.DependencyTree;
import org.fnlp.nlp.parser.dep.JointParser;
/**
 * 依存句法分析使用示例
 * @author xpqiu
 *
 */
public class DepParser {

	private static JointParser parser;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		parser = new JointParser("models/dep.m");
		
		System.out.println("得到支持的依存关系类型集合");
		System.out.println(parser.getSupportedTypes());
		
		String word = "中国进出口银行与中国银行加强合作。";
		test(word);

	}

	/**
	 * 只输入句子，不带词性
	 * @throws Exception 
	 */
	private static void test(String word) throws Exception {		
		POSTagger tag = new POSTagger("models/seg.m","models/pos.m");
		String[][] s = tag.tag2Array(word);
		try {
			DependencyTree tree = parser.parse2T(s[0],s[1]);
			System.out.println(tree.toString());
			String stree = parser.parse2String(s[0],s[1],true);
			System.out.println(stree);
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

}