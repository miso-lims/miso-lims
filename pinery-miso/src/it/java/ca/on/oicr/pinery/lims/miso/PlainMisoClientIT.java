package ca.on.oicr.pinery.lims.miso;

public class PlainMisoClientIT extends AbstractMisoClientIT {

  @Override
  protected String getAdditionalDataFilename() {
    return "pinery-miso_plain_test_data.sql";
  }

  @Override
  protected boolean hasArchivedSamples() {
    return false;
  }

}
