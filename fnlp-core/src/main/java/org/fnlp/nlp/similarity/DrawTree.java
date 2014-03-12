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

package org.fnlp.nlp.similarity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DrawTree {

	private static int wunit  = 300;
	private static int hunit  = 20;
	private static int width;
	private static int height;
	
	// 打印哈夫曼树  
	public static void printTree(Cluster a,String file) {  

		int depth = getDepth(a);
	    
	    width =  wunit*(depth+1);
		height = hunit*(depth+1);
		BufferedImage image  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image .createGraphics();
		
		g.setColor(new Color(0,0,0)); 
		g.setStroke(new BasicStroke(1)); 
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("宋体", Font.BOLD, 20); 
		
		g.setFont(font);   
		
		drawTree(a, g, width/2, 0 , 1);
		//释放对象 
		g.dispose(); 
		// 保存文件    
		try {
			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}  
	 	
	private static int getDepth(Cluster a) {
		int d1=0;
		int d2 = 0;
		if (a.getLeft() != null){
			d1 = getDepth(a.getLeft());
		}
		if (a.getRight() != null){
			d2 = getDepth(a.getRight());
		}
		return 1+ Math.max(d1, d2);
	}

	// 画树 
	public static void drawTree(Cluster a, Graphics2D g, int x, int y, int level) {  
	    level++;  
	    g.setPaint(Color.RED);
	    g.drawString(a.getN(), x-wunit/2, y+hunit/2);  
	    int newx = width/level/4;
	    if (a.getLeft() != null) {  
	    	g.setPaint(Color.BLACK);
	        g.drawLine(x, y, x - newx , y + hunit);  
	        drawTree(a.getLeft(), g, x - newx, y + hunit,  level);  
	    }  
	    if (a.getRight() != null) {  
	    	g.setPaint(Color.BLACK);
	        g.drawLine(x, y, x + newx, y + hunit);  
	        drawTree(a.getRight(), g, x + newx, y +hunit, level);  
	    }  
	    
	}


}