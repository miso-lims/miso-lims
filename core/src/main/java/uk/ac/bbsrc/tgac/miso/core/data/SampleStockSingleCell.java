package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface SampleStockSingleCell extends SampleStock {

  public static final String SAMPLE_CLASS_NAME = "Single Cell DNA (stock)";

  public BigDecimal getTargetCellRecovery();

  public void setTargetCellRecovery(BigDecimal targetCellRecovery);

  public BigDecimal getCellViability();

  public void setCellViability(BigDecimal cellViability);

  public BigDecimal getLoadingCellConcentration();

  public void setLoadingCellConcentration(BigDecimal loadingCellConcentration);

}
