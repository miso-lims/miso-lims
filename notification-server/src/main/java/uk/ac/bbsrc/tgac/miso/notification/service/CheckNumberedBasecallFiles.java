package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;

import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;

final class CheckNumberedBasecallFiles extends RunSink<String> {
  private boolean checkAny(File rootDir, String... files) {
    for (String file : files) {
      if (PossiblyGzippedFileUtils.checkExists(rootDir, file)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void process(String input, IlluminaRunMessage output) throws Exception {
    if (output.getNumReads() == null || output.getNumReads() < 1) return;
    File rootDir = new File(input);
    for (int i = 1; i <= output.getNumReads(); i++) {
      if (!checkAny(rootDir, "/Basecalling_Netcopy_complete_Read" + i + ".txt", "/Basecalling_Netcopy_complete_READ" + i + ".txt",
          "/RTARead" + i + "Complete.txt")) {
        IlluminaTransformer.log.debug(output.getRunName() + " :: No Basecalling complete fo read " + i);
        output.setNumberedBaseCallsComplete(false);
        return;
      }
    }
    output.setNumberedBaseCallsComplete(true);
  }
}