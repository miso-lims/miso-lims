package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;

@Entity
@DiscriminatorValue("Slide")
public class SampleSlideImpl extends SampleTissueProcessingImpl implements SampleSlide {

  private static final long serialVersionUID = 1L;

  private Integer initialSlides;
  private Integer slides;
  private Integer thickness;
  private BigDecimal percentTumour;
  private BigDecimal percentNecrosis;
  private BigDecimal markedAreaSize;
  private BigDecimal markedAreaPercentTumour;

  @ManyToOne
  @JoinColumn(name = "stain", nullable = true)
  private Stain stain;

  @Override
  public Integer getInitialSlides() {
    return initialSlides;
  }

  @Override
  public void setInitialSlides(Integer initialSlides) {
    this.initialSlides = initialSlides;
  }

  @Override
  public Integer getSlides() {
    return slides;
  }

  @Override
  public void setSlides(Integer slides) {
    this.slides = slides;
  }

  @Override
  public Integer getThickness() {
    return thickness;
  }

  @Override
  public void setThickness(Integer thickness) {
    this.thickness = thickness;
  }

  @Override
  public Stain getStain() {
    return stain;
  }

  @Override
  public void setStain(Stain stain) {
    this.stain = stain;
  }

  @Override
  public BigDecimal getPercentTumour() {
    return percentTumour;
  }

  @Override
  public void setPercentTumour(BigDecimal percentTumour) {
    this.percentTumour = percentTumour;
  }

  @Override
  public BigDecimal getPercentNecrosis() {
    return percentNecrosis;
  }

  @Override
  public void setPercentNecrosis(BigDecimal percentNecrosis) {
    this.percentNecrosis = percentNecrosis;
  }

  @Override
  public BigDecimal getMarkedArea() {
    return markedAreaSize;
  }

  @Override
  public void setMarkedArea(BigDecimal markedArea) {
    this.markedAreaSize = markedArea;
  }

  @Override
  public BigDecimal getMarkedAreaPercentTumour() {
    return markedAreaPercentTumour;
  }

  @Override
  public void setMarkedAreaPercentTumour(BigDecimal markedAreaPercentTumour) {
    this.markedAreaPercentTumour = markedAreaPercentTumour;
  }

}
