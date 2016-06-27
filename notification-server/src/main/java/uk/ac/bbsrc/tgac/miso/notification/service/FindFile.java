package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class FindFile extends RunTransform<String, InputStream> {

  private final String[] extensions;

  @SafeVarargs
  public FindFile(String... extensions) {
    this.extensions = extensions;
  }

  @Override
  protected InputStream convert(String input) throws Exception {
    for (String extension : extensions) {
      File target = new File(input + extension);
      if (target.exists() && target.canRead()) {
        return new FileInputStream(target);
      }
      target = new File(input + extension + ".gz");
      if (target.exists() && target.canRead()) {
        return new GZIPInputStream(new FileInputStream(target));
      }
    }
    return null;
  }

}
