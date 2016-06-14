package uk.ac.bbsrc.tgac.miso.spring;

import java.io.File;
import javax.servlet.http.HttpSession;

/**
 * For when you need web utils, since this module can't access `miso-web`.
 *
 */
public class ControllerHelperServiceUtils {
  /**
   * Returns the location of the identificationBarcode image storage folder
   * @param HttpSession session
   * @return File 
   */
  public static File getBarcodeFileLocation(HttpSession session) {
    return new File(session.getServletContext().getRealPath("/temp/"));
  }
}
