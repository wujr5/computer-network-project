package MinetUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.sun.prism.impl.Disposer.Record;

import Protocol.*;
import Utils.Utils;
import Client.OnlineUserListItem;
import Client.ScreenShareClient;
import Client.ScreenShareServer;
import Client.TCPClient;

/**
 * @author jiarong
 * to do list:
 * 1. 聊天窗口消息记录数量少的时候，记录占据整个空间
 * 2. 聊天窗口自动滑动到底部的功能不能完全滑动到底部
 * 3. 输入优化
 * 4. 更新在线用户列表优化：如果前后相同，则不更新
 * 5. 记录用户列表的选中状态
 * 6. 服务器端同步记录聊天信息
 * 7. 一个用户离线时，正在跟这个离线用户对话的用户相应的聊天记录删除（前期版本）
 * 8. 未读信息提醒
 * 9. 语音聊天
 * 10. 视频聊天
 * 11. 群语音
 * 12. 重构协议代码，统一接口
 * 13. 屏幕分享在交互上还有些许bug
 */

public class UIMainInterface extends JFrame {
  public final int WIDTH_MAIN_INTERFACE = 1200; // 窗口宽
  public final int HEIGHT_MAIN_INTERFACE = 900; // 窗口高
  
  // HashMap类型的聊天记录对象
  public static HashMap<String, ArrayList<ChattingRecordItem>> chatting_record;
  // 记录用户选中的用户
  public static HashMap<String, Integer> selected_users;
  
  private JPanel panel_contactsList; // “在线用户” 面板
  private JPanel panel_chattingWindow; // “聊天窗口” 面板，位于左中右大布局的中
  private JPanel panel_videoAndVoiceChatting; // “视频和语音聊天” 面板
  private JTextArea chattingEditor; // 信息编辑区
  private JScrollPane CW_scroll; // chatting_window 滚动
  private JPanel contentJPanel; // 聊天信息呈现面板
  
  private JLabel label_chattingTarget; // label呈现聊天对象
  private String chattingTarget;  // string记录聊天对象
  
  private JScrollPane scrollpane;
  private JList list = null; // 在线用户list
  
  private JPanel panel_groupChatting; // 群组面板
  private JScrollPane groupChatting_scroll; // 群组面板滚动容器
  private JList groupChatting_list; // 群组列表
  
  public static JButton shareScreen; // 共享屏幕按钮
  public static String current_screen_share_user = null;
  
  //记录平屏幕共享状态: server_open: 作为服务端正在连接
  //                   client_open: 作为客户端正在连接
  //                   close: 连接关闭
  public static String screen_share_state = "close"; 
  
  public static SharedScreen shareScreenFrame = null;
  
  // 设置消息编辑框激活与否
  public void setEditorEnable(Boolean b) {
    chattingEditor.setEditable(b);
  }
  
  // 构造函数
  public UIMainInterface() 
      throws JsonGenerationException, JsonMappingException, IOException {
    init(); // 初始化
    getOnlineUserList(); // 取得在线用户信息
    setContactListPanel(); // 设置在线用户列表
    setChattingPanel(); // 设置聊天窗口
    setScreenSharing(); // 设置视频和音频聊天窗口
    setGroupChattingPanel(); // 设置群聊窗口
  }
  
  
  // 初始化
  public void init() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Dimension dimension = new Dimension(WIDTH_MAIN_INTERFACE, HEIGHT_MAIN_INTERFACE);
    setSize(dimension);
    GridBagLayout layout = new GridBagLayout();
    setLayout(layout);
    
    setResizable(false);
    setTitle("Minet: " + TCPClient.username);
    
    Toolkit kit = Toolkit.getDefaultToolkit();
    Dimension screenSize = kit.getScreenSize();
    
    int width = screenSize.width;
    int height = screenSize.height;
    int x = (width - WIDTH_MAIN_INTERFACE) / 2;
    int y = (height - HEIGHT_MAIN_INTERFACE) / 2;
    
    setLocation(x, y);
    setVisible(true);
    
    chatting_record = new HashMap<String, ArrayList<ChattingRecordItem>>();
    selected_users = new HashMap<String, Integer>();
    
