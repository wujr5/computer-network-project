package Protocol;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Э������
 * @author jiarong
 *
 */

public class MMprotocol {
  private ProtocolType protocolType; // Э�����
  private String from; // ������
  private String to; // ������
  private String dataType; // ��������
  private String data; // ����
  private Date date; // ����
  private String sourseIP; // ip
  private int soursePort; // �˿�
  
  public MMprotocol(ProtocolType protocolType, String from, String to, 
      String type, String data, Date date, String sourseIP, int soursePort) 
          throws UnknownHostException {
    setProtocolType(protocolType);
    setFrom(from);
    setTo(to);
    setType(type);
    setData(data);
    setDate(date);
    setSourseIP(sourseIP);
    setSoursePort(soursePort);
  }
  
  public void setProtocolType(ProtocolType pt) { protocolType = pt;}
  public ProtocolType getProtocolType() { return protocolType;}
  
  public void setFrom(String fr) { from = fr;}
  public String getFrom() { return from;}
  
  public void setTo(String t) { to = t;}
  public String getTo() { return to;}
  
  public void setType(String ty) { dataType = ty;}
  public String getType() { return dataType;}
  
  public void setData(String da) {data = da;}
  public String getData() { return data; }

  public void setDate(Date da) { date = da;}
  public Date getDate() { return date;}

  public String getSourseIP() { return sourseIP;}
  public void setSourseIP(String sourseIP) { this.sourseIP = sourseIP;}
  
  public int getSoursePort() { return soursePort;}
  public void setSoursePort(int soursePort) { this.soursePort = soursePort;}
}
