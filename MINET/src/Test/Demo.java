package Test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Demo extends JFrame {

  public Demo(){
    this.setSize(300, 220);

    this.setLayout(new BorderLayout());

    this.setVisible(true);
    Container con = this.getContentPane();
    con.setLayout(new BorderLayout());
    JPanel panel=new JPanel(); 
    BoxLayout layout=new BoxLayout(panel, BoxLayout.Y_AXIS); 
    panel.setLayout(layout);

    JLabel jl29=new JLabel("设置选课截止时间：");
    JLabel jl30=new JLabel("-");
    JLabel jl31=new JLabel("-");
    JTextField jtf23=new JTextField(4);
    JTextField jtf24=new JTextField(2);
    JTextField jtf25=new JTextField(2);
    JPanel p29 = new JPanel();
    p29.setLayout(new BorderLayout());
    p29.add(new JPanel(), BorderLayout.WEST);
    p29.add(jl29, BorderLayout.EAST);
    JPanel p30 = new JPanel();
    p30.setLayout(new BorderLayout());
    p30.add(new JPanel(), BorderLayout.WEST);
    p30.add(jl30, BorderLayout.EAST);
    JPanel p31 = new JPanel();
    p31.setLayout(new BorderLayout());
    p31.add(new JPanel(), BorderLayout.WEST);
    p31.add(jl31, BorderLayout.EAST);
    JPanel p23 = new JPanel();
    p23.setLayout(new BorderLayout());
    p23.add(new JPanel(), BorderLayout.WEST);
    p23.add(jtf23, BorderLayout.EAST);
    JPanel p24 = new JPanel();
    p24.setLayout(new BorderLayout());
    p24.add(new JPanel(), BorderLayout.WEST);
    p24.add(jtf24, BorderLayout.EAST);
    JPanel p25 = new JPanel();
    p25.setLayout(new BorderLayout());
    p25.add(new JPanel(), BorderLayout.WEST);
    p25.add(jtf25, BorderLayout.EAST);

    panel.add(p29);
    panel.add(p30);
    panel.add(p31);
    panel.add(p23);
    panel.add(p24);
    panel.add(p25);
    
    con.add(panel, BorderLayout.CENTER);
  }
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    new Demo();
  }

}
