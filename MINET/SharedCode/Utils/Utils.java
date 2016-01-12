package Utils;
import java.util.Date;


public class Utils {
  public static String getDate() {
    return (new Date()).toString() + ": ";
  }
}
