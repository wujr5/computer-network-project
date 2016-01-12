package OnlineUsers;

/**
 * 在线用户列表记录item
 * @author jiarong
 *
 */

public class OnlineUserListItem {
  private String ip; // ip
  private int port; // 端口
  private String[] groups; // 所在群组？
  
  public OnlineUserListItem(String ip, int port, String[] groups) {
    setIp(ip);
    setPort(port);
    setGroups(groups);
  }
  
  public void setIp(String ip) { this.ip = ip;}
  public String getIp() { return ip;}
  
  public void setPort(int port) { this.port = port;}
  public int getPort() { return port;}
  
  public String[] getGroups() { return groups;}
  public void setGroups(String[] groups) { this.groups = groups;}

  @Override
  public String toString() {
    return (ip + ":" + port + ":" + groups.toString());
  }
}
