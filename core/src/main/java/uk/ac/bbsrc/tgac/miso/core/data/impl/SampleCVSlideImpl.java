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

  private Integer cuts;

  private Integer discards;

  private Integer thickness;

  @Override
  public Integer getCuts() {
    return cuts;
  }

  @Override
  public void setCuts(Integer cuts) {
    this.cuts = cuts;
  }

  @Override
  public Integer getCutsRemaining() {
    int cutsConsumed = 0;
    for (Sample child : getChildren()) {
      if (child != null && child instanceof SampleLCMTube && ((SampleLCMTube) child).getCutsConsumed() != null) {
        cutsConsumed += ((SampleLCMTube) child).getCutsConsumed();
      }
    }
    return (getCuts() - getDiscards() - cutsConsumed);
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
