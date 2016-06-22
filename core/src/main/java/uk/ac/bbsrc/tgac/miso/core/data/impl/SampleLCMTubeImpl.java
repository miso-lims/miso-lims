package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleLCMTube;

@Entity
@Table(name = "SampleLCMTube")
public class SampleLCMTubeImpl extends SampleTissueProcessingImpl implements SampleLCMTube {

  private static final long serialVersionUID = 1L;
  
  private Integer cutsConsumed;

  @Override
  public Integer getCutsConsumed() {
    return cutsConsumed;
  }

  @Override
  public void setCutsConsumed(Integer cutsConsumed) {
    this.cutsConsumed = cutsConsumed;
  }

}
