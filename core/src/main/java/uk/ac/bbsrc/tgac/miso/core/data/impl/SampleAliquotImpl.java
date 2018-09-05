package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

@Entity
@Table(name = "SampleAliquot")
@Inheritance(strategy = InheritanceType.JOINED)
public class SampleAliquotImpl extends DetailedSampleImpl implements SampleAliquot {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = SamplePurposeImpl.class)
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
