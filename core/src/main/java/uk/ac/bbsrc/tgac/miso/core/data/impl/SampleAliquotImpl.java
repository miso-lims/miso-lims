package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

@Entity
@Table(name = "SampleAliquot")
public class SampleAliquotImpl extends DetailedSampleImpl implements SampleAliquot {

  private static final long serialVersionUID = 1L;

  @OneToOne(targetEntity = SamplePurposeImpl.class)
  @JoinColumn(name = "samplePurposeId")
  private SamplePurpose samplePurpose;

  @Override
  public SamplePurpose getSamplePurpose() {
    return samplePurpose;
  }

  @Override
  public void setSamplePurpose(SamplePurpose samplePurpose) {
    this.samplePurpose = samplePurpose;
  }

  @Override
  public String toString() {
    return "SampleAliquotImpl [samplePurpose=" + samplePurpose + "]";
  }

}
