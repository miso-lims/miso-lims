package ca.on.oicr.pinery.lims.miso;

public class DetailedMisoClientIT extends AbstractMisoClientIT {

  @Override
  protected String getAdditionalDataFilename() {
    return "pinery-miso_detailed_test_data.sql";
  }

  @Override
  protected boolean hasArchivedSamples() {
    return true;
  }

}
