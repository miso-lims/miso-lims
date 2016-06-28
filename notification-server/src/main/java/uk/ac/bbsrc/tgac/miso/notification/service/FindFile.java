package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Given an input path, find matching files, possibly gzipped and open them.
 * 
 * @author amasella
 *
 */
public class FindFile extends RunTransform<String, InputStream> {

  private final String[] extensions;

  /**
   * Check for the supplied files
   * 
   * @param extensions
   *          The names of the files that will be appended to the input path; they will automatically be suffixed with ".gz"
   */
  public FindFile(String... extensions) {
    this.extensions = extensions;
  }

  @Override
  protected InputStream convert(String input, IlluminaRunMessage output) throws Exception {
    for (String extension : extensions) {
      File target = new File(input + extension);
      log.debug("Checking " + target.getPath());
      if (target.exists() && target.canRead()) {
        return new FileInputStream(target);
      }
      target = new File(input + extension + ".gz");
      log.debug("Checking " + target.getPath());
      if (target.exists() && target.canRead()) {
        return new GZIPInputStream(new FileInputStream(target));
      }
    }
    log.debug("No files");
    return null;
  }

}
