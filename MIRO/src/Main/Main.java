package Main;

import Miro.Miro;
import Miro.UIHelpers;
import Server.TCPServer;

public class Main {
  public static void main(String argv[]) throws Exception {
    UIHelpers.setLookAndFeel();
    Miro.miro = new Miro(); // ���������UI
    TCPServer.TCPServerStart(); // ��ʼTCP����
  }
}
