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
 * 1. ���촰����Ϣ��¼�����ٵ�ʱ�򣬼�¼ռ�������ռ�
 * 2. ���촰���Զ��������ײ��Ĺ��ܲ�����ȫ�������ײ�
 * 3. �����Ż�
 * 4. ���������û��б��Ż������ǰ����ͬ���򲻸���
 * 5. ��¼�û��б��ѡ��״̬
 * 6. ��������ͬ����¼������Ϣ
 * 7. һ���û�����ʱ�����ڸ���������û��Ի����û���Ӧ�������¼ɾ����ǰ�ڰ汾��
 * 8. δ����Ϣ����
 * 9. ��������
 * 10. ��Ƶ����
 * 11. Ⱥ����
 * 12. �ع�Э����룬ͳһ�ӿ�
 * 13. ��Ļ�����ڽ����ϻ���Щ��bug
 */

public class UIMainInterface extends JFrame {
  public final int WIDTH_MAIN_INTERFACE = 1200; // ���ڿ�
  public final int HEIGHT_MAIN_INTERFACE = 900; // ���ڸ�
  
  // HashMap���͵������¼����
  public static HashMap<String, ArrayList<ChattingRecordItem>> chatting_record;
  // ��¼�û�ѡ�е��û�
  public static HashMap<String, Integer> selected_users;
  
  private JPanel panel_contactsList; // �������û��� ���
  private JPanel panel_chattingWindow; // �����촰�ڡ� ��壬λ�������Ҵ󲼾ֵ���
  private JPanel panel_videoAndVoiceChatting; // ����Ƶ���������족 ���
  private JTextArea chattingEditor; // ��Ϣ�༭��
  private JScrollPane CW_scroll; // chatting_window ����
  private JPanel contentJPanel; // ������Ϣ�������
  
  private JLabel label_chattingTarget; // label�����������
  private String chattingTarget;  // string��¼�������
  
  private JScrollPane scrollpane;
  private JList list = null; // �����û�list
  
  private JPanel panel_groupChatting; // Ⱥ�����
  private JScrollPane groupChatting_scroll; // Ⱥ������������
  private JList groupChatting_list; // Ⱥ���б�
  
  public static JButton shareScreen; // ������Ļ��ť
  public static String current_screen_share_user = null;
  
  //��¼ƽ��Ļ����״̬: server_open: ��Ϊ�������������
  //                   client_open: ��Ϊ�ͻ�����������
  //                   close: ���ӹر�
  public static String screen_share_state = "close"; 
  
  public static SharedScreen shareScreenFrame = null;
  
  // ������Ϣ�༭�򼤻����
  public void setEditorEnable(Boolean b) {
    chattingEditor.setEditable(b);
  }
  
  // ���캯��
  public UIMainInterface() 
      throws JsonGenerationException, JsonMappingException, IOException {
    init(); // ��ʼ��
    getOnlineUserList(); // ȡ�������û���Ϣ
    setContactListPanel(); // ���������û��б�
    setChattingPanel(); // �������촰��
    setScreenSharing(); // ������Ƶ����Ƶ���촰��
    setGroupChattingPanel(); // ����Ⱥ�Ĵ���
  }
  
  
  // ��ʼ��
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
    
    // �����ڹر�ʱ��ִ���¼�
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
  
  // ���������������ȡ�������û���Ϣ
  public void getOnlineUserList() 
      throws JsonGenerationException, JsonMappingException, IOException {
    TCPClient.request_onlineUsersList();
  }
  
  //���������û����
  public void setContactListPanel() 
      throws JsonGenerationException, JsonMappingException, IOException {
    JTabbedPane tabbedPane_CL = new JTabbedPane(JTabbedPane.TOP);
    
    panel_contactsList = new JPanel();
    panel_contactsList.setLayout(new BorderLayout());
    
    updateContactList();
    
    scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(200, 740));
    
