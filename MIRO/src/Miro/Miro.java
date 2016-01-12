package Miro;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Miro extends JFrame {
  private JTextArea output;
  private JLabel title;
  private JScrollPane scrollPane;
  
  public static Miro miro = null;

  private static final long serialVersionUID = 1L;
  static final int WIDTH = 1000;
  static final int HEIGHT = 800;
  
  public Miro() throws ClassNotFoundException, IOException, SQLException {
    init();
    actionMiro();
  }
  
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口，点击右上角关闭按钮的时候退出程序
    setTitle("Miro Server"); 
    
    GridBagLayout lay = new GridBagLayout();  // GridBag布局
    setLayout(lay);
    
    setSize(WIDTH, HEIGHT); // 设置宽高
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize(); // 取得电脑屏幕大小
    
    int width=screenSize.width;
    int height=screenSize.height;
    int x = (width - WIDTH) / 2;
    
    int y = (height - HEIGHT) / 2;
    
    setLocation(x, y); // 设置窗口居中
  }
  
  public void actionMiro() {
    title = new JLabel("Miro Server");
    title.setFont(new Font("", Font.BOLD, 30));
    
    add(title, new GBC(0, 0).setSpan(1, 1).setWeight(1, 1).setAnchor(GBC.CENTER));
    
    output = new JTextArea(35, 140);
    output.setForeground(Color.blue);
    
    scrollPane = new JScrollPane(output);
    
    add(scrollPane, new GBC(0, 1).setSpan(1, 1).setWeight(2, 2).setAnchor(GBC.CENTER));
    
    setVisible(true);
    setResizable(false);
  }
  
  public void miroPrint(String str) {
    output.append(str);  
    output.append("\n");  
    output.setCaretPosition(output.getDocument().getLength()); 
  }
  
}
