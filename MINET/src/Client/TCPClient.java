package Client;

/**
 * @author jiarong
 * to-do:
 * 1. json������������
 * 2. soursePort��̬ѡ��
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
  public static String serverIP; // ��¼����������IP
  public static int serverPort; // ��¼�������˿�
  public static String sourseIP; // �ͻ���IP
  public static int soursePort; // �ͻ��˶˿�
  
  public static String username; // �Ѿ���¼���û���
  
  public static String current_chatting_user = ""; // ��ǰ������û�
  public static String current_chatting_group = ""; // ��ǰȺ��
  public static HashMap onlineUsersMap = null; // ��¼�����û���Ϣ��HashMap
  public static HashMap<String, String[]> group_list = null; // ��¼Ⱥ����Ϣ
  public static ScreenShareServer screenShareServer = null;
  
  // ��ʼ��
  public static void init() throws UnknownHostException {
    String temp = InetAddress.getLocalHost().toString();
    
    // "172.18.182.75"; // "192.168.199.248"; 
    // serverIP = temp.substring(temp.indexOf('/') + 1); // ���÷�����IP
    serverPort = 6789; // ���÷���˶˿�
    sourseIP = temp.substring(temp.indexOf('/') + 1); // �ͻ���IP
    soursePort = 6788; // �ͻ��� �˿�
    
    if (onlineUsersMap == null)
      onlineUsersMap = new HashMap<String, OnlineUserListItem>();
    if (group_list == null)
      group_list = new HashMap<String, String[]>();
  }
  
  // ���������Socket����
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
        case "REQUEST_ONLINE_OR_OFFLINE": // �������û���Ϣ�����������Ӧ����
          handler_updateUsersList(outToServerOrOtherClients);
          break;
        case "SENDTO_PERSONAL_CHATTING_ITEM": // ��P2P��Ϣ����Ӧ����
          handler_receive_personal_chatting_item(reqObj);
          break;
        case "BROADCAST_USER_ONLINE_OR_OFFLIE": // ���û������߹㲥��Ϣ����Ӧ����
          handler_broadcast_user_online_or_offline(reqObj);
          break;
        case "BROADCAST_NEW_GROUP": // ���½�Ⱥ��㲥��Ϣ����Ӧ����
          handler_broadcast_new_group(reqObj);
          break;
        case "BROADCAST_GROUP_CHATTING_RECORD": // ��Ⱥ�Ĺ㲥��Ϣ����Ӧ����
          handler_broadcast_group_chatting_record(reqObj);
          break;
        case "REQUEST_SHARE_SCREEN": // ��������Ļ�������Ӧ����
          response_share_screen(reqObj, outToServerOrOtherClients);
          break;
        case "INFO_SHARE_SCREEN_SHUTDOWN": // �Թر���Ļ����֪ͨ����Ӧ����
          handler_share_screen_shutdown(); // ��Ϊ����ˣ���Ӧ��Ļ����رյ�֪ͨ
          break;
        default:
          System.out.println("Here is default!");
          break;
      }
    }
  }
  
  // �����û��б�Ĵ�����
  public static void handler_updateUsersList(DataOutputStream outToServerOrOtherClients) 
          throws JsonGenerationException, JsonMappingException, IOException {
    MMprotocol response = new MMprotocol(ProtocolType.RESPONSE_ONLINE_USER_LIST, TCPClient.username,
        null, null, null, new Date(), TCPClient.sourseIP, TCPClient.soursePort);
    
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(response);
    
    outToServerOrOtherClients.writeBytes(json + "\n");
  }
  
  // P2Pͨ�Ŵ�����
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
  
  // �㲥Ⱥ����Ϣ
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
  
  // ���û����߻���������Ϣ֪ͨ�Ĵ�����
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
      // ����һ�γ��ּ����contact list��ѡ���޷�ѡ�е�bug��������������
      Minet.minetMainInterface.updateContactList();
      Minet.minetMainInterface.updateContactList();
    } catch (NullPointerException e) {
      
    }
  }
  
  // �½�Ⱥ��������û��Ļ�����������㲥֪ͨ�����������������½�Ⱥ��㲥
  public static void handler_broadcast_new_group(HashMap object) 
      throws JsonGenerationException, JsonMappingException, IOException {
    String groupname = (String) object.get("data");
    group_list.put(groupname, null);
    Minet.minetMainInterface.updateGroupChattingList();
  }
  
  // Ⱥ�Ĺ㲥��Ϣ������
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
  
  // �ͻ��˸�֪��������������
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
  
  // ����������������û���Ϣ
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
  
  // ��¼����
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
  
  // ע������
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

  // ����P2P��Ϣ
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

  // �½�Ⱥ��
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
  
  // ����Ⱥ����Ϣ
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

  // ������Ļ��������
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
  
  // ��Ӧ��Ļ��������
  public static void response_share_screen(HashMap object, DataOutputStream outToOtherClient) 
      throws JsonGenerationException, JsonMappingException, IOException {
    
    String username = (String) object.get("from");
    int result = JOptionPane.showConfirmDialog(
        null, "�Ƿ��������" + username + "����Ļ��������");
    
    // ���ȷ�ϣ�������Ļ������
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
  
  // ��Ϊ�ͻ��ˣ����͸���Ϊ����˵Ŀͻ�����Ļ����رյ�֪ͨ
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
  
  // ����ͻ��˷��͹�������Ļ����رյ�֪ͨ
  public static void handler_share_screen_shutdown() {
    TCPClient.screenShareServer.suspend();
    Minet.minetMainInterface.shareScreen.setEnabled(true);
    Minet.minetMainInterface.shareScreen.doClick();
  }
}
