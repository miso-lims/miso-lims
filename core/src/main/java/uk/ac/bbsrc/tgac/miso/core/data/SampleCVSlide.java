package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleCVSlide extends SampleTissueProcessing {

  public static final String SAMPLE_CLASS_NAME = "CV Slide";

  Integer getCuts();

  void setCuts(Integer cuts);

  Integer getCutsRemaining();

  Integer getDiscards();

  void setDiscards(Integer discards);

  Integer getThickness();

  void setThickness(Integer thickness);

}
