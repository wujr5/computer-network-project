package MinetUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.ConnectException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.sun.xml.internal.bind.v2.model.core.Adapter;

import Client.TCPClient;
import Minet.Minet;

/**
 * ��¼������
 * @author jiarong
 * todo:
 *
 */

public class UILogin extends JFrame {
  private JButton btn_login; // ��¼��ť
  private JButton btn_register; // ע�ᰴť
  
  private JLabel label_title;
  private JLabel label_username; 
  private JLabel label_password;
  
  private JTextField input_username; // �û�������
  private JTextField input_password; // ��������
  
  private JLabel label_errMsg; // ������Ϣ

  private static final long serialVersionUID = 1L;
  static final int WIDTH = 500;
  static final int HEIGHT = 300;
  
  private ActionListener action_login; // �����¼��ť���¼�������
  private ActionListener action_register; // ���ע�ᰴť���¼�������
  
  public UILogin() {
    init(); // ��ʼ��
    setActionListener(); // �����¼�������
    setComponent(); // �������
    setResizable(false); // �̶����ڴ�С
    setVisible(true); // ���ô��ڿɼ�
  }
  
  /*
   * ��ʼ��
   */
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���ô��ڣ�������Ͻǹرհ�ť��ʱ���˳�����
    setTitle("Minet Login"); 
    
    GridBagLayout lay = new GridBagLayout();  // GridBag����
    setLayout(lay);
    
    setSize(WIDTH, HEIGHT); // ���ÿ��
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize(); // ȡ�õ�����Ļ��С
    
    int width=screenSize.width;
    int height=screenSize.height;
    int x = (width - WIDTH) / 2;
    int y = (height - HEIGHT) / 2;
    
    setLocation(x, y); // ���ô��ھ���
    
  }
  
  /**
   * �����¼�������
   */
  public void setActionListener() {
    // ��¼�¼�������
    action_login = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String username = input_username.getText(); // �û���
        String pass = input_password.getText(); // ����
        
        try {
          // ������������¼����true���¼�ɹ�������ʧ��
          try {
            // for dev
//            if (username.equals("")) {
//              username = "wu";
//              pass = "11";
//            }
            // for dev
            if (TCPClient.request_login(username, pass)) {
              Minet.minetLogin.setVisible(false); // ���õ�¼���ڲ��ɼ�
              Minet.minetMainInterface = new UIMainInterface(); // ��¼�ɹ�������������
            } else {
              // ��¼ʧ�������������Ϣ
              label_errMsg.setText("Login Failed: wrong username or wrong password!");
            }
          } catch (ConnectException ec) {
            label_errMsg.setText("Login Failed: ���ӷ�����ʧ��");
          }
          
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    };
    // ע���¼����������ӵ�¼������ת��ע�ᴰ��
    action_register = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Minet.minetLogin.setVisible(false); // ���õ�¼���ڲ��ɼ�
        Minet.minetRegister.setVisible(true); // ����ע�ᴰ�ڿɼ�
        Minet.minetRegister.reset();
      }
    };
  }
  
  public void setComponent() {
    btn_login = new JButton("Login"); // ��¼��ť
    btn_register = new JButton("Register >>"); // ע�ᰴť
    label_title = new JLabel("Login to Minet!"); // ����
    label_username = new JLabel("Username: ");
    label_password = new JLabel("Password: ");
    
    label_errMsg = new JLabel(); // ������Ϣ
    
    label_errMsg.setFont(new Font("Senf", Font.BOLD, 18));
    label_errMsg.setForeground(Color.red);
    
    Font font_title = new Font("Senf", Font.BOLD, 30);
    label_title.setFont(font_title);
    
    btn_login.addActionListener(action_login);
    btn_register.addActionListener(action_register);
    
    input_username = new JTextField(20);
    input_password = new JPasswordField(20);
    
    /*
     * ���ü��̼����¼������û���enter��ʱ�������¼����
     */
    input_username.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) { // �����ɿ�ʱ���¼��������
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          btn_login.doClick();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyTyped(KeyEvent e) {}
    });
    
    /*
     * ͬ����
     */
    input_password.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) { // �����ɿ�ʱ���¼��������
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          btn_login.doClick();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyTyped(KeyEvent e) {}
    });
    
    // ���ø�����Ĳ���
    add(label_title, new GBC(2, 0).setSpan(4, 1).setWeight(4, 4).setAnchor(GBC.CENTER));
    
    add(label_errMsg, new GBC(2, 1).setSpan(4, 1).setWeight(4, 4).setAnchor(GBC.CENTER));
    
    add(label_username, new GBC(2, 2).setSpan(1, 1).setWeight(4, 2).setAnchor(GBC.EAST));
    add(input_username, new GBC(3, 2).setSpan(1, 1).setWeight(4, 2).setAnchor(GBC.WEST));
    
    add(label_password, new GBC(2, 3).setSpan(1, 1).setWeight(4, 2).setAnchor(GBC.EAST));
    add(input_password, new GBC(3, 3).setSpan(1, 1).setWeight(4, 2).setAnchor(GBC.WEST));
      
    add(btn_login, new GBC(2, 4).setSpan(1, 1).setWeight(4, 4).setAnchor(GBC.EAST));
    add(btn_register, new GBC(3, 4).setSpan(1, 1).setWeight(4, 4).setAnchor(GBC.CENTER));
  }
}
