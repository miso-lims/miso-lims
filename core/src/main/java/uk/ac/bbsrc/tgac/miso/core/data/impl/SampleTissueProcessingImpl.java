package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("TissueProcessing")
public class SampleTissueProcessingImpl extends DetailedSampleImpl implements SampleTissueProcessing {

  private static final long serialVersionUID = 1L;

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitSampleTissueProcessing(this);
  }

}
