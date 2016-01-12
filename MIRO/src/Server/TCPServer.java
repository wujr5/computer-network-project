package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jdk.internal.org.objectweb.asm.Handle;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import Miro.Miro;
import MiroDB.MiroDB;
import MiroDB.TableGroups;
import MiroDB.TableUsers;
import Protocol.MMprotocol;
import Protocol.ProtocolType;
import Utils.Utils;
import OnlineUsers.*;
/**
 * @author jiarong
 * todo:
 */

public class TCPServer {
  public static HashMap<String, OnlineUserListItem> onlineUsersMap;
  public static String serverIP;
  public static int serverPort;
  
  // 服务器启动“主”函数
  public static void TCPServerStart() 
      throws IOException, ClassNotFoundException, SQLException {
    initMiroDB(); // 数据库初始化设置
    initTCPServer(); // TCP服务器初始化设置
    createSocketListener(); // 启动Socket监听
  }
  
  // 数据库初始化
  public static void initMiroDB() 
      throws ClassNotFoundException, SQLException {
    MiroDB.init();
  }
  
  // TCP服务器初始化
  public static void initTCPServer() throws UnknownHostException {
    onlineUsersMap = new HashMap<String, OnlineUserListItem>();
    String temp = InetAddress.getLocalHost().toString();
    serverIP = temp.substring(temp.indexOf('/') + 1);
    serverPort = 6789;
  }
  
  // 创建Socket监听，启动服务器
  public static void createSocketListener() throws IOException, SQLException {
//    System.out.println(Utils.getDate() + "TCP Server start...");
    Miro.miro.miroPrint(Utils.getDate() + "TCP Server start...");
    Miro.miro.miroPrint(Utils.getDate() + "Server IP: " + serverIP + ", Server Port: " + serverPort);
    ServerSocket welcomeSocket = new ServerSocket(6789);
    
    while(true) {
      Socket connectionSocket = welcomeSocket.accept();
      
      BufferedReader inFromClient =
          new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
      DataOutputStream outToClient =
          new DataOutputStream(connectionSocket.getOutputStream());
      
      String reqJson = inFromClient.readLine();
      ObjectMapper mapper = new ObjectMapper();
      HashMap resObj = mapper.readValue(reqJson, HashMap.class);
      
      String type = (String) resObj.get("protocolType");
      
      switch (type) {
        case "SENDTO_REGISTER": // 对注册请求的响应函数
          handler_register(resObj, outToClient, connectionSocket);
          break;
        case "SENDTO_LOGIN": // 对登录请求的相应函数
          handler_login(resObj, outToClient, connectionSocket);
          break;
        case "REQUEST_ONLINE_USER_LIST": // 对获取在线用户列表信息的响应函数
          handler_request_online_user_list(resObj, outToClient, connectionSocket);
          break;
        case "SENDTO_NEW_GROUP_CHATTING": // 对新建群组的响应函数
          handler_new_group_chatting(resObj, outToClient, connectionSocket);
          break;
        case "SENDTO_LOGOUT": // 对用户登出通知（下线）的响应函数
          handler_logout(resObj);
          break;
        case "REQUEST_GROUP_LIST": // 对群组列表请求的响应函数
          handler_request_group_list(resObj, outToClient, connectionSocket);
          break;
        case "BROADCAST_GROUP_CHATTING_RECORD": // 对群组信息的响应函数
          broadcast_group_chatting_record(resObj);
          break;
        default:
//          System.out.println(Utils.getDate() + "Here is default!");
          Miro.miro.miroPrint(Utils.getDate() + "Here is default!");
          break;
      }
    }
  }
  
  // 注册处理函数
  public static void handler_register(
      HashMap object, DataOutputStream outToClient, Socket connectionSocket) 
      throws JsonGenerationException, JsonMappingException, IOException, SQLException {
    
    String username = (String) object.get("from");
    String pass = (String) object.get("data");
    Boolean result = TableUsers.user_register(username, pass);
    
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_REGISTER, 
        null, username, null, result.toString(), new Date(), null, 0);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(response);
    
    outToClient.writeBytes(json + '\n');
    
