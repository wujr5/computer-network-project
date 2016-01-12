package Client;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.sun.org.apache.bcel.internal.generic.GOTO;

/**
 * 屏幕分享服务端
 * @author jiarong
 */

public class ScreenShareServer extends Thread {
  public static int SERVERPORT = 8000;
  private ServerSocket serverSocket;
  private Robot robot;
  public  Dimension screen;
  public Rectangle rect ;
  private Socket socket; 
   
  public static void main(String args[]) {
    new ScreenShareServer().start();
  }
  
  public ScreenShareServer() {
    try {
      try {
        serverSocket = new ServerSocket(SERVERPORT);
        serverSocket.setSoTimeout(864000000);
        robot = new Robot();
      } catch (BindException be) {
        
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    screen = Toolkit.getDefaultToolkit().getScreenSize();
    rect = new Rectangle(screen);
  }
   
  @Override
  public void run() {
    //实时等待接收截屏消息
    while(true) {
      try{
        socket = serverSocket.accept();
        System.out.println("学生端口已经连接");
        ZipOutputStream zip = new ZipOutputStream(
            new DataOutputStream(socket.getOutputStream()));
        zip.setLevel(9); //设置压缩级别
         
        BufferedImage img = robot.createScreenCapture(rect);
        zip.putNextEntry(new ZipEntry("test.jpg"));
        ImageIO.write(img, "jpg", zip);
        if(zip!=null)zip.close();
        System.out.println("Client正在实时连接");
      } catch (IOException ioe) {
        System.out.println("连接断开");
      } finally {
        if (socket != null) {
          try {
            socket.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}

