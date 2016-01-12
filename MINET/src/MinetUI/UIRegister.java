package MinetUI;

/**
 * @author jiarong
 * to-do：
 * 1. 加强注册数据格式的校验强度
 * 2. 数据加密
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
   * 注册窗口
   */
  private static final long serialVersionUID = 1L;
  
  private JLabel label_username;
  private JLabel label_password;
  private JLabel label_rePass;
  private JLabel label_title;
  
  private JLabel label_errMsg; // 错误信息
  
  private JTextField text_username;
  private JPasswordField text_password;
  private JPasswordField text_rePass;
  
  private JButton btn_login; // 登陆按钮，跳转到登录窗口
  private JButton btn_register; // 登录
  
  private ActionListener action_login; // 点击登陆按钮的事件监听器
  private ActionListener action_register; // 点击注册按钮的事件监听器
  
  public final int WIDTH_REGISTER = 500; // 宽度
  public final int HEIGHT_REGISTER = 300; // 高度
  
  public UIRegister() {
    init(); // 初始化
    setActionListener(); // 设置事件监听器
    setComponent(); // 设置各组件
  }
  
  // 初始化
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置点击右上角关闭按钮时，退出程序
    
    Dimension dimension = new Dimension(WIDTH_REGISTER, HEIGHT_REGISTER);
    setSize(dimension); // 设置大小
    GridBagLayout layout = new GridBagLayout();
    setLayout(layout); // 设置布局方式
    
    setResizable(false); // 设置固定大小
    setTitle("Minet Register");
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize(); // 获取电脑屏幕大小
    
    int width = screenSize.width;
    int height = screenSize.height;
    int x = (width - WIDTH) / 2 - WIDTH_REGISTER / 2;
    int y = (height - HEIGHT) / 2 - HEIGHT_REGISTER / 2;
    
    setLocation(x, y); // 居中定位
    
    setVisible(false); // 设置不可见
  }
  
  public void setActionListener() {
    // 跳转到登陆界面的事件监听器
    action_login = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Minet.minetRegister.setVisible(false); // 设置注册窗口不可见
        Minet.minetLogin.setVisible(true); // 设置登录窗口可见
      }
    };
    // 点击注册按钮事件监听器
    action_register = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String pass = text_password.getText();
        String rePass = text_rePass.getText();
        String username = text_username.getText();
        
        if (!pass.equals(rePass)) {
          // 两次密码不相同，错误提示
          label_errMsg.setText("Passwords are not the same!");
        } else {
          try {
            try {
              // 注册请求，如果返回为true，则注册公共，否则，注册失败
              if (TCPClient.request_register(username, pass)) {
                Minet.minetMainInterface = new UIMainInterface();
                Minet.minetRegister.setVisible(false);
              } else {
                // 注册失败信息
                label_errMsg.setText("Register Failed: username have already existed!");
              }
            } catch (ConnectException ec) {
              label_errMsg.setText("Login Failed: 连接服务器失败");
            }
            
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    };
  }
  
  // 设置基本组件
  public void setComponent() {
    label_title = new JLabel("Register to Minet");
    label_username = new JLabel("Username: ");
    label_password = new JLabel("Password: ");
    label_rePass = new JLabel("Password again: ");
    label_errMsg = new JLabel(); // 错误信息
    
    Font font_title = new Font("Senf", Font.BOLD, 30);
    label_title.setFont(font_title);
    
    Font font_errMsg = new Font("Senf", Font.BOLD, 18);
    label_errMsg.setFont(font_errMsg);
    label_errMsg.setForeground(Color.red);
    
    // 布局
    add(label_title, new GBC(0, 0).setSpan(2, 1).setWeight(4, 4));
    add(label_username, new GBC(0, 2).setAnchor(GBC.EAST).setWeight(4, 4));
    add(label_password, new GBC(0, 3).setAnchor(GBC.EAST).setWeight(4, 4));
    add(label_rePass, new GBC(0, 4).setAnchor(GBC.EAST).setWeight(4, 4));
    
    add(label_errMsg, new GBC(0, 1).setSpan(2, 1).setAnchor(GBC.CENTER).setWeight(4, 4));
    
    text_username = new JTextField(20);
    text_password = new JPasswordField(20);
    text_rePass = new JPasswordField(20);
    
    // 按钮事件监听器，点击enter出发注册时间
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
    // 按钮事件监听器，点击enter出发注册时间
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
    
    // 按钮事件监听器，点击enter出发注册时间
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
    
    // 布局
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
  
  // 当从登录窗口跳转到注册窗口时，重置信息
  public void reset() {
    label_errMsg.setText("");
    text_password.setText("");
    text_rePass.setText("");
  }
}
