package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@Entity
public class SampleProbe extends Probe {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = SampleTissueProcessingImpl.class)
  @JoinColumn(name = "sampleId")
  private SampleTissueProcessing sample;

  public SampleTissueProcessing getSample() {
    return sample;
  }

  public void setSample(SampleTissueProcessing sample) {
    this.sample = sample;
  }
}
