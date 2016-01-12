package MinetUI;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import Client.TCPClient;
import Minet.Minet;

//Client�˴��ڸ����࣬ר��������ʾ�ӷ�����յ�����Ļ��Ϣ
public class SharedScreen extends JFrame {
  private static final long serialVersionUID = 1L;
  public JLabel jlbImg;
  private boolean flag;

  public boolean getFlag() {
    return this.flag;
  }

  public SharedScreen() {
    this.flag=true;
    this.jlbImg = new JLabel();
    this.setTitle("Զ�̼��--IP:"  + "--����:" );
    this.setSize(400, 400);
    //this.setUndecorated(true);  //ȫ����ʾ������ʱ���ע�͵�
    //this.setAlwaysOnTop(true);  //��ʾ����ʼ������ǰ��
    this.add(jlbImg);
    this.setLocationRelativeTo(null);
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setVisible(true);
    this.validate();
   
    //���ڹر��¼�
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        flag=false;
        SharedScreen.this.dispose();
        System.out.println("����ر�");
        System.gc();    //��������
        
        Minet.minetMainInterface.shareScreen.doClick();
      }
    });
  }
}
