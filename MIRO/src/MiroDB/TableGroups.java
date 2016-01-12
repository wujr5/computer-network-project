package MiroDB;

/**
 * @author jiarong
 * todo:
 * 1. 增加creater
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import Server.TCPServer;
import Utils.Utils;
import Miro.Miro;

public class TableGroups {
  
  // 取得群组HashMap对象
  public static HashMap get_group_hash_map(String groupname) 
      throws SQLException {
    String sql = "SELECT * FROM GROUPS WHERE groupname = " + "'" + groupname + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    HashMap aHashMap = new HashMap();
    return aHashMap;
  }
  
  // 根据组名字取得改组成员信息
  public static String[] get_group_users(String groupname) 
      throws SQLException, JsonParseException, JsonMappingException, IOException {
    String sql = "SELECT * FROM GROUPS WHERE groupname = " + "'" + groupname + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    String json_groupusers = MiroDB.rs.getString("users");
    ObjectMapper mapper = new ObjectMapper();
    String[] groupuser = mapper.readValue(json_groupusers, String[].class);
    return groupuser;
  }
  
  // 新建群组处理函数
  public static Boolean new_group_chatting(String[] users) 
      throws SQLException, JsonGenerationException, JsonMappingException, IOException {
    String groupname = users[users.length - 1];
    
    String[] groupusers = new String[users.length - 1];
    
    for (int i = 0; i < groupusers.length; i++) 
      groupusers[i] = users[i];
    
    ObjectMapper mapper = new ObjectMapper();
    String json_groupusers = mapper.writeValueAsString(groupusers);
    
    String sql = "SELECT * FROM GROUPS WHERE groupname = " + "'" + groupname + "'";
    MiroDB.rs = MiroDB.stmt.executeQuery(sql);
    
    if ( MiroDB.rs.next() ) {
//      System.out.println(Utils.getDate() + ": " + groupname + " already exist!");
      Miro.miro.miroPrint(Utils.getDate() + ": " + groupname + " already exist!");
      return false;
    } else {
      Date time = new Date();
      sql = "INSERT INTO GROUPS (GROUPNAME, USERS, CREATE_AT) " +
          "VALUES ('" + groupname + "', '" +  json_groupusers + "'," + time.getTime() + ");";
      
      MiroDB.stmt.executeUpdate(sql);
      
      for (int i = 0; i < groupusers.length; i++) {
        TableUsers.user_update_groups(groupusers[i], groupname);
        TCPServer.update_group_list(groupusers[i], groupname);
      }
      
      TCPServer.broadcast_new_group(groupusers, groupname);
      
//      System.out.println((new Date()).toString() + ": " + groupname + " created");
      Miro.miro.miroPrint((new Date()).toString() + ": " + groupname + " created");
      return true;
    }
  }
}
