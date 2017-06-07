package uk.ac.bbsrc.tgac.miso.core.data.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.deproxify;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;

@Entity
@Table(name = "SampleSlide")
public class SampleSlideImpl extends SampleTissueProcessingImpl implements SampleSlide {

  private static final long serialVersionUID = 1L;

  private Integer slides;

  private Integer discards;

  private Integer thickness;

  @ManyToOne
  @JoinColumn(name = "stain", nullable = true)
  private Stain stain;

  @Override
  public Integer getSlides() {
    return slides;
  }

  @Override
  public void setSlides(Integer slides) {
    this.slides = slides;
  }

  @Override
  public Integer getSlidesRemaining() {
    if (getSlides() == null) {
      return null;
    }
    int slidesConsumed = 0;
    for (Sample child : getChildren()) {
      if (child == null) continue;
      child = deproxify(child);
      if (child instanceof SampleLCMTube) {
        Integer consumed = ((SampleLCMTube) child).getSlidesConsumed();
        if (consumed != null) slidesConsumed += consumed;
      } else if (child instanceof SampleSlideImpl) {
        Integer consumed = ((SampleSlideImpl) child).getSlides();
        if (consumed != null) slidesConsumed += consumed;
      }
    }
    int discards = 0;
    if (getDiscards() != null) {
      discards = getDiscards();
    }
    return (getSlides() - discards - slidesConsumed);
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
