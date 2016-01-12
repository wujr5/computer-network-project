package Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

///这是一个登录类。设计成一个继承容器的类。
///WIDTH是指整个顶层框架的宽度。
///HEIGHT是指整个顶层框架的长度。

class TestJpanel extends JPanel {

  private static final long serialVersionUID = 1L;
  static final int WIDTH = 600;
  static final int HEIGHT = 650;
  
  JFrame loginframe;
  
  ///按照网格组布局方式排列组件的方法
  ///x指控件位于第几列。
  ///y指控件位于第几行。
  ///w指控件需要占几列。
  ///h指控件需要占几行。
  
  public void add(Component c, GridBagConstraints constraints, 
      int x,int y,int w,int h) {
    
    constraints.gridx=x;
    constraints.gridy=y;
    constraints.gridwidth=w;
    constraints.gridheight=h;
    
    add(c,constraints);
  }
  
  //此方法用来添加控件到容器中
  ///这是一个构造器方法
  ///loginframe是就是指这个界面的框架
  /// setDefaultCloseOperation这是一个使得窗口上面的关闭控件有效的类库方法
  /// lay是一个网格组布局管理器的对象。
  /// nameinput是用来输入用户名的文本域。
  /// passwordinput是用来输入密码的文本域。
  /// title是用来显示标题的标签。
  /// name是用来显示“姓名”的标签。
  ///password是用来显示“密码”的标签。
  ///ok是一个按钮，使进入系统。
  ///cancel是一个按钮，使退出界面和系统。
  /// ok.addActionListener是一个进入系统动作事件监听方法。
  ///cancel.addActionListener是一个退出系统和界面动作事件的监听方法。
  
  TestJpanel() {
    loginframe = new JFrame("Minet Client"); 
    loginframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    GridBagLayout lay = new GridBagLayout();  
    setLayout(lay);
    
    loginframe.add(this, BorderLayout.WEST);
    loginframe.setSize(WIDTH,HEIGHT);
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    
    int width=screenSize.width;
    int height=screenSize.height;
    int x=(width-WIDTH)/2;
    int y=(height-HEIGHT)/2;
    
    loginframe.setLocation(x,y);
    
    JButton ok = new JButton("登录");
    JButton cancel = new JButton("放弃");
    JLabel title = new JLabel("欢迎进入Java世界");
    JLabel name = new JLabel("用户名");
    JLabel password = new JLabel("密 码");
    
    final JTextField nameinput = new JTextField(15);
    final JTextField passwordinput = new JTextField(15);
    GridBagConstraints constraints = new GridBagConstraints();
    
    constraints.fill = GridBagConstraints.NONE;
    constraints.anchor = GridBagConstraints.EAST;
    constraints.weightx = 3;
    constraints.weighty = 4;
    
    add(title,constraints,0,0,4,1);                 //使用网格组布局添加控件
    add(name,constraints,0,1,1,1);
    add(password,constraints,0,2,1,1);
    add(nameinput,constraints,2,1,1,1);
    add(passwordinput,constraints,2,2,1,1);
    add(ok,constraints,0,3,1,1);
    add(cancel,constraints,2,3,1,1);
    
    loginframe.setResizable(true);
    loginframe.setVisible(true); 
  }
  
//  public static void main(String[] args) {
//    HelloWorld hello = new HelloWorld();
//  }
}
