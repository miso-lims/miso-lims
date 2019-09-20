package uk.ac.bbsrc.tgac.miso.core.data;

import java.math.BigDecimal;

public interface SampleAliquotSingleCell extends SampleAliquot {

  public static final String SUBCATEGORY_NAME = "Single Cell (aliquot)";

  public BigDecimal getInputIntoLibrary();

  public void setInputIntoLibrary(BigDecimal inputIntoLibrary);

}
