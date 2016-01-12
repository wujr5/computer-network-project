package Test;
import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.*;

public class ScrollingToVisible  extends JFrame {
  JTextArea ta = null;
  JLabel pic;
  JScrollPane scroller;
  public ScrollingToVisible() { 
    super("Test");   
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
    ta = new JTextArea(10, 40);  
    pic=new JLabel();
    pic.setIcon(new ImageIcon("sky.jpg"));
    pic.setText("fds");
    scroller = new JScrollPane(ta);
    //scroller = new JScrollPane(pic); //这是关键所在，我的图片是很常的，我要它自动

    //scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 隐藏滚动条 
    //scroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_NEVER); 隐藏滚动条

    getContentPane().add(scroller, BorderLayout.CENTER); 
    setBounds(200,10,410,400);  
    setVisible(true);   
    
    Thread thread = new Thread() {
      public void run() {   
        int i=0;
        while (true) {
          append(" long "+i++);    
          try { 
            Thread.sleep(200);   
          } catch (InterruptedException exp) {}  
        } 
      }
      };
      thread.start(); 
  }  
  
  public void append(String text) { 
    ta.append(text);  
    ta.append("\n");  
    ta.setCaretPosition(ta.getDocument().getLength()); 
  }
  
  public static void main(String[] args){
    new ScrollingToVisible(); 
  } 

}