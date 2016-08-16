package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleCVSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;

@Entity
@Table(name = "SampleCVSlide")
public class SampleCVSlideImpl extends SampleTissueProcessingImpl implements SampleCVSlide {

  private static final long serialVersionUID = 1L;

  private Integer slides;

  private Integer discards;

  private Integer thickness;

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
    int slidesConsumed = 0;
    for (Sample child : getChildren()) {
      if (child != null && child instanceof SampleLCMTube && ((SampleLCMTube) child).getSlidesConsumed() != null) {
        slidesConsumed += ((SampleLCMTube) child).getSlidesConsumed();
      }
    }
    return (getSlides() - getDiscards() - slidesConsumed);
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

}
