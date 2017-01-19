package uk.ac.bbsrc.tgac.miso.core.util;

/**
 * Created by zakm on 06/08/2015.
 */
public class KitUtils {
  public static boolean toBoolean(int boolInt) {
    return boolInt != 0;
  }

  public static int toInt(boolean boolInt) {
    return (boolInt) ? 1 : 0;
  }
}