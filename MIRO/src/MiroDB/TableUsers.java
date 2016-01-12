package MiroDB;

import java.io.EOFException;
import java.io.IOException;
import java.sql.*;
import java.util.Date;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import Miro.Miro;
import OnlineUsers.OnlineUserListItem;
import Server.*;
import Utils.Utils;

public class TableUsers {
  // 用户注册处理函数
  public static Boolean user_register(String username, String pass) throws SQLException {
    String sql = "SELECT * FROM USERS WHERE username = " + "'" + username + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    if ( MiroDB.rs.next() ) {
//      System.out.println(Utils.getDate() + "register failed: " + username);
      Miro.miro.miroPrint(Utils.getDate() + "register failed: " + username);
      
      return false;
    } else {
      Date time = new Date();
      sql = "INSERT INTO USERS (USERNAME, PASS, CREATE_AT, GROUPS) " +
          "VALUES ('" + username + "', '" +  pass + "'," + time.getTime() + ", null);";
      
      MiroDB.stmt.executeUpdate(sql);
      
//      System.out.println(Utils.getDate() + "register success: " + username);
      Miro.miro.miroPrint(Utils.getDate() + "register success: " + username);
      return true;
    }
  }
  
  // 用户登录处理函数
  public static void user_login(String username, String pass, String sourseIP, int soursePort) 
      throws SQLException, JsonParseException, JsonMappingException, IOException {
    
    String sql = "SELECT * FROM USERS WHERE username = " + "'" + username + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    if ( MiroDB.rs.next() ) {
      String dbPass = MiroDB.rs.getString("pass");
      
      if (dbPass.equals(pass)) {
        String json_groups = MiroDB.rs.getString("groups");
        ObjectMapper mapper = new ObjectMapper();
        
        String[] groups = null;
        try {
          if (json_groups != null)
            groups = mapper.readValue(json_groups, String[].class);
        } catch (EOFException e) {
          
        }
        
        OnlineUserListItem item = new OnlineUserListItem(sourseIP, soursePort, groups);
        TCPServer.onlineUsersMap.put(username, item);
//        System.out.println(Utils.getDate() + "login success: " + username);
        Miro.miro.miroPrint(Utils.getDate() + "login success: " + username);
      } else {
//        System.out.println(Utils.getDate() + "login falied: " + username);
        Miro.miro.miroPrint(Utils.getDate() + "login falied: " + username);
      }
    } else {
//      System.out.println(Utils.getDate() + "login falied: " + username);
      Miro.miro.miroPrint(Utils.getDate() + "login falied: " + username);
    }
  }

  // 更新群组
  public static void user_update_groups(String username, String groupname) 
      throws SQLException, JsonParseException, JsonMappingException, IOException {
    String sql = "SELECT * FROM USERS WHERE username = " + "'" + username + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    ObjectMapper mapper = new ObjectMapper();
    
    String json_groups = MiroDB.rs.getString("groups");
    String[] groups = null;
    
    if (json_groups == null || json_groups.equals("")) {
      groups = new String[1];
      groups[0] = groupname;
    } else {
      String[] temp = mapper.readValue(json_groups, String[].class);
      groups = new String[temp.length + 1];
      for (int i = 0; i < temp.length; i++) groups[i] = temp[i];
      groups[groups.length - 1] = groupname;
    }
    
    String update_json = mapper.writeValueAsString(groups);
    
    sql = "UPDATE USERS set GROUPS = '" + update_json + "' where USERNAME='" + username + "';";
    MiroDB.stmt.executeUpdate(sql);
  }
}
