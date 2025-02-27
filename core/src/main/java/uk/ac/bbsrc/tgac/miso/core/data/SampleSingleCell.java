package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface SampleSingleCell extends SampleTissueProcessing {

  public static final String SUBCATEGORY_NAME = "Single Cell";

  public BigDecimal getInitialCellConcentration();

  public void setInitialCellConcentration(BigDecimal initialCellConcentration);

  public String getDigestion();

  public void setDigestion(String digestion);

}
