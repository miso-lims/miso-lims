package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

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
