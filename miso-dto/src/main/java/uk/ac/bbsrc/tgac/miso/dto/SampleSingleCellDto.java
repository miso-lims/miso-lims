package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;

@JsonTypeName(value = SampleSingleCell.SUBCATEGORY_NAME)
public class SampleSingleCellDto extends SampleTissueProcessingDto implements SampleSingleCellRelative {

  private String initialCellConcentration;
  private Integer targetCellRecovery;
  private String loadingCellConcentration;
  private String digestion;

  @Override
  public String getInitialCellConcentration() {
    return initialCellConcentration;
  }

  @Override
  public void setInitialCellConcentration(String initialCellConcentration) {
    this.initialCellConcentration = initialCellConcentration;
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
  public String getLoadingCellConcentration() {
    return loadingCellConcentration;
  }

  @Override
  public void setLoadingCellConcentration(String loadingCellConcentration) {
    this.loadingCellConcentration = loadingCellConcentration;
  }

  @Override
  public String getDigestion() {
    return digestion;
  }

  @Override
  public void setDigestion(String digestion) {
    this.digestion = digestion;
  }

}
