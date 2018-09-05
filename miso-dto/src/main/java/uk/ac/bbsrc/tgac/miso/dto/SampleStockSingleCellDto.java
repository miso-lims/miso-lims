package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleStockSingleCell.SAMPLE_CLASS_NAME)
public class SampleStockSingleCellDto extends SampleStockDto implements SampleStockSingleCellRelative {

  private Long tissueProcessingClassId;
  private String initialCellConcentration;
  private String digestion;

  private String targetCellRecovery;
  private String cellViability;
  private String loadingCellConcentration;

  @Override
  public Long getTissueProcessingClassId() {
    return tissueProcessingClassId;
  }

  @Override
  public void setTissueProcessingClassId(Long tissueProcessingClassId) {
    this.tissueProcessingClassId = tissueProcessingClassId;
  }

  @Override
  public String getInitialCellConcentration() {
    return initialCellConcentration;
  }

  @Override
  public void setInitialCellConcentration(String initialCellConcentration) {
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
  public String getTargetCellRecovery() {
    return targetCellRecovery;
  }

  @Override
  public void setTargetCellRecovery(String targetCellRecovery) {
    this.targetCellRecovery = targetCellRecovery;
  }

  @Override
  public String getCellViability() {
    return cellViability;
  }

  @Override
  public void setCellViability(String cellViability) {
    this.cellViability = cellViability;
  }

  @Override
  public String getLoadingCellConcentration() {
    return loadingCellConcentration;
  }

  @Override
  public void setLoadingCellConcentration(String loadingCellConcentration) {
    this.loadingCellConcentration = loadingCellConcentration;
  }

}
