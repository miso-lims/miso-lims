package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleLCMTube extends SampleTissueProcessing {

  public static final String SAMPLE_CLASS_NAME = "LCM Tube";

  Integer getCutsConsumed();

  void setCutsConsumed(Integer cutsConsumed);

}
