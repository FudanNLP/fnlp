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

package org.fnlp.nlp.corpus;

/**
 * @author Administrator
 * @version 1.0
 * @since 1.0
 */
public class CharSets {
	
	public static String punctuation =  
		"、。·ˉˇ¨〃々—～‖…‘’“”〔〕〈〉《》「」『』〖〗【】±＋－×"+
		"÷∧∨∑∏∪∩∈√⊥‖∠⌒⊙∫∮≡≌≈∽∝≠≮≯≤≥∞∶∵∴∷♂♀°′"+
		"〃℃＄¤￠￡‰§№☆★〇○●◎◇◆回□■△▽⊿▲▼◣◤◢◥▁▂▃▄▅"+
		"▆▇█▉▊▋▌▍▎▏▓※→←↑↓↖↗↘↙〓ⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹ①②③④"+
		"⑤⑥⑦⑧⑨⑩⒈⒉⒊⒋⒌⒍⒎⒏⒐⒑⒒⒓⒔⒕⒖⒗⒘⒙⒚⒛⑴⑵⑶⑷⑸⑹⑺⑻"+
		"⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ！"+
		"”ㄅㄆㄇㄈㄉㄊㄋㄌㄍㄎㄏㄐㄑ"+
		"ㄒㄓㄔㄕㄖㄗㄘㄙㄚㄛㄜㄝㄞㄟㄠㄡㄢㄣㄤㄥㄦㄧㄨㄩ︴﹏﹋﹌—━│┃"+
		"┄┅┆┇┈┉┊┋┌┍┎┏┐┑┒┓└┕┖┗┘┙┚┛├┝┞┟┠┡┢┣┤┥┦"+
		"┧┨┩┪┫┬┭┮┯┰┱┲┳┴┵┶┷┸┹┺┻┼┽┾┿╀╁╂╃╄╅╆╇╈╉"+
		"╊╋⊕㊣㈱曱甴囍∟┅﹊﹍╭╮╰╯_^（：！\t\b\r\"<>`,:~～"+
		"卐℡ぁ＂″ミ灬№＊ㄨ≮≯＋－／∝≌∽≤≥≈＜＞じ"+
		"ぷ┗┛￥￡§я-―¨…‰′〃℅℉№℡∕∝∣═║╒╓╔╕╖╗╘╙╚╛╜╝"+
		"╞╟╠╡╢╣╤╥╦╧╨╩╪╫╬╱╲╳▔▕〆〒〡〢〣〤〥〦〧〨〩㎎㎏㎜"+
		"㎝㎞㎡㏄㏎㏑㏒㏕兀∶﹍﹎"+
		"▄【┻┳═\\/%&';=@#!˙";	
	
	public static String RegexPunc="\\-\\(\\)\\{\\}\\[\\]\\s\\.\\*\\+\\^\\$\\\\\\?\\|";
	
	public static String allRegexPunc = punctuation+RegexPunc;
	
	public static String japanese = "ぁあぃいぅうぇえぉおかがき"+
		"ぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはば"+
		"ぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをん"+
		"ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッ"+
		"ツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユ"+
		"ョヨラリルレロヮワヰヱヲンヴヵヶ";
	public static String unkown = "ΑΒΓΔΕΖΗΘΙΚ∧ΜΝΞΟ∏Ρ∑Τ"+
	"ΥΦΧΨΩαβγδεζηθικλμνξοπρστυφχψω"+
	"АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦ"+
	"ЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчш"+
	"щъыьэюāáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜüêɑńňɡ";
	public static String fullShape = "＃￥％＆’（）＊＋，－．／０１２３４５６７８９：；＜＝＞？＠ＡＢＣＤ"+
		"ＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ＼＾＿‘ａｂｃｄｅｆｇ"+
		"ｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ｛｜｝［］";
	public static String chineseNum = "一二三四五六七八九十";

}