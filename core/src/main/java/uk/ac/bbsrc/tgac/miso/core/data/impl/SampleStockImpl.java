package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Stock")
public class SampleStockImpl extends DetailedSampleImpl implements SampleStock {

  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private Boolean dnaseTreated;

  @ManyToOne(targetEntity = SampleSlideImpl.class)
  @JoinColumn(name = "referenceSlideId")
  private SampleSlide referenceSlide;

  @Override
  public StrStatus getStrStatus() {
    return strStatus;
  }

  @Override
  public void setStrStatus(StrStatus strStatus) {
    this.strStatus = strStatus;
  }

  @Override
  public void setStrStatus(String strStatus) {
    this.strStatus = StrStatus.get(strStatus);
  }

  @Override
  public Boolean getDNAseTreated() {
    return dnaseTreated;
  }

  @Override
  public void setDNAseTreated(Boolean dnaseTreated) {
    this.dnaseTreated = dnaseTreated;
  }

  @Override
  public SampleSlide getReferenceSlide() {
    return referenceSlide;
  }

  @Override
  public void setReferenceSlide(SampleSlide referenceSlide) {
    this.referenceSlide = referenceSlide;
  }

  @Override
  public String toString() {
    return "SampleStockImpl [strStatus=" + strStatus + ", dnaseTreated=" + dnaseTreated + "]";
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitSampleStock(this);
  }

}
