package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;

@Entity
@DiscriminatorValue("StockRna")
public class SampleStockRnaImpl extends SampleStockImpl implements SampleStockRna {

  private static final long serialVersionUID = 1L;

  private Boolean dnaseTreated;

  @Override
  public Boolean getDnaseTreated() {
    return dnaseTreated;
  }

  @Override
  public void setDnaseTreated(Boolean dnaseTreated) {
    this.dnaseTreated = dnaseTreated;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((dnaseTreated == null) ? 0 : dnaseTreated.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    SampleStockRnaImpl other = (SampleStockRnaImpl) obj;
    if (dnaseTreated == null) {
      if (other.dnaseTreated != null)
        return false;
    } else if (!dnaseTreated.equals(other.dnaseTreated))
      return false;
    return true;
  }

}
