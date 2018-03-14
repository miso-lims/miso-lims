package ca.on.oicr.pinery.lims.miso;

public class PlainMisoClientIT extends AbstractMisoClientIT {

  @Override
  protected String getAdditionalDataFilename() {
    return null;
  }

  @Override
  protected boolean hasArchivedSamples() {
    return false;
  }

}
