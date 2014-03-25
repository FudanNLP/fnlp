package org.fnlp.app.num;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
class MyFrame extends JFrame{
	protected JTextArea textIn;
	protected JScrollPane jspIn;
	protected JTextArea textOut;
	protected JScrollPane jspOut;
	private CNExpression expr=new CNExpression();
	JButton button;
	MyFrame(){
		setTitle("中文算式识别");
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setSize(400, 300);
		setLocation((screenSize.width-getWidth())/2,(screenSize.height-getHeight())/2);
		init();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	void init(){		
		textIn=new JTextArea();
		textIn.setLineWrap(false);
		textIn.setWrapStyleWord(true);
		jspIn=new JScrollPane(textIn);
		//jspIn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		textOut=new JTextArea();
		textOut.setLineWrap(false);
		textOut.setWrapStyleWord(true);
		textOut.setEditable(false);
		textOut.setText("");
		jspOut=new JScrollPane(textOut);
		jspOut.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//jspOut.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		Panel panel=new Panel();
		panel.setLayout(null);
		int sp=3;
		int width=getWidth()-15;
		int height=getHeight()-50;
		jspOut.setBounds(sp, sp, width-sp*2, height*5/6);
		jspIn.setBounds(sp, height*5/6+sp*2, width*4/5-sp*2, height*1/6);
		panel.add(jspIn);
		panel.add(jspOut);
		
		button=new JButton("发送");
		button.addActionListener(new ButtonListener());
		button.setBounds(width*4/5+sp, height*5/6+sp*2, width*1/5-sp*2, height*1/6);
		panel.add(button);
		
		add(panel);
	}
	class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(e.getActionCommand().equals("发送")){
				String str;
				str=textIn.getText();
				str.intern();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String ly_time = sdf.format(new java.util.Date());
				textOut.append("User "+ly_time+"\n");
				textOut.append(str);
				textOut.append("\n");
				String str2;
				if(expr.setExpr(str)){
					expr.calculate();
					str2=expr.getAnswerInChn();
				}
				else str2=new String("不能识别请重新输入");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ly_time = sdf.format(new java.util.Date());
				textOut.append("System "+ly_time+"\n");
				textOut.append(str2);
				textOut.append("\n");
			}
		}
	}
}

public class Test {
	public static void main(String args[]){
		MyFrame frame=new MyFrame();
		
	}
}
