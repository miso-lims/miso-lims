package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;

@Entity
@Table(name = "SampleSlide")
public class SampleSlideImpl extends SampleTissueProcessingImpl implements SampleSlide {

  private static final long serialVersionUID = 1L;

  private Integer initialSlides;

  private Integer slides;

  private Integer discards;

  private Integer thickness;

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
  public Integer getDiscards() {
    return discards;
  }

  @Override
  public void setDiscards(Integer discards) {
    this.discards = discards;
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

}
