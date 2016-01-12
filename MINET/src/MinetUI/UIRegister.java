package MinetUI;

/**
 * @author jiarong
 * to-do��
 * 1. ��ǿע�����ݸ�ʽ��У��ǿ��
 * 2. ���ݼ���
 */

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ConnectException;

import javax.swing.*;

import Client.TCPClient;
import Minet.Minet;

public class UIRegister extends JFrame {
  /**
   * ע�ᴰ��
   */
  private static final long serialVersionUID = 1L;
  
  private JLabel label_username;
  private JLabel label_password;
  private JLabel label_rePass;
  private JLabel label_title;
  
  private JLabel label_errMsg; // ������Ϣ
  
  private JTextField text_username;
  private JPasswordField text_password;
  private JPasswordField text_rePass;
  
  private JButton btn_login; // ��½��ť����ת����¼����
  private JButton btn_register; // ��¼
  
  private ActionListener action_login; // �����½��ť���¼�������
  private ActionListener action_register; // ���ע�ᰴť���¼�������
  
  public final int WIDTH_REGISTER = 500; // ���
  public final int HEIGHT_REGISTER = 300; // �߶�
  
  public UIRegister() {
    init(); // ��ʼ��
    setActionListener(); // �����¼�������
    setComponent(); // ���ø����
  }
  
  // ��ʼ��
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���õ�����Ͻǹرհ�ťʱ���˳�����
    
    Dimension dimension = new Dimension(WIDTH_REGISTER, HEIGHT_REGISTER);
    setSize(dimension); // ���ô�С
    GridBagLayout layout = new GridBagLayout();
    setLayout(layout); // ���ò��ַ�ʽ
    
    setResizable(false); // ���ù̶���С
    setTitle("Minet Register");
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize(); // ��ȡ������Ļ��С
    
    int width = screenSize.width;
    int height = screenSize.height;
    int x = (width - WIDTH) / 2 - WIDTH_REGISTER / 2;
    int y = (height - HEIGHT) / 2 - HEIGHT_REGISTER / 2;
    
    setLocation(x, y); // ���ж�λ
    
    setVisible(false); // ���ò��ɼ�
  }
  
  public void setActionListener() {
    // ��ת����½������¼�������
    action_login = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Minet.minetRegister.setVisible(false); // ����ע�ᴰ�ڲ��ɼ�
        Minet.minetLogin.setVisible(true); // ���õ�¼���ڿɼ�
      }
    };
    // ���ע�ᰴť�¼�������
    action_register = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String pass = text_password.getText();
        String rePass = text_rePass.getText();
        String username = text_username.getText();
        
        if (!pass.equals(rePass)) {
          // �������벻��ͬ��������ʾ
          label_errMsg.setText("Passwords are not the same!");
        } else {
          try {
            try {
              // ע�������������Ϊtrue����ע�ṫ��������ע��ʧ��
              if (TCPClient.request_register(username, pass)) {
                Minet.minetMainInterface = new UIMainInterface();
                Minet.minetRegister.setVisible(false);
              } else {
                // ע��ʧ����Ϣ
                label_errMsg.setText("Register Failed: username have already existed!");
              }
            } catch (ConnectException ec) {
              label_errMsg.setText("Login Failed: ���ӷ�����ʧ��");
            }
            
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    };
  }
  
  // ���û������
  public void setComponent() {
    label_title = new JLabel("Register to Minet");
    label_username = new JLabel("Username: ");
    label_password = new JLabel("Password: ");
    label_rePass = new JLabel("Password again: ");
    label_errMsg = new JLabel(); // ������Ϣ
    
    Font font_title = new Font("Senf", Font.BOLD, 30);
    label_title.setFont(font_title);
    
    Font font_errMsg = new Font("Senf", Font.BOLD, 18);
    label_errMsg.setFont(font_errMsg);
    label_errMsg.setForeground(Color.red);
    
    // ����
    add(label_title, new GBC(0, 0).setSpan(2, 1).setWeight(4, 4));
    add(label_username, new GBC(0, 2).setAnchor(GBC.EAST).setWeight(4, 4));
    add(label_password, new GBC(0, 3).setAnchor(GBC.EAST).setWeight(4, 4));
    add(label_rePass, new GBC(0, 4).setAnchor(GBC.EAST).setWeight(4, 4));
    
    add(label_errMsg, new GBC(0, 1).setSpan(2, 1).setAnchor(GBC.CENTER).setWeight(4, 4));
    
    text_username = new JTextField(20);
    text_password = new JPasswordField(20);
    text_rePass = new JPasswordField(20);
    
    // ��ť�¼������������enter����ע��ʱ��
    text_username.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          btn_register.doClick();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyTyped(KeyEvent e) {}
    });
    // ��ť�¼������������enter����ע��ʱ��
    text_password.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          btn_register.doClick();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyTyped(KeyEvent e) {}
    });
    
    // ��ť�¼������������enter����ע��ʱ��
    text_rePass.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) {
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          btn_register.doClick();
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {}

      @Override
      public void keyTyped(KeyEvent e) {}
    });
    
    // ����
    add(text_username, new GBC(1, 2).setAnchor(GBC.WEST).setWeight(4, 4));
    add(text_password, new GBC(1, 3).setAnchor(GBC.WEST).setWeight(4, 4));
    add(text_rePass, new GBC(1, 4).setAnchor(GBC.WEST).setWeight(4, 4));
    
    btn_login = new JButton("<< Login");
    btn_login.addActionListener(action_login);
    btn_register = new JButton("Register");
    btn_register.addActionListener(action_register);
    
    add(btn_login, new GBC(0, 5).setSpan(1, 1).setWeight(4, 4).setAnchor(GBC.EAST));
    add(btn_register, new GBC(1, 5).setSpan(1, 1).setWeight(4, 4).setAnchor(GBC.CENTER));
  }
  
  // ���ӵ�¼������ת��ע�ᴰ��ʱ��������Ϣ
  public void reset() {
    label_errMsg.setText("");
    text_password.setText("");
    text_rePass.setText("");
  }
}
