package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Aliquot")
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

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitSampleAliquot(this);
  }
}
