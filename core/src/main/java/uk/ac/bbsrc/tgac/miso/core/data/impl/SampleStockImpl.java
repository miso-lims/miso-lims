package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.type.StrStatus;

@Entity
@Table(name = "SampleStock")
@Inheritance(strategy = InheritanceType.JOINED)
public class SampleStockImpl extends DetailedSampleImpl implements SampleStock {

  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private StrStatus strStatus = StrStatus.NOT_SUBMITTED;

  private Boolean dnaseTreated;

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
  public String toString() {
    return "SampleStockImpl [strStatus=" + strStatus + ", dnaseTreated=" + dnaseTreated + "]";
  }

}
