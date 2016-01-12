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
 * 登录窗口类
 * @author jiarong
 * todo:
 *
 */

public class UILogin extends JFrame {
  private JButton btn_login; // 登录按钮
  private JButton btn_register; // 注册按钮
  
  private JLabel label_title;
  private JLabel label_username; 
  private JLabel label_password;
  
  private JTextField input_username; // 用户名输入
  private JTextField input_password; // 密码输入
  
  private JLabel label_errMsg; // 错误信息

  private static final long serialVersionUID = 1L;
  static final int WIDTH = 500;
  static final int HEIGHT = 300;
  
  private ActionListener action_login; // 点击登录按钮的事件监听器
  private ActionListener action_register; // 点击注册按钮的事件监听器
  
  public UILogin() {
    init(); // 初始化
    setActionListener(); // 设置事件监听器
    setComponent(); // 设置组件
    setResizable(false); // 固定窗口大小
    setVisible(true); // 设置窗口可见
  }
  
  /*
   * 初始化
   */
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口，点击右上角关闭按钮的时候退出程序
    setTitle("Minet Login"); 
    
    GridBagLayout lay = new GridBagLayout();  // GridBag布局
    setLayout(lay);
    
    setSize(WIDTH, HEIGHT); // 设置宽高
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize(); // 取得电脑屏幕大小
    
    int width=screenSize.width;
    int height=screenSize.height;
    int x = (width - WIDTH) / 2;
    int y = (height - HEIGHT) / 2;
    
    setLocation(x, y); // 设置窗口居中
    
  }
  
  /**
   * 设置事件监听器
   */
  public void setActionListener() {
    // 登录事件监听器
    action_login = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String username = input_username.getText(); // 用户名
        String pass = input_password.getText(); // 密码
        
        try {
          // 向服务器发起登录请求，true则登录成功，否则失败
          try {
            // for dev
//            if (username.equals("")) {
//              username = "wu";
//              pass = "11";
//            }
            // for dev
            if (TCPClient.request_login(username, pass)) {
              Minet.minetLogin.setVisible(false); // 设置登录窗口不可见
              Minet.minetMainInterface = new UIMainInterface(); // 登录成功，启动主界面
            } else {
              // 登录失败则输出错误信息
              label_errMsg.setText("Login Failed: wrong username or wrong password!");
            }
          } catch (ConnectException ec) {
            label_errMsg.setText("Login Failed: 连接服务器失败");
          }
          
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    };
    // 注册事件监听器，从登录窗口跳转到注册窗口
    action_register = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Minet.minetLogin.setVisible(false); // 设置登录窗口不可见
        Minet.minetRegister.setVisible(true); // 设置注册窗口可见
        Minet.minetRegister.reset();
      }
    };
  }
  
  public void setComponent() {
    btn_login = new JButton("Login"); // 登录按钮
    btn_register = new JButton("Register >>"); // 注册按钮
    label_title = new JLabel("Login to Minet!"); // 标题
    label_username = new JLabel("Username: ");
    label_password = new JLabel("Password: ");
    
    label_errMsg = new JLabel(); // 错误信息
    
    label_errMsg.setFont(new Font("Senf", Font.BOLD, 18));
    label_errMsg.setForeground(Color.red);
    
    Font font_title = new Font("Senf", Font.BOLD, 30);
    label_title.setFont(font_title);
    
    btn_login.addActionListener(action_login);
    btn_register.addActionListener(action_register);
    
    input_username = new JTextField(20);
    input_password = new JPasswordField(20);
    
    /*
     * 设置键盘监听事件，当用户按enter键时，发起登录动作
     */
    input_username.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) { // 键盘松开时的事件处理程序
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
     * 同上理
     */
    input_password.addKeyListener(new KeyListener() {
      @Override
      public void keyReleased(KeyEvent e) { // 键盘松开时的事件处理程序
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
    
    // 设置各组件的布局
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
