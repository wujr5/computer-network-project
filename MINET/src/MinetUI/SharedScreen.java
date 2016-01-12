package MinetUI;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import Client.TCPClient;
import Minet.Minet;

//Client端窗口辅助类，专门用来显示从服务端收到的屏幕信息
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
    this.setTitle("远程监控--IP:"  + "--主题:" );
    this.setSize(400, 400);
    //this.setUndecorated(true);  //全屏显示，测试时最好注释掉
    //this.setAlwaysOnTop(true);  //显示窗口始终在最前面
    this.add(jlbImg);
    this.setLocationRelativeTo(null);
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setVisible(true);
    this.validate();
   
    //窗口关闭事件
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        flag=false;
        SharedScreen.this.dispose();
        System.out.println("窗体关闭");
        System.gc();    //垃圾回收
        
        Minet.minetMainInterface.shareScreen.doClick();
      }
    });
  }
}
