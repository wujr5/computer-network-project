package Client;

/**
 * �����û��б���Ϣ�ļ�¼��
 * @author jiarong
 *
 */

public class OnlineUserListItem {
  private String ip; // ip
  private int port; // �˿�
  
  public OnlineUserListItem(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }
  
  public void setIp(String ip) { this.ip = ip;}
  public String getIp() { return ip;}
  
  public void setPort(int port) { this.port = port;}
  public int getPort() { return port;}
  
  @Override
  public String toString() {
    return (ip + ":" + port);
  }
}