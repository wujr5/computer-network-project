package Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Test extends JFrame {
  JScrollPane pane;
  JButton button;
  
  public Test() {
    super();
    pane = new JScrollPane(new JTextArea("当 BoxLayout 进行布局时，它将所有控件依次按照控件的优先尺寸按照顺序的进行水平或者垂直放置，假如布局的整个水平或者垂直空间的尺寸不能放下所有控件，那么 BoxLayout 会试图调整各个控件的大小来填充整个布局的水平或者垂直空间。\r\n" + 
        "BoxLayout 往往和 Box 这个容器结合在一起使用，这么做的理由是，BoxLayout 是把控件以水平或者垂直的方向一个接一个的放置，如果要调整这些控件之间的空间，就会需要使用 Box 容器提供的透明的组件作为填充来填充控件之间的空间，从而达到调整控件之间的间隔空间的目的。Box 容器提供了 4 种透明的组件，分别是 rigid area、strut、glue、filler。Box 容器分别提供了不同的方法来创建这些组件。这四个组件的特点如下：\r\n" + 
        "Rigid area 是一种用户可以定义水平和垂直尺寸的透明组件；\r\n" + 
        "strut 与 rigid area 类似，但是用户只能定义一个方向的尺寸，即水平方向或者垂直方向，不能同时定义水平和垂直尺寸；\r\n" + 
        "当用户将 glue 放在两个控件之间时，它会尽可能的占据两个控件之间的多余空间，从而将两个控件挤到两边；\r\n" + 
        "Filler 是 Box 的内部类，它与 rigid area 相似，都可以指定水平或者垂直的尺寸，但是它可以设置最小，最大和优先尺寸。"));
    button = new JButton("点击");
    
    add(pane, BorderLayout.CENTER);
    add(button, BorderLayout.SOUTH);
    
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JScrollBar bar = pane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
      }
    });
  }
  
  public static void main (String[] args) {
    JFrame frame = new Test();
    frame.setVisible(true);
    frame.setBounds(100,100,300,300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
