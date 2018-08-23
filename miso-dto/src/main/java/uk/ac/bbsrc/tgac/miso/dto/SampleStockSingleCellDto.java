package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleStockSingleCell.SAMPLE_CLASS_NAME)
public class SampleStockSingleCellDto extends SampleStockDto {

  private String targetCellRecovery;
  private String cellViability;
  private String loadingCellConcentration;

  public String getTargetCellRecovery() {
    return targetCellRecovery;
  }

  public void setTargetCellRecovery(String targetCellRecovery) {
    this.targetCellRecovery = targetCellRecovery;
  }

  public String getCellViability() {
    return cellViability;
  }

  public void setCellViability(String cellViability) {
    this.cellViability = cellViability;
  }

  public String getLoadingCellConcentration() {
    return loadingCellConcentration;
  }

  public void setLoadingCellConcentration(String loadingCellConcentration) {
    this.loadingCellConcentration = loadingCellConcentration;
  }

}
