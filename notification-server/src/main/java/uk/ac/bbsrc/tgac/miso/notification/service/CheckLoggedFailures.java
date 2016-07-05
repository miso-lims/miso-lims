package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;

/**
 * Checks files in the run directory's /Data/RTALogs/ subdirectory for a log message indicating run failure
 */
class CheckLoggedFailures extends RunSink<String> {
  private final static Pattern FAILURE_REGEX = Pattern.compile(".*(Application\\s{1}exited\\s{1}before\\s{1}completion).*");
  private final static FilenameFilter MATCH_LOGS = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return (name.endsWith("Log_00.txt") || name.endsWith("Log_00.txt.gz") || name.equals("Log.txt") || name.equals("Log.txt.gz"));
    }
  };

  @Override
  public void process(String input, IlluminaRunMessage output) throws Exception {
    File rtaLogDir = new File(input, "/Data/RTALogs/");
    if (!rtaLogDir.exists()) {
      return;
    }

    for (File f : rtaLogDir.listFiles(MATCH_LOGS)) {
      Matcher m = PossiblyGzippedFileUtils.tailGrep(f, FAILURE_REGEX, 5);
      if (m != null && m.groupCount() > 0) {
        output.setHealth(HealthType.Failed);
        return;
      }
    }
  }
}