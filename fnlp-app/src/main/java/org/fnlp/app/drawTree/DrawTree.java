package org.fnlp.app.drawTree;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import control.ViewUtil;
import edu.fudan.nlp.parser.dep.DependencyTree;
/**
 * 图形化显示依存句法树
 * @author xpqiu,fxx
 *
 */
public class DrawTree {
	ArrayList<List<String>> al;
	private String path;
	private BufferedImage image;
	public DrawTree() {
		final JFXPanel fxPanel = new JFXPanel();
		System.out.println( Platform.isFxApplicationThread());
	}
	public BufferedImage draw(ArrayList<List<String>> al){
		this.al = al;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initAndShowGUI();
			}
		});

		while(image==null){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return image;

	}
	
	
	/**
	 * 将图形化树保存到文件
	 * @param al
	 * @param path
	 * @throws IOException 
	 */
	public void draw(DependencyTree dt, String path) throws IOException {
		al = dt.toList();
		draw(al);
		ImageIO.write(image, "png", new File(path));
		image=null;

	}
	/**
	 * 将图形化树保存到文件
	 * @param al
	 * @param path
	 * @throws IOException 
	 */
	public void draw(ArrayList<List<String>> al, String path) throws IOException {
		draw(al);
		ImageIO.write(image, "png", new File(path));
		image=null;


	}
	private void initAndShowGUI() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println( Platform.isFxApplicationThread());

				image = ViewUtil.createImage(al);
			}
		});
		System.out.println( Platform.isFxApplicationThread());
	}


	public static void main(String[] args) throws IOException {
		ArrayList<List<String>> al = new ArrayList<List<String>>();
		al.add(Arrays.asList(new String[]{"我是","v","2"	,"a"}));
		al.add(Arrays.asList(new String[]{"一头","n"	,"0","b"}));
		al.add(Arrays.asList(new String[]{"来自"	,"vvv","-1","c"}));
		al.add(Arrays.asList(new String[]{"北方的熊","n","0","d"}));
		String path = "./tmp.png";
		DrawTree dt = new DrawTree();
		dt.draw(al,path);
		
		dt.draw(al,path+".png");
		System.exit(0);

	}



}