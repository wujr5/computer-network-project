package MinetUI;

/**
 * ������Ϣ��¼��
 * @author jiarong
 *
 */
public class ChattingRecordItem {
  public String username; // �û���
  public String content; // ����
  
  public ChattingRecordItem(String username, String content) {
    setUsername(username);
    setContent(content);
  }
  
  public void setUsername(String username) { this.username = username;}
  public String getUsername() { return this.username;}
  
  public void setContent(String content) { this.content = content;}
  public String getContent() { return this.content;}
}