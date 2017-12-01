package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;

@Entity
@Table(name = "SampleLCMTube")
public class SampleLCMTubeImpl extends SampleTissueProcessingImpl implements SampleLCMTube {

  private static final long serialVersionUID = 1L;

  private Integer slidesConsumed;

  @Override
  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  @Override
  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

}
