package Minet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import Client.TCPClient;
import MinetUI.UIHelpers;
import MinetUI.UILogin;
import MinetUI.UIMainInterface;
import MinetUI.UIRegister;
/**
 * Minet类，用来管理所有有关的UI对象
 * @author jiarong
 * todo：
 * 1. 将登录窗口类和注册窗口类合并成一个窗口，利用JTabbedPane进行布局
 */
public class Minet {
  public static UILogin minetLogin; // 登录窗口
  public static UIRegister minetRegister; // 注册窗口
  public static UIMainInterface minetMainInterface; // 登录或注册成功后的主界面
  
  public void actionUI() 
      throws JsonGenerationException, JsonMappingException, IOException {
    UIHelpers.setLookAndFeel(); // 函数的作用（应该）是使得UI界面看起来更适合该电脑
    setServerIP();
    minetLogin = new UILogin(); // 建立登录窗口
    minetRegister = new UIRegister(); // 建立注册窗口
  }
  
  // 设置服务器IP地址，如果取消，则使用默认IP
  public void setServerIP() throws UnknownHostException {
    String serverIP = JOptionPane.showInputDialog("请输入服务器IP, 取消则使用默认IP");
    if (serverIP == null || serverIP.equals("")) {
      String temp = InetAddress.getLocalHost().toString();
      serverIP = temp.substring(temp.indexOf('/') + 1);
    }
    
    while (!isIP(serverIP)) {
      serverIP = JOptionPane.showInputDialog("IP不符合规则，请重新输入");
      if (serverIP == null || serverIP.equals("")) {
        String temp = InetAddress.getLocalHost().toString();
        serverIP = temp.substring(temp.indexOf('/') + 1);
      }
    }
    
    TCPClient.serverIP = serverIP;
  }
  
  // 判断IP格式
  public boolean isIP(String addr) {
    if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
      return false;
    
    String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    Pattern pat = Pattern.compile(rexp);  
    Matcher mat = pat.matcher(addr);  
    boolean ipAddress = mat.find();
    return ipAddress;
  }
}
