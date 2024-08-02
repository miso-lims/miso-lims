package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface SampleStockSingleCell extends SampleStock {

  public static final String SUBCATEGORY_NAME = "Single Cell (stock)";

  public Integer getTargetCellRecovery();

  public void setTargetCellRecovery(Integer targetCellRecovery);

  public BigDecimal getCellViability();

  public void setCellViability(BigDecimal cellViability);

  public BigDecimal getLoadingCellConcentration();

  public void setLoadingCellConcentration(BigDecimal loadingCellConcentration);

}
