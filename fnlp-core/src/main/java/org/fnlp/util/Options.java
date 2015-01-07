/**
 * 
 */
package org.fnlp.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Xipeng Qiu  E-mail: xpqiu@fudan.edu.cn
 * @version 创建时间：2015年1月6日 下午4:39:23
 * @since fnlp 2.1
 */
public class Options {
	HashMap<String,String> options = new HashMap<String,String>();
	ArrayList<String> rootArgs = new ArrayList<String>();
	
	
	public void parsing(String[] args) {
		
		for (int i=0; i<args.length; ++i) {
			if (args[i].charAt(0) != '-') {
				rootArgs.add(args[i]);
			} else if (i+1 < args.length) {
				options.put(args[i], args[++i]);
			} else {
				throw new IllegalArgumentException();
			}
		}
	}

}
