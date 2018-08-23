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

}
