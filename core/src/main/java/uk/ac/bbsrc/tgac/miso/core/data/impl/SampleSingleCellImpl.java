package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;

@Entity
@DiscriminatorValue("SingleCell")
public class SampleSingleCellImpl extends SampleTissueProcessingImpl implements SampleSingleCell {

  private static final long serialVersionUID = 1L;

  @Column
  private BigDecimal initialCellConcentration;
  private String digestion;

  @Override
  public BigDecimal getInitialCellConcentration() {
    return initialCellConcentration;
  }

  @Override
  public void setInitialCellConcentration(BigDecimal initialCellConcentration) {
    this.initialCellConcentration = initialCellConcentration;
  }

  @Override
  public String getDigestion() {
    return digestion;
  }

  @Override
  public void setDigestion(String digestion) {
    this.digestion = digestion;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((digestion == null) ? 0 : digestion.hashCode());
    result = prime * result + ((initialCellConcentration == null) ? 0 : initialCellConcentration.hashCode());
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
    SampleSingleCellImpl other = (SampleSingleCellImpl) obj;
    if (digestion == null) {
      if (other.digestion != null)
        return false;
    } else if (!digestion.equals(other.digestion))
      return false;
    if (initialCellConcentration == null) {
      if (other.initialCellConcentration != null)
        return false;
    } else if (!initialCellConcentration.equals(other.initialCellConcentration))
      return false;
    return true;
  }

}
