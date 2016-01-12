package Client;

/**
 * @author jiarong
 * to-do:
 * 1. json中文乱码问题
 * 2. soursePort动态选择
 * 3. 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import sun.security.x509.IPAddressName;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.sun.media.jfxmedia.control.VideoDataBuffer;

import Minet.Minet;
import MinetUI.ChattingRecordItem;
import MinetUI.chattingItemType;
import Protocol.MMprotocol;
import Protocol.ProtocolType;
import Utils.*;

public class TCPClient {
  public static String serverIP; // 记录服务器主机IP
  public static int serverPort; // 记录服务器端口
  public static String sourseIP; // 客户端IP
  public static int soursePort; // 客户端端口
  
  public static String username; // 已经登录的用户名
  
  public static String current_chatting_user = ""; // 当前聊天的用户
  public static String current_chatting_group = ""; // 当前群组
  public static HashMap onlineUsersMap = null; // 记录在线用户信息的HashMap
  public static HashMap<String, String[]> group_list = null; // 记录群组信息
  public static ScreenShareServer screenShareServer = null;
  
  // 初始化
  public static void init() throws UnknownHostException {
    String temp = InetAddress.getLocalHost().toString();
    
    // "172.18.182.75"; // "192.168.199.248"; 
    // serverIP = temp.substring(temp.indexOf('/') + 1); // 设置服务器IP
    serverPort = 6789; // 设置服务端端口
    sourseIP = temp.substring(temp.indexOf('/') + 1); // 客户端IP
    soursePort = 6788; // 客户端 端口
    
    if (onlineUsersMap == null)
      onlineUsersMap = new HashMap<String, OnlineUserListItem>();
    if (group_list == null)
      group_list = new HashMap<String, String[]>();
  }
  
  // 启动服务端Socket监听
  public static void TCPClientStart() throws IOException {
    System.out.println(Utils.getDate() + "TCP Client start...");
    ServerSocket welcomeSocket;
    try {
      welcomeSocket = new ServerSocket(soursePort);
    } catch (BindException e) {
      TCPClient.soursePort++;
      TCPClient.TCPClientStart();
      return;
    }
    
    while(true) {
      Socket connectionSocket = welcomeSocket.accept();
      
      BufferedReader inFromServerOrOtherClients =
          new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
      DataOutputStream outToServerOrOtherClients =
          new DataOutputStream(connectionSocket.getOutputStream());
      
      String reqJson = inFromServerOrOtherClients.readLine();
      ObjectMapper mapper = new ObjectMapper();
      HashMap reqObj = mapper.readValue(reqJson, HashMap.class);
      
      String type = (String) reqObj.get("protocolType");
      
      switch (type) {
        case "REQUEST_ONLINE_OR_OFFLINE": // 对在线用户信息更新请求的响应函数
          handler_updateUsersList(outToServerOrOtherClients);
          break;
        case "SENDTO_PERSONAL_CHATTING_ITEM": // 对P2P信息的响应函数
          handler_receive_personal_chatting_item(reqObj);
          break;
        case "BROADCAST_USER_ONLINE_OR_OFFLIE": // 对用户上下线广播消息的响应函数
          handler_broadcast_user_online_or_offline(reqObj);
          break;
        case "BROADCAST_NEW_GROUP": // 对新建群组广播消息的相应函数
          handler_broadcast_new_group(reqObj);
          break;
        case "BROADCAST_GROUP_CHATTING_RECORD": // 对群聊广播消息的响应函数
          handler_broadcast_group_chatting_record(reqObj);
          break;
        case "REQUEST_SHARE_SCREEN": // 对请求屏幕分享的响应函数
          response_share_screen(reqObj, outToServerOrOtherClients);
          break;
        case "INFO_SHARE_SCREEN_SHUTDOWN": // 对关闭屏幕共享通知的响应函数
          handler_share_screen_shutdown(); // 作为服务端：响应屏幕分享关闭的通知
          break;
        default:
          System.out.println("Here is default!");
          break;
      }
    }
  }
  
  // 更新用户列表的处理函数
  public static void handler_updateUsersList(DataOutputStream outToServerOrOtherClients) 
          throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_ONLINE_USER_LIST, TCPClient.username,
        null, null, null, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(response);
    
    outToServerOrOtherClients.writeBytes(json + "\n");
  }
  
  // P2P通信处理函数
  public static void handler_receive_personal_chatting_item(HashMap reqObj) 
      throws UnknownHostException, IOException {
    
    String content = (String) reqObj.get("data");
    String from = (String) reqObj.get("from");
    
    Minet.minetMainInterface.add_chatting_item_to_chatting_record(from, from, content);
    
    if (from.equals(TCPClient.current_chatting_user)) {
      Minet.minetMainInterface.addChattingItem(
          content, from, chattingItemType.OTHERS_CHATTING_ITEM);
    }
  }
  
  // 广播群聊信息
  public static void broadcast_group_chatting_item(String groupname, String content) 
      throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol requestObject = new MMprotocol(ProtocolType.BROADCAST_GROUP_CHATTING_RECORD, 
        TCPClient.username, groupname, "text", content, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    Socket clientSocket = new Socket(serverIP, serverPort);
    DataOutputStream outToTarget =
        new DataOutputStream(clientSocket.getOutputStream());
    
    outToTarget.writeBytes(json + '\n');
  }
  
  // 对用户上线或者下线信息通知的处理函数
  public static void handler_broadcast_user_online_or_offline(HashMap object) 
      throws JsonGenerationException, JsonMappingException, IOException {
    String data = (String) object.get("data");
    String[] temp = data.split(":");
    String username = temp[0];
    String type = temp[1];
    
    if (type.equals("logout")) {
      onlineUsersMap.remove(username);
      Minet.minetMainInterface.chatting_record.remove(username);
      if (TCPClient.current_chatting_user != null && 
          TCPClient.current_chatting_user.equals(username)) {
        
        TCPClient.current_chatting_user = null;
        Minet.minetMainInterface.setEditorEnable(false);
        Minet.minetMainInterface.updateChattingWindow(TCPClient.current_chatting_user);
        Minet.minetMainInterface.updateChattingWindow(TCPClient.current_chatting_user);
      }
    } else {
      String ip = (String) object.get("sourseIP");
      int port = (int) object.get("soursePort");
      
      try {
        onlineUsersMap.put(username, new OnlineUserListItem(ip, port));
      } catch (NullPointerException e) {
        
      }
    }
    
    try {
      // 更新一次出现间隔性contact list复选框无法选中的bug，更新两次正常
      Minet.minetMainInterface.updateContactList();
      Minet.minetMainInterface.updateContactList();
    } catch (NullPointerException e) {
      
    }
  }
  
  // 新建群组包含本用户的话，服务器会广播通知；本函数用来监听新建群组广播
  public static void handler_broadcast_new_group(HashMap object) 
      throws JsonGenerationException, JsonMappingException, IOException {
    String groupname = (String) object.get("data");
    group_list.put(groupname, null);
    Minet.minetMainInterface.updateGroupChattingList();
  }
  
  // 群聊广播信息处理函数
  public static void handler_broadcast_group_chatting_record(HashMap object) 
      throws UnknownHostException, IOException {
    String groupname = (String) object.get("from");
    String data = (String) object.get("data");
    String username = data.split(":")[0];
    String content = data.split(":")[1];
    
    ArrayList<ChattingRecordItem> record = 
        Minet.minetMainInterface.chatting_record.get(groupname);
    if (record == null) record = new ArrayList<ChattingRecordItem>();
    record.add(new ChattingRecordItem(username, content));
    
    Minet.minetMainInterface.chatting_record.put(groupname, record);
    
    try {
      if (TCPClient.current_chatting_group != null &&
          Minet.minetMainInterface.getChattingTarget().equals(
          TCPClient.current_chatting_group)) {
        Minet.minetMainInterface.updateChattingWindow(TCPClient.current_chatting_group);
      }
    } catch (NullPointerException e) {
      
    }
    
  }
  
  // 客户端告知服务器即将下线
  public static void send_logout() throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol requestObject = new MMprotocol(ProtocolType.SENDTO_LOGOUT, 
        TCPClient.username, null, null, null, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    @SuppressWarnings("resource")
    Socket clientSocket = new Socket(serverIP, serverPort);
    DataOutputStream outToTarget =
        new DataOutputStream(clientSocket.getOutputStream());
    
    outToTarget.writeBytes(json + '\n');
  }
  
  // 向服务器请求在线用户信息
  public static void request_onlineUsersList() 
      throws JsonGenerationException, JsonMappingException, IOException {
    
    MMprotocol requestObject = new MMprotocol(ProtocolType.REQUEST_ONLINE_USER_LIST, 
        TCPClient.username, null, "HashMap", null, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    @SuppressWarnings("resource")
    Socket clientSocket = new Socket(serverIP, serverPort);
    
    DataOutputStream outToServer =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToServer.writeBytes(json + '\n');
    
    String response = inFromServer.readLine();
    
    HashMap resObj = mapper.readValue(response, HashMap.class);
    String userListJson = (String) resObj.get("data");
    
    HashMap userListMap = 
        mapper.readValue(userListJson, HashMap.class);
    
    onlineUsersMap.clear();
    Iterator iter = userListMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();
      HashMap val = (HashMap) entry.getValue();
      
      String ip = (String) val.get("ip");
      int port = (int) val.get("port");
      
      onlineUsersMap.put(key, new OnlineUserListItem(ip, port));
    }
  }
  
  // 登录请求
  public static Boolean request_login(String username, String pass)
      throws JsonGenerationException, JsonMappingException, IOException {
    
    MMprotocol requestObject = new MMprotocol(ProtocolType.SENDTO_LOGIN, 
        username, null, null, pass, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    Socket clientSocket = new Socket(serverIP, serverPort);
    
    DataOutputStream outToServer =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToServer.writeBytes(json + '\n');
    
    String response = inFromServer.readLine();
    HashMap resObj = mapper.readValue(response, HashMap.class);
    
    String data = (String) resObj.get("data");
    String[] result = mapper.readValue(data, String[].class);
    
    String[] groups = null;
    if (result[1] != null && !result[1].equals("null")) {
      groups = mapper.readValue(result[1], String[].class);
    }
    
    if (result[0].equals("true")) {
      TCPClient.username = username;
      
      if (!result[1].equals("null")) {
        for (int i = 0; i < groups.length; i++) {
          TCPClient.group_list.put(groups[i], null);
        }
      }
      return true;
    }
    else {
      return false;
    }
  }
  
  // 注册请求
  public static Boolean request_register(String username, String pass) 
      throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol requestObject = new MMprotocol(ProtocolType.SENDTO_REGISTER, 
        username, null, null, pass, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    Socket clientSocket = new Socket(TCPClient.serverIP, TCPClient.serverPort);
    
    DataOutputStream outToServer =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToServer.writeBytes(json + '\n');
    
    String response = inFromServer.readLine();
    HashMap resObj = mapper.readValue(response, HashMap.class);
    
    String isRegister = (String) resObj.get("data");
    
    if (isRegister.equals("true")) {
      TCPClient.username = username;
      return true;
    }
    else {
      return false;
    }
  }

  // 发送P2P信息
  public static void send_chatting_item(String content) 
      throws UnknownHostException, IOException {
    
    MMprotocol requestObject = new MMprotocol(ProtocolType.SENDTO_PERSONAL_CHATTING_ITEM, 
        username, TCPClient.current_chatting_user, "text", content, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    OnlineUserListItem target = (OnlineUserListItem) 
        onlineUsersMap.get(TCPClient.current_chatting_user);
    
    @SuppressWarnings("resource")
    Socket clientSocket = new Socket(target.getIp(), target.getPort());
    
    DataOutputStream outToTarget =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToTarget.writeBytes(json + '\n');
  }

  // 新建群组
  public static Boolean new_group_chatting(String[] chatting_users)
      throws JsonGenerationException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    
    String json_chatting_users = mapper.writeValueAsString(chatting_users);
    
    MMprotocol requestObject = new MMprotocol(ProtocolType.SENDTO_NEW_GROUP_CHATTING, 
        TCPClient.username, null, "StringArray", json_chatting_users, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    String json = mapper.writeValueAsString(requestObject);
    
    @SuppressWarnings("resource")
    Socket clientSocket = new Socket(serverIP, serverPort);
    
    DataOutputStream outToServer =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToServer.writeBytes(json + '\n');
    
    String response = inFromServer.readLine();
    HashMap resObj = mapper.readValue(response, HashMap.class);
    
    String is_new_group_chatting_success = (String) resObj.get("data");
    
    if (is_new_group_chatting_success.equals("true")) {
      TCPClient.username = username;
      return true;
    }
    else {
      return false;
    }
  }
  
  // 请求群组信息
  public static void request_update_group_list() 
      throws JsonGenerationException, JsonMappingException, IOException {
    
    MMprotocol requestObject = new MMprotocol(ProtocolType.REQUEST_GROUP_LIST, 
        TCPClient.username, null, null, null, new Date(), 
        TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    Socket clientSocket = new Socket(serverIP, serverPort);
    
    DataOutputStream outToServer =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromServer =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToServer.writeBytes(json + '\n');
    
    String response = inFromServer.readLine();
    
    HashMap resObj = mapper.readValue(response, HashMap.class);
    String json_grouplist = (String) resObj.get("data");
    
    String[] groups = 
        mapper.readValue(json_grouplist, String[].class);
    
    for (int i = 0; i < groups.length; i++) {
      group_list.put(groups[i], null);
    }
  }

  // 发起屏幕共享申请
  public static Boolean request_share_screen() 
      throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol requestObject = new MMprotocol(ProtocolType.REQUEST_SHARE_SCREEN, 
        TCPClient.username, TCPClient.current_chatting_user, null, null, new Date(), 
        TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(requestObject);
    
    OnlineUserListItem item = (OnlineUserListItem) 
        onlineUsersMap.get(TCPClient.current_chatting_user);
    
    String ip = item.getIp();
    int port = item.getPort();
    
    Socket clientSocket = new Socket(ip, port);
    
    DataOutputStream outToOtherClient =
        new DataOutputStream(clientSocket.getOutputStream());
    BufferedReader inFromOtherClient =
        new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    outToOtherClient.writeBytes(json + '\n');
    
    String response = inFromOtherClient.readLine();
    
    HashMap resObj = mapper.readValue(response, HashMap.class);
    String result = (String) resObj.get("data");
    
    System.out.println(Utils.getDate() + "result is: " + result);
    
    if (result.equals("0")) {
      return true;
    } else {
      return false;
    }
  }
  
  // 响应屏幕共享申请
  public static void response_share_screen(HashMap object, DataOutputStream outToOtherClient) 
      throws JsonGenerationException, JsonMappingException, IOException {
    
    String username = (String) object.get("from");
    int result = JOptionPane.showConfirmDialog(
        null, "是否接受来自" + username + "的屏幕共享申请");
    
    // 如果确认，启动屏幕共享函数
    if (result == 0) {
      Minet.minetMainInterface.actionScreenShareServe();
    } else {
      // do nothing
    }
    
    MMprotocol responseObject = new MMprotocol(ProtocolType.RESPONSE_SHARE_SCREEN, 
        TCPClient.username, username, "text", result + "", new Date(), 
        TCPClient.sourseIP, TCPClient.soursePort);
    ObjectMapper mapper = new ObjectMapper();
    
    String json_response = mapper.writeValueAsString(responseObject);
    outToOtherClient.writeBytes(json_response + '\n');
  }
  
  // 作为客户端，发送给作为服务端的客户端屏幕分享关闭的通知
  public static void info_share_screen_shutdown() 
      throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol responseObject = new MMprotocol(ProtocolType.INFO_SHARE_SCREEN_SHUTDOWN, 
        null, null, null, null, new Date(), null, 0);
    ObjectMapper mapper = new ObjectMapper();
    String json_response = mapper.writeValueAsString(responseObject);
    
    OnlineUserListItem item = (OnlineUserListItem) onlineUsersMap.get(
        Minet.minetMainInterface.current_screen_share_user);
    String ip = item.getIp();
    int port = item.getPort();
    
    Socket connectionSocket = new Socket(ip, port);
    DataOutputStream outToOtherClient = new DataOutputStream(
        connectionSocket.getOutputStream());
    
    outToOtherClient.writeBytes(json_response + "\n");
  }
  
  // 处理客户端发送过来的屏幕分享关闭的通知
  public static void handler_share_screen_shutdown() {
    TCPClient.screenShareServer.suspend();
    Minet.minetMainInterface.shareScreen.setEnabled(true);
    Minet.minetMainInterface.shareScreen.doClick();
  }
}
