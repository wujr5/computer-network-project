package Client;

import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Minet.Minet;
import MinetUI.SharedScreen;

/**
 * ��Ļ����ͻ���
 * @author jiarong
 *
 */
public class ScreenShareClient extends Thread {
  public SharedScreen frame;
  public Socket socket;
  public String IP;
    
  public static void main(String[] args) {
    new ScreenShareClient(new SharedScreen(), "127.0.0.1").start();
  }
  
  public ScreenShareClient(SharedScreen frame, String IP) {
    this.frame = frame;
    this.IP = IP;
  }
 
  public void run() {
    while(frame.getFlag()){
      try {
        socket = new Socket(IP, 8000);
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
        Minet.minetMainInterface.shareScreen.doClick(); // ģ�����ر����Ӱ�ť
      } finally {
        try {
          socket.close();
        } catch (IOException e) {}  
      }
    }   
  }
}
