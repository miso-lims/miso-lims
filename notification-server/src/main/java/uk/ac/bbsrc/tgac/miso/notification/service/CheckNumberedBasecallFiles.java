package uk.ac.bbsrc.tgac.miso.notification.service;

import java.io.File;

import uk.ac.bbsrc.tgac.miso.notification.util.PossiblyGzippedFileUtils;

final class CheckNumberedBasecallFiles extends RunSink<String> {
  @Override
  public void process(String input, IlluminaRunMessage output) throws Exception {
    if (output.getNumReads() == null || output.getNumReads() < 1) return;
    File rootDir = new File(input);
    for (int i = 1; i <= output.getNumReads(); i++) {
      if (!PossiblyGzippedFileUtils.checkExists(rootDir, "/Basecalling_Netcopy_complete_Read" + (i) + ".txt")
          && !PossiblyGzippedFileUtils.checkExists(rootDir, "/Basecalling_Netcopy_complete_READ" + (i) + ".txt")) {
        IlluminaTransformer.log.debug(output.getRunName() + " :: No Basecalling_Netcopy_complete_Read" + (i)
            + ".txt / Basecalling_Netcopy_complete_READ" + (i) + ".txt!");
        output.setNumberedBaseCallsComplete(false);
        return;
      }
    }
    output.setNumberedBaseCallsComplete(true);
  }
}