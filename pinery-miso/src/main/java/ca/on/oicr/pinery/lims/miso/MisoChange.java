package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultChange;

public class MisoChange extends DefaultChange {

  private String sampleId;

  public String getSampleId() {
    return sampleId;
  }

  public void setSampleId(String sampleId) {
    this.sampleId = sampleId;
  }

}
