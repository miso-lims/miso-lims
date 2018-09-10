package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;

@Entity
@Table(name = "SampleAliquotSingleCell")
public class SampleAliquotSingleCellImpl extends SampleAliquotImpl implements SampleAliquotSingleCell {

  private static final long serialVersionUID = 1L;

  private BigDecimal inputIntoLibrary;

  @Override
  public BigDecimal getInputIntoLibrary() {
    return inputIntoLibrary;
  }

  @Override
  public void setInputIntoLibrary(BigDecimal inputIntoLibrary) {
    this.inputIntoLibrary = inputIntoLibrary;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((inputIntoLibrary == null) ? 0 : inputIntoLibrary.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SampleAliquotSingleCellImpl other = (SampleAliquotSingleCellImpl) obj;
    if (inputIntoLibrary == null) {
      if (other.inputIntoLibrary != null) return false;
    } else if (!inputIntoLibrary.equals(other.inputIntoLibrary)) return false;
    return true;
  }

}
