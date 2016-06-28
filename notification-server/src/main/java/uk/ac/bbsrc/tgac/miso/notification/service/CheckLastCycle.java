package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;

/**
 * Looks for evidence of the final cycle completing
 */
public class CheckLastCycle extends RunSink<String> {

  @Override
  public void process(String input, IlluminaRunMessage output) throws Exception {
    File rootFile = new File(input);
    // Check for Post Run Step log
    File dir = new File(rootFile, "/Logs/");
    FileFilter fileFilter = new WildcardFileFilter("*Post Run Step.log*");
    File[] filterFiles = dir.listFiles(fileFilter);
    if (filterFiles != null && filterFiles.length > 0) {
      output.setSeenLastCycle(true);
      return;
    }

    if (output.getNumCycles() != null) {
      // Check for last cycle log file
      if (PossiblyGzippedFileUtils.checkExists(rootFile,
          "/Logs/" + output.getRuninfo() + "_Cycle" + output.getNumCycles() + "_Log.00.log")) {
        output.setSeenLastCycle(true);
        return;
      }

      // Check CycleTimes.txt for last cycle complete log message
      String cycleTimeLogPath = "/Logs/CycleTimes.txt";
      if (PossiblyGzippedFileUtils.checkExistsReadable(rootFile, cycleTimeLogPath)) {
        Pattern p = Pattern.compile("(\\d{1,2}\\/\\d{1,2}\\/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})\\.\\d{3}\\s+[A-z0-9]+\\s+"
            + output.getNumCycles() + "\\s+End\\s{1}Imaging");

        Matcher m = PossiblyGzippedFileUtils.tailGrep(rootFile, cycleTimeLogPath, p, 10);
        if (m != null && m.groupCount() > 0) {
          output.setSeenLastCycle(true);
          return;
        }
      }
    }
  }

}
