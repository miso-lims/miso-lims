package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface SampleSingleCell extends SampleTissueProcessing {

  public static final String SUBCATEGORY_NAME = "Single Cell";

  public BigDecimal getInitialCellConcentration();

  public void setInitialCellConcentration(BigDecimal initialCellConcentration);

  public Integer getTargetCellRecovery();

  public void setTargetCellRecovery(Integer targetCellRecovery);

  public BigDecimal getLoadingCellConcentration();

  public void setLoadingCellConcentration(BigDecimal loadingCellConcentration);

  public String getDigestion();

  public void setDigestion(String digestion);

}
