package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;

public class MatchPatternInFile extends RunTransform<String, String> {

  private final Pattern[] patterns;
  private final String extension;

  public MatchPatternInFile(String extension, String... patterns) {
    this.extension = extension;
    this.patterns = new Pattern[patterns.length];
    for (int i = 0; i < patterns.length; i++) {
      this.patterns[i] = Pattern.compile(patterns[i]);
    }
  }

  @Override
  protected String convert(String input, IlluminaRunMessage output) throws Exception {
    File rootFile = new File(input);
    if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, extension)) {
      for (Pattern pattern : patterns) {
        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, extension, pattern, 10);
        if (m != null && m.groupCount() > 0) {
          return m.group(1) + (m.groupCount() > 1 ? "," + m.group(2) : "");
        }
      }
    }
    return null;
  }

}
