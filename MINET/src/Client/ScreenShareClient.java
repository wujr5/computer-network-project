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
 * 屏幕共享客户端
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
        
        imgZip.getNextEntry();             //到Zip文件流的开始处
        Image img = ImageIO.read(imgZip);  //按照字节读取Zip图片流里面的图片
        frame.jlbImg.setIcon(new ImageIcon(img));
        System.out.println("连接第" + (System.currentTimeMillis() / 1000) % 24 % 60 + "秒");
        
        frame.validate();
        TimeUnit.MILLISECONDS.sleep(50);// 接收图片间隔时间
        imgZip.close();
      } catch (IOException | InterruptedException e) {
        System.out.println("连接断开");
        Minet.minetMainInterface.shareScreen.doClick(); // 模拟点击关闭连接按钮
      } finally {
        try {
          socket.close();
        } catch (IOException e) {}  
      }
    }   
  }
}
