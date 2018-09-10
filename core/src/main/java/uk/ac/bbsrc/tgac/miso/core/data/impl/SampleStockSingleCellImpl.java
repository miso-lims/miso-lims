package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;

@Entity
@Table(name = "SampleStockSingleCell")
public class SampleStockSingleCellImpl extends SampleStockImpl implements SampleStockSingleCell {

  private static final long serialVersionUID = 1L;

  private BigDecimal targetCellRecovery;
  private BigDecimal cellViability;
  private BigDecimal loadingCellConcentration;

  @Override
  public BigDecimal getTargetCellRecovery() {
    return targetCellRecovery;
  }

  @Override
  public void setTargetCellRecovery(BigDecimal targetCellRecovery) {
    this.targetCellRecovery = targetCellRecovery;
  }

  @Override
  public BigDecimal getCellViability() {
    return cellViability;
  }

  @Override
  public void setCellViability(BigDecimal cellViability) {
    this.cellViability = cellViability;
  }

  @Override
  public BigDecimal getLoadingCellConcentration() {
    return loadingCellConcentration;
  }

  @Override
  public void setLoadingCellConcentration(BigDecimal loadingCellConcentration) {
    this.loadingCellConcentration = loadingCellConcentration;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((cellViability == null) ? 0 : cellViability.hashCode());
    result = prime * result + ((loadingCellConcentration == null) ? 0 : loadingCellConcentration.hashCode());
    result = prime * result + ((targetCellRecovery == null) ? 0 : targetCellRecovery.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    SampleStockSingleCellImpl other = (SampleStockSingleCellImpl) obj;
    if (cellViability == null) {
      if (other.cellViability != null) return false;
    } else if (!cellViability.equals(other.cellViability)) return false;
    if (loadingCellConcentration == null) {
      if (other.loadingCellConcentration != null) return false;
    } else if (!loadingCellConcentration.equals(other.loadingCellConcentration)) return false;
    if (targetCellRecovery == null) {
      if (other.targetCellRecovery != null) return false;
    } else if (!targetCellRecovery.equals(other.targetCellRecovery)) return false;
    return true;
  }

}
