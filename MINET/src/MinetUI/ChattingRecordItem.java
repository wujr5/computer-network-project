package MinetUI;

/**
 * 聊天信息记录类
 * @author jiarong
 *
 */
public class ChattingRecordItem {
  public String username; // 用户名
  public String content; // 内容
  
  public ChattingRecordItem(String username, String content) {
    setUsername(username);
    setContent(content);
  }
  
  public void setUsername(String username) { this.username = username;}
  public String getUsername() { return this.username;}
  
  public void setContent(String content) { this.content = content;}
  public String getContent() { return this.content;}
}