    // 当窗口关闭时，执行事件
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        try {
          if (screen_share_state.equals("server_open") 
              || screen_share_state.equals("client_open")) {
            shutdownScreenShare();
          }
          TCPClient.send_logout();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        e.getWindow().dispose();
      }
    });
  }
  
  // 向服务器发起请求，取得在线用户信息
  public void getOnlineUserList() 
      throws JsonGenerationException, JsonMappingException, IOException {
    TCPClient.request_onlineUsersList();
  }
  
  //设置在线用户面板
  public void setContactListPanel() 
      throws JsonGenerationException, JsonMappingException, IOException {
    JTabbedPane tabbedPane_CL = new JTabbedPane(JTabbedPane.TOP);
    
    panel_contactsList = new JPanel();
    panel_contactsList.setLayout(new BorderLayout());
    
    updateContactList();
    
    scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(200, 740));
    
    JButton groupChatting = new JButton("开始聊天");
    
    groupChatting.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String[] group_chatting_users = new String[100];
        int count = 0;
        Iterator iter = selected_users.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry) iter.next();
          String key = (String) entry.getKey();
          int val = (int) entry.getValue();
          
          if (val == 1) group_chatting_users[count++] = key;
        }
        
        String[] chatting_users = new String[count + 2];
        
        for (int i = 0; i < count; i++) {
          chatting_users[i] = group_chatting_users[i];
        }
        chatting_users[count] = TCPClient.username;
        
        if (count == 1 || count == 0) {
          JOptionPane.showMessageDialog(null, "群聊用户应该大于等于2人", 
              "群聊创建失败", JOptionPane.ERROR_MESSAGE);
          return;
        }
        
        try {
          String groupname = JOptionPane.showInputDialog("Please input the group name");
          if (groupname == null || groupname.equals("")) return;
          chatting_users[count + 1] = groupname;
          Boolean result = TCPClient.new_group_chatting(chatting_users);
          if (!result) {
            JOptionPane.showMessageDialog(null, "群聊名字冲突", 
                "群聊创建失败", JOptionPane.ERROR_MESSAGE);
            return;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    
    panel_contactsList.add(scrollpane, "Center");
    panel_contactsList.add(groupChatting, BorderLayout.SOUTH);
    panel_contactsList.setBorder(new EmptyBorder(20, 20, 20, 20));
    
    panel_groupChatting = new JPanel();
    
    tabbedPane_CL.addTab("在线用户", panel_contactsList);
    tabbedPane_CL.addTab("群聊", panel_groupChatting);
    
    add(tabbedPane_CL, new GBC(0, 0).setSpan(2, 10).setWeight(1, 1).setAnchor(GBC.NORTH));
  }
  
  //设置聊天面板
  public void setChattingPanel() {
    panel_chattingWindow = new JPanel();
    panel_chattingWindow.setLayout(new BorderLayout());
    
    label_chattingTarget = new JLabel("");
    label_chattingTarget.setFont(new Font("Senf", Font.BOLD, 25));
    panel_chattingWindow.add(label_chattingTarget, BorderLayout.NORTH);
    
    contentJPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(contentJPanel, BoxLayout.Y_AXIS);
    contentJPanel.setLayout(boxLayout);
    
    CW_scroll = new JScrollPane(contentJPanel);
    CW_scroll.setPreferredSize(new Dimension(600, 500));
    CW_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    panel_chattingWindow.add(CW_scroll, BorderLayout.CENTER);
    
    chattingEditor = new JTextArea();
    chattingEditor.setEditable(false);
    chattingEditor.setLineWrap(true);
    chattingEditor.setWrapStyleWord(true);
    chattingEditor.setFont(new Font("Senf", Font.PLAIN, 20));
    JScrollPane CE_scroll = new JScrollPane(chattingEditor);
    CE_scroll.setPreferredSize(new Dimension(600, 250));
    
    JButton btn_send = new JButton("Send");
    
    btn_send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String content = chattingEditor.getText();
          TCPClient.send_chatting_item(content);
          add_chatting_item_to_chatting_record(TCPClient.current_chatting_user, TCPClient.username, content);
          addChattingItem(content, TCPClient.username, chattingItemType.MY_CHATTING_ITEM);
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });
    
    JPanel panel_editor = new JPanel();
    chattingEditor.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {  
        char charA = e.getKeyChar();  
        if ((int)charA == 10) { // Enter
          try {
            String content = chattingEditor.getText();
            if (label_chattingTarget.getText() // 群聊
                .equals(TCPClient.current_chatting_group)) { 
              TCPClient.broadcast_group_chatting_item(TCPClient.current_chatting_group, content);
              add_chatting_item_to_chatting_record(
                  TCPClient.current_chatting_group, TCPClient.username, content);
            } else { // 私聊
              TCPClient.send_chatting_item(content);
              add_chatting_item_to_chatting_record(
                  TCPClient.current_chatting_user, TCPClient.username, content);
            }
            addChattingItem(content, TCPClient.username, chattingItemType.MY_CHATTING_ITEM);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    panel_editor.setLayout(new BorderLayout());
    
    JLabel label_span = new JLabel(" ");
    
    panel_editor.add(label_span, BorderLayout.NORTH);
    panel_editor.add(CE_scroll, BorderLayout.CENTER);
    panel_editor.add(btn_send, BorderLayout.SOUTH);
    
    panel_chattingWindow.add(panel_editor, BorderLayout.SOUTH);
    add(panel_chattingWindow, new GBC(3, 0).setSpan(2, 10).setWeight(1, 1).setAnchor(GBC.CENTER));
  }
  
  // 设置视频聊天和音频聊天面板
  public void setScreenSharing() {
    panel_videoAndVoiceChatting = new JPanel();
    shareScreen = new JButton("申请屏幕共享");
    panel_videoAndVoiceChatting.add(shareScreen);
    shareScreen.setEnabled(false);
    
    shareScreen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String func = shareScreen.getText();
        
        // 客户端申请
        if (func.equals("申请屏幕共享")) {
          
          if (TCPClient.current_chatting_user == null || 
              TCPClient.current_chatting_user.equals("")) {
            JOptionPane.showMessageDialog(null, "请选择用户", 
                "申请失败", JOptionPane.ERROR_MESSAGE);
            return;
          } else {
            current_screen_share_user = TCPClient.current_chatting_user;
          }
          
          try {
            requestShareScreen();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
        // 服务端关闭
        else if (func.equals("关闭共享")) {
          try {
            shutdownScreenShare();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
        // 客户端关闭
        else if (func.equals("关闭连接")) {
          try {
            shutdownScreenShare();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
      }
    });
    
    add(panel_videoAndVoiceChatting, new GBC(9, 0).setSpan(2, 10).setWeight(4, 4));
  }
  
  // 申请共享屏幕函数
  public void requestShareScreen() 
      throws JsonGenerationException, JsonMappingException, IOException {
    shareScreen.setText("正在请求...");
    Boolean result = TCPClient.request_share_screen();
    if (result == true) {
      // 执行申请资源的程序
      shareScreen.setText("关闭连接");
      actionScreenShareClient();
      current_screen_share_user = TCPClient.current_chatting_user;
    } else {
      JOptionPane.showMessageDialog(null, "对方拒绝接受共享", 
          "屏幕共享", JOptionPane.ERROR_MESSAGE);
      shareScreen.setText("申请屏幕共享");
    }
  }
  
  // 关闭屏幕共享
  public void shutdownScreenShare() throws JsonGenerationException, JsonMappingException, IOException {
    String str = shareScreen.getText();
    
    shareScreen.setText("申请屏幕共享");
    
    // 服务端关闭共享
    if (str.equals("关闭共享")) {
      TCPClient.screenShareServer.suspend();
      screen_share_state = "close";
    }
    // 客户端关闭连接
    else if (str.equals("关闭连接")) {
      shareScreenFrame.dispose();
      TCPClient.info_share_screen_shutdown();
      screen_share_state = "close";
    }
    current_screen_share_user = null;
  }
  
  // 启动共享屏幕服务，做服务端
  public static void actionScreenShareServe() {
    System.out.println(Utils.getDate() + "正在共享屏幕...");
    shareScreen.setText("关闭共享");
    screen_share_state = "server_open";
    if (TCPClient.screenShareServer == null) {
      TCPClient.screenShareServer = new ScreenShareServer();
      TCPClient.screenShareServer.start();
    } else {
      TCPClient.screenShareServer.resume();
    }
  }
  
  // 启动资源申请，做客户端
  public static void actionScreenShareClient() {
    System.out.println(Utils.getDate() + "正在接受屏幕...");
    screen_share_state = "client_open";
    shareScreen.setText("关闭连接");
    String ip = ((OnlineUserListItem) TCPClient.onlineUsersMap.get(
        TCPClient.current_chatting_user)).getIp();
    
    shareScreenFrame = new SharedScreen();
    
    (new ScreenShareClient(shareScreenFrame, ip)).start();;
  }
  
  // 设置群聊面板
  public void setGroupChattingPanel() 
      throws JsonGenerationException, JsonMappingException, IOException {
    groupChatting_list = new JList();
    
    updateGroupChattingList();
 
    groupChatting_scroll = new JScrollPane(groupChatting_list);
    groupChatting_scroll.setPreferredSize(new Dimension(200, 770));
    
    panel_groupChatting.setLayout(new BorderLayout());
    panel_groupChatting.add(groupChatting_scroll, "Center");
    panel_groupChatting.add(groupChatting_scroll, BorderLayout.SOUTH);
    panel_groupChatting.setBorder(new EmptyBorder(20, 20, 20, 20));
  }
  
  // 取得在线用户的数组
  public String[] getUsersStringArray() {
    ArrayList usersArrayList = new ArrayList<String>(); 
    
    Iterator iter = TCPClient.onlineUsersMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();
      
      if (key.equals(TCPClient.username)) continue;
      
      usersArrayList.add(key);
    }
    
    String[] strs = new String[usersArrayList.size()];
    for (int i = 0; i < usersArrayList.size(); i++) {
      strs[i] = (String) usersArrayList.get(i);
    }
    return strs;
  }
  
  // 取得该用户所在的群组数组
  public String[] getGroupsStringArray() {
    ArrayList groupsArrayList = new ArrayList<String>(); 
    
    Iterator iter = TCPClient.group_list.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();
      groupsArrayList.add(key);
    }
    
    String[] strs = new String[groupsArrayList.size()];
    for (int i = 0; i < groupsArrayList.size(); i++) {
      strs[i] = (String) groupsArrayList.get(i);
    }
    return strs;
  }
  
  // 更新群聊列表
  public void updateGroupChattingList() 
      throws JsonGenerationException, JsonMappingException, IOException {
    String[] strs = getGroupsStringArray();
    if (groupChatting_list == null) groupChatting_list = new JList();
    
    groupChatting_list.setListData(strs);
    groupChatting_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    groupChatting_list.setBorder(new EmptyBorder(0, 4, 0, 0));
    
    groupChatting_list.addMouseListener(new MouseAdapter() { 
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          shareScreen.setEnabled(false);
          try {
            chattingEditor.setEditable(true);
            String target = groupChatting_list.getSelectedValue().toString();
            
            setChattingTarget(target);
            
            TCPClient.current_chatting_group = target;
            if (!chatting_record.containsKey(TCPClient.current_chatting_group))
              chatting_record.put(TCPClient.current_chatting_group, 
                  new ArrayList<ChattingRecordItem>());
          } catch (ArrayIndexOutOfBoundsException ev) {
            
          }
          
          try {
            updateChattingWindow(TCPClient.current_chatting_group);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    
    groupChatting_list.revalidate();
  }
  
  // 更新在线用户列表
  public void updateContactList() 
      throws JsonGenerationException, JsonMappingException, IOException {
    TCPClient.request_onlineUsersList();
    
    String[] strs = getUsersStringArray();
    
    if (list == null) list = new JList();
    
    list.setListData(CheckList.createData(strs));
    list.setCellRenderer(new CheckListRenderer());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
    list.setBorder(new EmptyBorder(0, 4, 0, 0));
    
    list.addMouseListener(new MouseAdapter() { 
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          try {
            int index = list.locationToIndex(e.getPoint()); 
            CheckableItem item = (CheckableItem) list.getModel() 
                .getElementAt(index);
            item.setSelected(!item.isSelected()); 
            
            selected_users.put(item.toString(), item.isSelected() ? 1 : 0);
            
            Rectangle rect = list.getCellBounds(index, index); 
            list.repaint(rect);
          } catch (ArrayIndexOutOfBoundsException ev) {
            
          }
        }
        else if (e.getClickCount() == 2) {
          try {
            shareScreen.setEnabled(true);
            chattingEditor.setEditable(true);
            int index = list.locationToIndex(e.getPoint()); 
            CheckableItem item = (CheckableItem) list.getModel() 
                .getElementAt(index);
            
            String target = item.toString();
            setChattingTarget(target);
            
            // 设置屏幕共享按钮文字
            if (current_screen_share_user != null && 
                current_screen_share_user.equals(target) && 
                screen_share_state.equals("server_open")) {
              shareScreen.setText("关闭共享");
            }
            else if (current_screen_share_user != null && 
                current_screen_share_user.equals(target) && 
                screen_share_state.equals("client_open")) {
              shareScreen.setText("关闭链接");
            } else {
              shareScreen.setText("申请屏幕共享");
            }
            
            TCPClient.current_chatting_user = item.toString();
            if (!chatting_record.containsKey(TCPClient.current_chatting_user))
              chatting_record.put(TCPClient.current_chatting_user, 
                  new ArrayList<ChattingRecordItem>());
          } catch (ArrayIndexOutOfBoundsException ev) {
            
          }
          
          try {
            updateChattingWindow(TCPClient.current_chatting_user);
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
    });
    
    list.revalidate();
  }
  
  // 在聊天信息窗口添加单条信息
  public void addChattingItem(String content, String user, chattingItemType type) 
      throws UnknownHostException, IOException {
      chattingEditor.setText("");
    if (!content.equals("") && !content.equals("\n")) {
      int username_and_content_align = 0;
      String username_panel_align = "";
      
      if (type == chattingItemType.MY_CHATTING_ITEM) {
        username_and_content_align = FlowLayout.RIGHT;
        username_panel_align = BorderLayout.EAST;
      }
      else if (type == chattingItemType.OTHERS_CHATTING_ITEM) {
        username_and_content_align = FlowLayout.LEFT;
        username_panel_align = BorderLayout.WEST;
      }
      JLabel username = new JLabel(user);
      username.setFont(new Font("Senf", Font.BOLD, 25));
      JLabel contentJLabel = new JLabel(content);
      
      JPanel aJPanel = new JPanel();
      aJPanel.setLayout(new FlowLayout(username_and_content_align));
      aJPanel.add(username);
      JPanel bJPanel = new JPanel();
      bJPanel.setLayout(new FlowLayout(username_and_content_align));
      bJPanel.add(contentJLabel);
      
      JPanel abJPanel = new JPanel();
      abJPanel.setLayout(new BorderLayout());
      abJPanel.add(aJPanel, username_panel_align);
      abJPanel.add(bJPanel, BorderLayout.SOUTH);
      
      contentJPanel.add(abJPanel);
      
      JPanel cJPanel = abJPanel;
      
      contentJPanel.revalidate();
      scrollToButtom();
      CW_scroll.revalidate();
    }
  }
  
  // 聊天窗口滚动到底部
  public void scrollToButtom() {
    JScrollBar sBar = CW_scroll.getVerticalScrollBar();
    sBar.setValue(sBar.getMaximum());
  }
  
  // 设置聊天对象
  public void setChattingTarget(String target) {
    chattingTarget = target;
    label_chattingTarget.setText(target);
  }
  
  // 取得聊天对象
  public String getChattingTarget() {
    return chattingTarget;
  }
  
  // 更新聊天信息窗口
  public void updateChattingWindow(String target) 
      throws UnknownHostException, IOException {
    contentJPanel.removeAll();
    contentJPanel.revalidate();
    
    if (target == null) {
      contentJPanel.revalidate();
      return;
    }
    
    ArrayList<ChattingRecordItem> record = 
        chatting_record.get(target);
    
    for (int i = 0; i < record.size(); i++) {
      String username = record.get(i).getUsername();
      String content = record.get(i).getContent();
      chattingItemType type = null;
      
      if (!username.equals(TCPClient.username)) 
        type = chattingItemType.OTHERS_CHATTING_ITEM;
      else 
        type = chattingItemType.MY_CHATTING_ITEM;
      
      addChattingItem(content, username, type);
    }
    
    contentJPanel.revalidate();
  }
  
  // 添加单条聊天信息记录到chatting_record对象中
  public static void add_chatting_item_to_chatting_record(
      String key, String username, String content) {
    if (!chatting_record.containsKey(key))
      chatting_record.put(key, new ArrayList<ChattingRecordItem>());
    
    ArrayList<ChattingRecordItem> record = 
        (ArrayList<ChattingRecordItem>) chatting_record.get(key);
    record.add(new ChattingRecordItem(username, content));
    chatting_record.remove(username);
    chatting_record.put(username, record);
  }
}
