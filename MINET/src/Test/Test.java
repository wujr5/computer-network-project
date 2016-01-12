package Test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Test extends JFrame {
  JScrollPane pane;
  JButton button;
  
  public Test() {
    super();
    pane = new JScrollPane(new JTextArea("�� BoxLayout ���в���ʱ���������пؼ����ΰ��տؼ������ȳߴ簴��˳��Ľ���ˮƽ���ߴ�ֱ���ã����粼�ֵ�����ˮƽ���ߴ�ֱ�ռ�ĳߴ粻�ܷ������пؼ�����ô BoxLayout ����ͼ���������ؼ��Ĵ�С������������ֵ�ˮƽ���ߴ�ֱ�ռ䡣\r\n" + 
        "BoxLayout ������ Box ������������һ��ʹ�ã���ô���������ǣ�BoxLayout �ǰѿؼ���ˮƽ���ߴ�ֱ�ķ���һ����һ���ķ��ã����Ҫ������Щ�ؼ�֮��Ŀռ䣬�ͻ���Ҫʹ�� Box �����ṩ��͸���������Ϊ��������ؼ�֮��Ŀռ䣬�Ӷ��ﵽ�����ؼ�֮��ļ���ռ��Ŀ�ġ�Box �����ṩ�� 4 ��͸����������ֱ��� rigid area��strut��glue��filler��Box �����ֱ��ṩ�˲�ͬ�ķ�����������Щ��������ĸ�������ص����£�\r\n" + 
        "Rigid area ��һ���û����Զ���ˮƽ�ʹ�ֱ�ߴ��͸�������\r\n" + 
        "strut �� rigid area ���ƣ������û�ֻ�ܶ���һ������ĳߴ磬��ˮƽ������ߴ�ֱ���򣬲���ͬʱ����ˮƽ�ʹ�ֱ�ߴ磻\r\n" + 
        "���û��� glue ���������ؼ�֮��ʱ�����ᾡ���ܵ�ռ�������ؼ�֮��Ķ���ռ䣬�Ӷ��������ؼ��������ߣ�\r\n" + 
        "Filler �� Box ���ڲ��࣬���� rigid area ���ƣ�������ָ��ˮƽ���ߴ�ֱ�ĳߴ磬����������������С���������ȳߴ硣"));
    button = new JButton("���");
    
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