    if (result) {
      String sourseIP = (String) object.get("sourseIP");
      int soursePort = (int) object.get("soursePort");
      
      OnlineUserListItem item = new OnlineUserListItem(sourseIP, soursePort, null);
      onlineUsersMap.put(username, item);
      
      broadcast_user_online_or_offline(username, sourseIP, soursePort);
    }
  }
  
  // 登录处理函数
  public static void handler_login(
      HashMap object, DataOutputStream outToClient, Socket connectionSocket)
      throws SQLException, JsonGenerationException, JsonMappingException, IOException {
    
    String username = (String) object.get("from");
    String pass = (String) object.get("data");
    String sourseIP = (String) object.get("sourseIP");
    int soursePort = (int) object.get("soursePort");
    
    TableUsers.user_login(username, pass, sourseIP, soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    
    OnlineUserListItem online_item = onlineUsersMap.get(username);
    
    String[] result = new String[2];
    
    if (online_item != null) {
      result[0] = "true";
      result[1] = mapper.writeValueAsString(online_item.getGroups());
    } else {
      result[0] = "false";
    }
    
    String json_result = mapper.writeValueAsString(result);
    
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_LOGIN, 
        null, username, null, json_result, new Date(), null, 0);
    
    String json = mapper.writeValueAsString(response);
    outToClient.writeBytes(json + '\n');
    
    // 广播登录用户的信息
    if (online_item != null) {
      broadcast_user_online_or_offline(username, sourseIP, soursePort);
    }
  }
  
  // 请求在线用户列表处理函数
  public static void handler_request_online_user_list(
      HashMap object, DataOutputStream outToClient, Socket connectionSocket) 
          throws JsonGenerationException, JsonMappingException, IOException {
    
    ObjectMapper mapper = new ObjectMapper();
    String userList = mapper.writeValueAsString(onlineUsersMap);
    
    String username = (String) object.get("from");
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_ONLINE_USER_LIST, 
        null, username, "OnlineUserListItem", userList, new Date(), null, 0);
    
    String json = mapper.writeValueAsString(response);
    outToClient.writeBytes(json + '\n');
  }
  
  //新建群组处理函数
  public static void handler_new_group_chatting(
      HashMap object, DataOutputStream outToClient, Socket connectionSocket) 
          throws SQLException, JsonGenerationException, JsonMappingException, IOException {
    String json_data = (String) object.get("data");
    String from = (String) object.get("from");
    
    ObjectMapper mapper = new ObjectMapper();
    
    String[] chatting_users = mapper.readValue(json_data, String[].class);
    
    Boolean result = TableGroups.new_group_chatting(chatting_users);
    
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_REGISTER, 
        null, from, null, result.toString(), new Date(), null, 0);
    
    String json = mapper.writeValueAsString(response);
    
    outToClient.writeBytes(json + '\n');
  }
  
  // 处理客户端下线通知
  public static void handler_logout(HashMap resObj) 
      throws JsonGenerationException, JsonMappingException, IOException {
    String username = (String) resObj.get("from");
    String sourseIP = (String) resObj.get("sourseIP");
    int soursePort = (int) resObj.get("soursePort");
    
    onlineUsersMap.remove(username);
    
    Miro.miro.miroPrint(Utils.getDate() + "logout: " + username);
    
    ObjectMapper mapper = new ObjectMapper();
    
    MMprotocol response = new MMprotocol(ProtocolType.BROADCAST_USER_ONLINE_OR_OFFLIE, 
        null, null, "text", username + ":logout", null, sourseIP, soursePort);
    
    String json = mapper.writeValueAsString(response);
    
    Iterator iter = onlineUsersMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();
      OnlineUserListItem val = (OnlineUserListItem) entry.getValue();
      
      String ip = val.getIp();
      int port = val.getPort();
      
      Socket clientSocket = null;
      try {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(2 * 1000);
      } catch (ConnectException e) {
        continue;
      }
      
      DataOutputStream outToClients =
          new DataOutputStream(clientSocket.getOutputStream());
      
      outToClients.writeBytes(json + '\n');
      clientSocket.close();
    }
  }
  
  //请求群组数据
  public static void handler_request_group_list(
      HashMap object, DataOutputStream outToClient, Socket connectionSocket) 
          throws IOException, SQLException {
    
//    System.out.println(Utils.getDate() + "server: handler request group list");
    Miro.miro.miroPrint(Utils.getDate() + "server: handler request group list");
    String from = (String) object.get("from");
    
    HashMap groupList = TableGroups.get_group_hash_map(from);
    
    ObjectMapper mapper = new ObjectMapper();
    String json_groupList = mapper.writeValueAsString(groupList);
    
//    System.out.println(Utils.getDate() + json_groupList);
    Miro.miro.miroPrint(Utils.getDate() + json_groupList);
    
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_REGISTER, 
        null, from, "text", json_groupList, new Date(), null, 0);
    String json_response = mapper.writeValueAsString(response);
    
    outToClient.writeBytes(json_response + '\n');
  }
  
  // 广播群聊信息
  public static void broadcast_group_chatting_record(HashMap object) 
      throws JsonParseException, JsonMappingException, SQLException, IOException {
    String content = (String) object.get("data");
    String groupname = (String) object.get("to");
    String from = (String) object.get("from");
    
    String[] groupusers = TableGroups.get_group_users(groupname);
    
    MMprotocol res = new MMprotocol(ProtocolType.BROADCAST_GROUP_CHATTING_RECORD, 
        groupname, null, "text", from + ":" + content, new Date(), null, 0);
    ObjectMapper mapper = new ObjectMapper();
    String json_broadcast = mapper.writeValueAsString(res);
    
    for (int i = 0; i < groupusers.length; i++) {
      String username = groupusers[i];
      
      if (username.equals(from)) continue;
      
      OnlineUserListItem item = onlineUsersMap.get(username);
      
      if (item == null) continue;
      
      String ip = item.getIp();
      int port = item.getPort();
      
      Socket clientSocket = null;
      try {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(2 * 1000);
      } catch (ConnectException e) {
        continue;
      }
      
      DataOutputStream outToClients =
          new DataOutputStream(clientSocket.getOutputStream());
      
      outToClients.writeBytes(json_broadcast + '\n');
      clientSocket.close();
    }
  }
  
  // 广播用户上线下线通知
  public static void broadcast_user_online_or_offline(
      String username, String sourseIP, int soursePort) 
          throws JsonGenerationException, JsonMappingException, IOException {
    
    MMprotocol res = new MMprotocol(ProtocolType.BROADCAST_USER_ONLINE_OR_OFFLIE, 
        null, null, "text", username + ":login", null, sourseIP, soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json_broadcast = mapper.writeValueAsString(res);
    
    Iterator iter = onlineUsersMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();
      OnlineUserListItem val = (OnlineUserListItem) entry.getValue();
      
      String ip = val.getIp();
      int port = val.getPort();
      
      Socket clientSocket = null;
      try {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(2 * 1000);
      } catch (ConnectException e) {
        continue;
      }
      
      DataOutputStream outToClients =
          new DataOutputStream(clientSocket.getOutputStream());
      
      outToClients.writeBytes(json_broadcast + '\n');
      clientSocket.close();
    }
  }
  
  // 更新在线用户列表
  public static void updateUsersListMap() throws UnknownHostException, IOException {
    Iterator iter = TCPServer.onlineUsersMap.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = null;
      String key = null;
      OnlineUserListItem val = null;
      try {
        entry = (Map.Entry) iter.next();
        key = (String) entry.getKey();
        val = (OnlineUserListItem) entry.getValue();
      } catch (ConcurrentModificationException e) {
        return;
      }
      
      String ip = val.getIp();
      int port = val.getPort();
      
      Socket clientSocket = null;
      try {
        clientSocket = new Socket(ip, port);
        clientSocket.setSoTimeout(2 * 1000);
      } catch (ConnectException e) {
//        System.out.println(Utils.getDate() + "offline: " + key);
        Miro.miro.miroPrint(Utils.getDate() + "offline: " + key);
        TCPServer.onlineUsersMap.remove(key);
        continue;
      }
      
      DataOutputStream outToClients =
          new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inFromClients =
          new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      
      MMprotocol request = new MMprotocol(
          ProtocolType.REQUEST_ONLINE_OR_OFFLINE, null, key, 
          null, null, new Date(), null, 0);
      
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(request);
      
      outToClients.writeBytes(json + '\n');
      
      String response = null;
      try {
        response = inFromClients.readLine();
//        System.out.println(Utils.getDate() + "online: " + key);
        Miro.miro.miroPrint(Utils.getDate() + "online: " + key);
      } catch (SocketException e) {
//        System.out.println(Utils.getDate() + "offline: " + key);
        Miro.miro.miroPrint(Utils.getDate() + "offline: " + key);
        TCPServer.onlineUsersMap.remove(key);
      }
      clientSocket.close();
    }
  }
  
  // 更新群组列表
  public static void update_group_list(String username, String groupname) {
    OnlineUserListItem item = TCPServer.onlineUsersMap.get(username);
    
    if (item != null) {
      String[] groups = item.getGroups();
      String[] new_groups = null;
      if (groups == null) {
        new_groups = new String[1];
        new_groups[0] = groupname;
      } else {
        new_groups = new String[groups.length + 1];
        new_groups[new_groups.length - 1] = groupname;
      }
      item.setGroups(new_groups);
      TCPServer.onlineUsersMap.put(username, item);
    }
  }

  // 广播新建群组
  public static void broadcast_new_group(String[] groupusers, String groupname) 
      throws IOException {
    for (int i = 0; i < groupusers.length; i++) {
      MMprotocol request = new MMprotocol(
          ProtocolType.BROADCAST_NEW_GROUP, null, groupusers[i], 
          "text", groupname, new Date(), null, 0);
      
      OnlineUserListItem item = onlineUsersMap.get(groupusers[i]);
      
      Socket clientSocket = new Socket(item.getIp(), item.getPort());
      DataOutputStream outToClients =
          new DataOutputStream(clientSocket.getOutputStream());
      
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(request);
      
      outToClients.writeBytes(json + '\n');
    }
  }
}
