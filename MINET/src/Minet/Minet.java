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
 * Minet�࣬�������������йص�UI����
 * @author jiarong
 * todo��
 * 1. ����¼�������ע�ᴰ����ϲ���һ�����ڣ�����JTabbedPane���в���
 */
public class Minet {
  public static UILogin minetLogin; // ��¼����
  public static UIRegister minetRegister; // ע�ᴰ��
  public static UIMainInterface minetMainInterface; // ��¼��ע��ɹ����������
  
  public void actionUI() 
      throws JsonGenerationException, JsonMappingException, IOException {
    UIHelpers.setLookAndFeel(); // ���������ã�Ӧ�ã���ʹ��UI���濴�������ʺϸõ���
    setServerIP();
    minetLogin = new UILogin(); // ������¼����
    minetRegister = new UIRegister(); // ����ע�ᴰ��
  }
  
  // ���÷�����IP��ַ�����ȡ������ʹ��Ĭ��IP
  public void setServerIP() throws UnknownHostException {
    String serverIP = JOptionPane.showInputDialog("�����������IP, ȡ����ʹ��Ĭ��IP");
    if (serverIP == null || serverIP.equals("")) {
      String temp = InetAddress.getLocalHost().toString();
      serverIP = temp.substring(temp.indexOf('/') + 1);
    }
    
    while (!isIP(serverIP)) {
      serverIP = JOptionPane.showInputDialog("IP�����Ϲ�������������");
      if (serverIP == null || serverIP.equals("")) {
        String temp = InetAddress.getLocalHost().toString();
        serverIP = temp.substring(temp.indexOf('/') + 1);
      }
    }
    
    TCPClient.serverIP = serverIP;
  }
  
  // �ж�IP��ʽ
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
