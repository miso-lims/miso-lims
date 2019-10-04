package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleSlide extends SampleTissueProcessing {

  public static final String SUBCATEGORY_NAME = "Slide";

  public Integer getInitialSlides();

  public void setInitialSlides(Integer initialSlides);

  public Integer getSlides();

  public void setSlides(Integer slides);

  public Integer getDiscards();

  public void setDiscards(Integer discards);

  public Integer getThickness();

  public void setThickness(Integer thickness);

  public Stain getStain();

  public void setStain(Stain stain);

}
