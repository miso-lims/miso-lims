package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

@Entity
@Table(name = "SampleStock")
public class SampleStockImpl extends SampleAdditionalInfoImpl implements SampleStock {

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private Double concentration;

  @Override
  public Double getConcentration() {
    return concentration;
  }

  @Override
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }

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
  public String toString() {
    return "SampleStockImpl [strStatus=" + strStatus + ", concentration=" + concentration + "]";
  }

}
