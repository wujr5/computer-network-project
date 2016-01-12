package Miro;

import javax.swing.UIManager;

public class UIHelpers {
  public static void setLookAndFeel() {
    try {
      String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
      UIManager.setLookAndFeel(lookAndFeel);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
