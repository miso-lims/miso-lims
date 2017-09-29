package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import org.apache.log4j.Logger;

public class TagUtils {

  private static final Logger log = Logger.getLogger(TagUtils.class);

  public static boolean instanceOf(Object o, String className) {
    try {
      return Class.forName(className).isInstance(o);
    } catch (ClassNotFoundException e) {
      log.error(String.format("Failed to find the class name '%s'. %s", className, e.getMessage()), e);
      return false;
    }
  }

}
