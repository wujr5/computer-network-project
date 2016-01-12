package Test;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * ��Ļ����ͻ���
 * @author jiarong
 *
 */
public class ScreenShareClient extends Thread {
  public BorderInit frame;
  public Socket socket;
  public String IP;
    
  public static void main(String[] args) {
    new ScreenShareClient(new BorderInit(), "127.0.0.1").start();
  }
  
  public ScreenShareClient(BorderInit frame, String IP) {
    this.frame = frame;
    this.IP = IP;
  }
 
  public void run() {
    while(frame.getFlag()){
      try {
        socket = new Socket(IP,8000);
        DataInputStream ImgInput = new DataInputStream(socket.getInputStream());
        ZipInputStream imgZip = new ZipInputStream(ImgInput);
             
        imgZip.getNextEntry();             //��Zip�ļ����Ŀ�ʼ��
        Image img = ImageIO.read(imgZip);  //�����ֽڶ�ȡZipͼƬ�������ͼƬ
        frame.jlbImg.setIcon(new ImageIcon(img));
        System.out.println("���ӵ�" + (System.currentTimeMillis() / 1000) % 24 % 60 + "��");
        
        frame.validate();
        TimeUnit.MILLISECONDS.sleep(50);// ����ͼƬ���ʱ��
        imgZip.close();
      } catch (IOException | InterruptedException e) {
        System.out.println("���ӶϿ�");
      } finally {
        try {
          socket.close();
        } catch (IOException e) {}  
      }
    }   
  }
}
 
//Client�˴��ڸ����࣬ר��������ʾ�ӷ�����յ�����Ļ��Ϣ
class BorderInit extends JFrame {
  private static final long serialVersionUID = 1L;
  public JLabel jlbImg;
  private boolean flag;
  
  public boolean getFlag() {
    return this.flag;
  }
  
  public BorderInit() {
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
        BorderInit.this.dispose();
        System.out.println("����ر�");
        System.gc();    //��������
      }
    });
  }
}