    JButton groupChatting = new JButton("��ʼ����");
    
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
          JOptionPane.showMessageDialog(null, "Ⱥ���û�Ӧ�ô��ڵ���2��", 
              "Ⱥ�Ĵ���ʧ��", JOptionPane.ERROR_MESSAGE);
          return;
        }
        
        try {
          String groupname = JOptionPane.showInputDialog("Please input the group name");
          if (groupname == null || groupname.equals("")) return;
          chatting_users[count + 1] = groupname;
          Boolean result = TCPClient.new_group_chatting(chatting_users);
          if (!result) {
            JOptionPane.showMessageDialog(null, "Ⱥ�����ֳ�ͻ", 
                "Ⱥ�Ĵ���ʧ��", JOptionPane.ERROR_MESSAGE);
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
    
    tabbedPane_CL.addTab("�����û�", panel_contactsList);
    tabbedPane_CL.addTab("Ⱥ��", panel_groupChatting);
    
    add(tabbedPane_CL, new GBC(0, 0).setSpan(2, 10).setWeight(1, 1).setAnchor(GBC.NORTH));
  }
  
  //�����������
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
            if (label_chattingTarget.getText() // Ⱥ��
                .equals(TCPClient.current_chatting_group)) { 
              TCPClient.broadcast_group_chatting_item(TCPClient.current_chatting_group, content);
              add_chatting_item_to_chatting_record(
                  TCPClient.current_chatting_group, TCPClient.username, content);
            } else { // ˽��
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
  
  // ������Ƶ�������Ƶ�������
  public void setScreenSharing() {
    panel_videoAndVoiceChatting = new JPanel();
    shareScreen = new JButton("������Ļ����");
    panel_videoAndVoiceChatting.add(shareScreen);
    shareScreen.setEnabled(false);
    
    shareScreen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String func = shareScreen.getText();
        
        // �ͻ�������
        if (func.equals("������Ļ����")) {
          
          if (TCPClient.current_chatting_user == null || 
              TCPClient.current_chatting_user.equals("")) {
            JOptionPane.showMessageDialog(null, "��ѡ���û�", 
                "����ʧ��", JOptionPane.ERROR_MESSAGE);
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
        // ����˹ر�
        else if (func.equals("�رչ���")) {
          try {
            shutdownScreenShare();
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
        // �ͻ��˹ر�
        else if (func.equals("�ر�����")) {
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
  
  // ���빲����Ļ����
  public void requestShareScreen() 
      throws JsonGenerationException, JsonMappingException, IOException {
    shareScreen.setText("��������...");
    Boolean result = TCPClient.request_share_screen();
    if (result == true) {
      // ִ��������Դ�ĳ���
      shareScreen.setText("�ر�����");
      actionScreenShareClient();
      current_screen_share_user = TCPClient.current_chatting_user;
    } else {
      JOptionPane.showMessageDialog(null, "�Է��ܾ����ܹ���", 
          "��Ļ����", JOptionPane.ERROR_MESSAGE);
      shareScreen.setText("������Ļ����");
    }
  }
  
  // �ر���Ļ����
  public void shutdownScreenShare() throws JsonGenerationException, JsonMappingException, IOException {
    String str = shareScreen.getText();
    
    shareScreen.setText("������Ļ����");
    
    // ����˹رչ���
    if (str.equals("�رչ���")) {
      TCPClient.screenShareServer.suspend();
      screen_share_state = "close";
    }
    // �ͻ��˹ر�����
    else if (str.equals("�ر�����")) {
      shareScreenFrame.dispose();
      TCPClient.info_share_screen_shutdown();
      screen_share_state = "close";
    }
    current_screen_share_user = null;
  }
  
  // ����������Ļ�����������
  public static void actionScreenShareServe() {
    System.out.println(Utils.getDate() + "���ڹ�����Ļ...");
    shareScreen.setText("�رչ���");
    screen_share_state = "server_open";
    if (TCPClient.screenShareServer == null) {
      TCPClient.screenShareServer = new ScreenShareServer();
      TCPClient.screenShareServer.start();
    } else {
      TCPClient.screenShareServer.resume();
    }
  }
  
  // ������Դ���룬���ͻ���
  public static void actionScreenShareClient() {
    System.out.println(Utils.getDate() + "���ڽ�����Ļ...");
    screen_share_state = "client_open";
    shareScreen.setText("�ر�����");
    String ip = ((OnlineUserListItem) TCPClient.onlineUsersMap.get(
        TCPClient.current_chatting_user)).getIp();
    
    shareScreenFrame = new SharedScreen();
    
    (new ScreenShareClient(shareScreenFrame, ip)).start();;
  }
  
  // ����Ⱥ�����
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
  
  // ȡ�������û�������
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
  
  // ȡ�ø��û����ڵ�Ⱥ������
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
  
  // ����Ⱥ���б�
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
  
  // ���������û��б�
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
            
            // ������Ļ����ť����
            if (current_screen_share_user != null && 
                current_screen_share_user.equals(target) && 
                screen_share_state.equals("server_open")) {
              shareScreen.setText("�رչ���");
            }
            else if (current_screen_share_user != null && 
                current_screen_share_user.equals(target) && 
                screen_share_state.equals("client_open")) {
              shareScreen.setText("�ر�����");
            } else {
              shareScreen.setText("������Ļ����");
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
  
  // ��������Ϣ������ӵ�����Ϣ
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
  
  // ���촰�ڹ������ײ�
  public void scrollToButtom() {
    JScrollBar sBar = CW_scroll.getVerticalScrollBar();
    sBar.setValue(sBar.getMaximum());
  }
  
  // �����������
  public void setChattingTarget(String target) {
    chattingTarget = target;
    label_chattingTarget.setText(target);
  }
  
  // ȡ���������
  public String getChattingTarget() {
    return chattingTarget;
  }
  
  // ����������Ϣ����
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
  
  // ��ӵ���������Ϣ��¼��chatting_record������
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
