package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.BarcodableVisitor;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Stock")
public class SampleStockImpl extends DetailedSampleImpl implements SampleStock {

  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private Integer slidesConsumed;

  private Long referenceSlideId;

  @ManyToOne
  @JoinColumn(name = "indexId")
  private SampleIndex index;

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
  public Integer getSlidesConsumed() {
    return slidesConsumed;
  }

  @Override
  public void setSlidesConsumed(Integer slidesConsumed) {
    this.slidesConsumed = slidesConsumed;
  }

  @Override
  public Long getReferenceSlideId() {
    return referenceSlideId;
  }

  @Override
  public void setReferenceSlideId(Long referenceSlideId) {
    this.referenceSlideId = referenceSlideId;
  }

  @Override
  public SampleIndex getIndex() {
    return index;
  }

  @Override
  public void setIndex(SampleIndex index) {
    this.index = index;
  }

  @Override
  public String toString() {
    return "SampleStockImpl [strStatus=" + strStatus + "]";
  }

  @Override
  public <T> T visit(BarcodableVisitor<T> visitor) {
    return visitor.visitSampleStock(this);
  }

}
