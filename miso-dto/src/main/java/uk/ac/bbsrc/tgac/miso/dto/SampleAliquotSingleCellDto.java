package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;

@JsonTypeName(value = SampleAliquotSingleCell.SUBCATEGORY_NAME)
public class SampleAliquotSingleCellDto extends SampleAliquotDto implements SampleStockSingleCellRelative {

  private Long tissueProcessingClassId;
  private String initialCellConcentration;
  private String digestion;

  private Integer targetCellRecovery;
  private String cellViability;
  private String loadingCellConcentration;

  private String inputIntoLibrary;

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
  public Integer getTargetCellRecovery() {
    return targetCellRecovery;
  }

  @Override
  public void setTargetCellRecovery(Integer targetCellRecovery) {
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

  public String getInputIntoLibrary() {
    return inputIntoLibrary;
  }

  public void setInputIntoLibrary(String inputIntoLibrary) {
    this.inputIntoLibrary = inputIntoLibrary;
  }

}
