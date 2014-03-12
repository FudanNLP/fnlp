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

package org.fnlp.nlp.cn.ner.ne;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串预处理模块，为分析器TimeNormalizer提供相应的字符串预处理服务
 * 
 * @author 曹零07300720158
 *
 */
public class PreProcessor {

    /**
     * 该方法删除一字符串中所有匹配某一规则字串
     * 可用于清理一个字符串中的空白符和语气助词
     * 
     * @param target 待处理字符串
     * @param rules 删除规则
     * @return 清理工作完成后的字符串
     */
    public static String delKeyword(String target, String rules) {
        Pattern p = Pattern.compile(rules);
        Matcher m = p.matcher(target);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            m.appendReplacement(sb, "");
            result = m.find();
        }
        m.appendTail(sb);
        String s = sb.toString();
        //System.out.println("字符串："+target+" 的处理后字符串为：" +sb);
        return s;
    }

    /**
     * 该方法可以将字符串中所有的用汉字表示的数字转化为用阿拉伯数字表示的数字
     * 如"这里有一千两百个人，六百零五个来自中国"可以转化为
     * "这里有1200个人，605个来自中国"
     * 此外添加支持了部分不规则表达方法
     * 如两万零六百五可转化为20650
     * 两百一十四和两百十四都可以转化为214
     * 一六零加一五八可以转化为160+158
     * 该方法目前支持的正确转化范围是0-99999999
     * 该功能模块具有良好的复用性
     * 
     * @param target 待转化的字符串
     * @return 转化完毕后的字符串
     */
    public static String numberTranslator(String target) {
        Pattern p = Pattern.compile("[一二两三四五六七八九123456789]万[一二两三四五六七八九123456789](?!(千|百|十))");
        Matcher m = p.matcher(target);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("万");
            int num = 0;
            if (s.length == 2) {
                num += wordToNumber(s[0]) * 10000 + wordToNumber(s[1]) * 1000;
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("[一二两三四五六七八九123456789]千[一二两三四五六七八九123456789](?!(百|十))");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("千");
            int num = 0;
            if (s.length == 2) {
                num += wordToNumber(s[0]) * 1000 + wordToNumber(s[1]) * 100;
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("[一二两三四五六七八九123456789]百[一二两三四五六七八九123456789](?!十)");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("百");
            int num = 0;
            if (s.length == 2) {
                num += wordToNumber(s[0]) * 100 + wordToNumber(s[1]) * 10;
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("[零一二两三四五六七八九]");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("(?<=(周|星期))[天日]");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("(?<!(周|星期))0?[0-9]?十[0-9]?");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("十");
            int num = 0;
            if (s.length == 0) {
                num += 10;
            } else if (s.length == 1) {
                int ten = Integer.parseInt(s[0]);
                if (ten == 0) {
                    num += 10;
                } else {
                    num += ten * 10;
                }
            } else if (s.length == 2) {
                if (s[0].equals("")) {
                    num += 10;
                } else {
                    int ten = Integer.parseInt(s[0]);
                    if (ten == 0) {
                        num += 10;
                    } else {
                        num += ten * 10;
                    }
                }
                num += Integer.parseInt(s[1]);
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("0?[1-9]百[0-9]?[0-9]?");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("百");
            int num = 0;
            if (s.length == 1) {
                int hundred = Integer.parseInt(s[0]);
                num += hundred * 100;
            } else if (s.length == 2) {
                int hundred = Integer.parseInt(s[0]);
                num += hundred * 100;
                num += Integer.parseInt(s[1]);
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("0?[1-9]千[0-9]?[0-9]?[0-9]?");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("千");
            int num = 0;
            if (s.length == 1) {
                int thousand = Integer.parseInt(s[0]);
                num += thousand * 1000;
            } else if (s.length == 2) {
                int thousand = Integer.parseInt(s[0]);
                num += thousand * 1000;
                num += Integer.parseInt(s[1]);
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        p = Pattern.compile("[0-9]+万[0-9]?[0-9]?[0-9]?[0-9]?");
        m = p.matcher(target);
        sb = new StringBuffer();
        result = m.find();
        while (result) {
            String group = m.group();
            String[] s = group.split("万");
            int num = 0;
            if (s.length == 1) {
                int tenthousand = Integer.parseInt(s[0]);
                num += tenthousand * 10000;
            } else if (s.length == 2) {
                int tenthousand = Integer.parseInt(s[0]);
                num += tenthousand * 10000;
                num += Integer.parseInt(s[1]);
            }
            m.appendReplacement(sb, Integer.toString(num));
            result = m.find();
        }
        m.appendTail(sb);
        target = sb.toString();

        return target;
    }

    /**
     * 方法numberTranslator的辅助方法，可将[零-九]正确翻译为[0-9]
     * 
     * @param s 大写数字
     * @return 对应的整形数，如果不是大写数字返回-1
     */
    private static int wordToNumber(String s) {
        if (s.equals("零") || s.equals("0")) {
            return 0;
        } else if (s.equals("一") || s.equals("1")) {
            return 1;
        } else if (s.equals("二") || s.equals("两") || s.equals("2")) {
            return 2;
        } else if (s.equals("三") || s.equals("3")) {
            return 3;
        } else if (s.equals("四") || s.equals("4")) {
            return 4;
        } else if (s.equals("五") || s.equals("5")) {
            return 5;
        } else if (s.equals("六") || s.equals("6")) {
            return 6;
        } else if (s.equals("七") || s.equals("天") || s.equals("日") || s.equals("7")) {
            return 7;
        } else if (s.equals("八") || s.equals("8")) {
            return 8;
        } else if (s.equals("九") || s.equals("9")) {
            return 9;
        } else {
            return -1;
        }
    }
}