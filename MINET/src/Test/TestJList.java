package Test;
import java.awt.*;
import javax.swing.*;

public class TestJList extends JFrame { 
  JList list;
  
  public TestJList(){
    super("JList������ʾ");
                                //�ļ�������fileName.length / 3
    String[] fileName = new String[]{"���Ʈȥ.txt", "1.02MB", "�ı��ļ�",
        "Java SE.pdf", "1.02MB", "pdf�ļ�",
        "Java OOP.doc", "1.02MB", "Word�ĵ�",
        "Java EE.pdf", "1.02MB", "pdf�ļ�",
        "Java ME.pdf", "1.02MB", "pdf�ļ�"};    
    list = new JList(fileName);
    list.setLayoutOrientation(JList.HORIZONTAL_WRAP);  //���ö�����ʾ
    list.setVisibleRowCount(fileName.length / 3);    //��������
    this.setLayout(new FlowLayout());
    this.add(list);
    this.setSize(300, 300);
    this.setVisible(true);    
  }
  
  public static void main(String[] args) {
    new TestJList();
  }
}