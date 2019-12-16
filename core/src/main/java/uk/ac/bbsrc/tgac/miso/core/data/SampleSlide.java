package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

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

  public BigDecimal getPercentTumour();

  public void setPercentTumour(BigDecimal percentTumour);

  public BigDecimal getPercentNecrosis();

  public void setPercentNecrosis(BigDecimal percentNecrosis);

  public BigDecimal getMarkedArea();

  public void setMarkedArea(BigDecimal markedArea);

  public BigDecimal getMarkedAreaPercentTumour();

  public void setMarkedAreaPercentTumour(BigDecimal markedAreaPercentTumour);

}
