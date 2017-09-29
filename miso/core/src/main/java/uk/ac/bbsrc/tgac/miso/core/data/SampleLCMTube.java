package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleLCMTube extends SampleTissueProcessing {

  public static final String SAMPLE_CLASS_NAME = "LCM Tube";

  Integer getSlidesConsumed();

  void setSlidesConsumed(Integer slidesConsumed);

}
