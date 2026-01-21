package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleProbe;

import java.math.BigDecimal;
import java.util.Set;

public interface SampleSingleCell extends SampleTissueProcessing {

  public static final String SUBCATEGORY_NAME = "Single Cell";

  BigDecimal getInitialCellConcentration();

  void setInitialCellConcentration(BigDecimal initialCellConcentration);

  Integer getTargetCellRecovery();

  void setTargetCellRecovery(Integer targetCellRecovery);

  BigDecimal getLoadingCellConcentration();

  void setLoadingCellConcentration(BigDecimal loadingCellConcentration);

  String getDigestion();

  void setDigestion(String digestion);

  Set<SampleProbe> getProbes();

  void setProbes(Set<SampleProbe> probes);

}
