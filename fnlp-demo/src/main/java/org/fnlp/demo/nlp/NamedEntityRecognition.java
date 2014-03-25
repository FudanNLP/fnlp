package org.fnlp.demo.nlp;


import java.util.HashMap;

import org.fnlp.nlp.cn.tag.NERTagger;

/**
 * 实体名识别使用示例
 * @author xpqiu
 *
 */
public class NamedEntityRecognition {	


	/**
	 * 主程序
	 * @param args
	 * @throws IOException 
	 * @throws  
	 */
	public static void main(String[] args) throws Exception {
		
		NERTagger tag = new NERTagger("./models/seg.m","./models/pos.m");
		String str = " 新浪体育讯　北京时间4月15日03:00(英国当地时间14日20:00)，2009/10赛季英格兰足球超级联赛第34轮一场焦点战在白鹿巷球场展开角逐，阿森纳客场1比2不敌托特纳姆热刺，丹尼-罗斯和拜尔先入两球，本特纳扳回一城。阿森纳仍落后切尔西6分(净胜球少15个)，夺冠几成泡影。热刺近 7轮联赛取得6胜，继续以1分之差紧逼曼城。";
		HashMap<String, String> map = new HashMap<String, String>();
		tag.tag(str,map);
		System.out.println(map);
		map = tag.tagFile("./example-data/data-tag.txt");
		System.out.println(map);
		System.out.println("Done!");
	}
}
