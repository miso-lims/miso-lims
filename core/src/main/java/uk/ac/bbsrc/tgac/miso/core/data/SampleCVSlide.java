package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleCVSlide extends SampleTissueProcessing {

  public static final String SAMPLE_CLASS_NAME = "CV Slide";

  Integer getSlides();

  void setSlides(Integer slides);

  Integer getSlidesRemaining();

  Integer getDiscards();

  void setDiscards(Integer discards);

  Integer getThickness();

  void setThickness(Integer thickness);

}
