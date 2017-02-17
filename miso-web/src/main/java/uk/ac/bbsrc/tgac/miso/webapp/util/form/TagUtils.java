package uk.ac.bbsrc.tgac.miso.webapp.util.form;

public class TagUtils {

  public static boolean instanceOf(Object o, String className) {
    try {
      return Class.forName(className).isInstance(o);
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
