package Main;

import java.io.IOException;

import MinetUI.*;
import Minet.Minet;
import Protocol.*;
import Client.TCPClient;

public class Main {
  /**
   * 客户端入口函数
   * @param argv
   * @throws IOException
   */
  public static void main(String argv[]) throws IOException {
    Minet myMinet = new Minet();
    myMinet.actionUI();
    TCPClient.init();
    TCPClient.TCPClientStart();
  }
}
