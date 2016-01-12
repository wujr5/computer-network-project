package MiroDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Miro.Miro;
import Utils.Utils;

public class MiroDB {
  public static Connection miroDBConnection;
  public static Statement stmt;
  public static ResultSet rs;
  
  // 数据库初始化
  public static void init() throws SQLException, ClassNotFoundException {
    miroDBConnection = DriverManager.getConnection("jdbc:sqlite:miro.db");
    stmt = miroDBConnection.createStatement();
    Class.forName("org.sqlite.JDBC");
    miroDBConnection.setAutoCommit(true);
    rs = null;
    
//    System.out.println(Utils.getDate() + "Opened database: miro.db");
    Miro.miro.miroPrint(Utils.getDate() + "Opened database: miro.db");
    
    try {
      String sql = 
          "CREATE TABLE USERS (" +
          "USERNAME   TEXT PRIMARY KEY NOT NULL, " +
          "PASS       TEXT             NOT NULL, " + 
          "CREATE_AT  INT              NOT NULL, " + 
          "GROUPS     TEXT);";
      
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
//      System.out.println(Utils.getDate() + "Table USERS have already existed!");
      Miro.miro.miroPrint(Utils.getDate() + "Table USERS have already existed!");
    }
    
    try {
      String sql = 
          "CREATE TABLE GROUPS (" +
          "GROUPNAME  TEXT PRIMARY KEY NOT NULL, " +
          "USERS      TEXT             NOT NULL, " + 
          "CREATE_AT  INT              NOT NULL);";
      
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
//      System.out.println(Utils.getDate() + "Table GROUPS have already existed!");
      Miro.miro.miroPrint(Utils.getDate() + "Table GROUPS have already existed!");
    }
  }
}
