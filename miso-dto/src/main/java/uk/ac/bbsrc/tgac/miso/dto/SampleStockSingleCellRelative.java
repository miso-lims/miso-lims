package uk.ac.bbsrc.tgac.miso.dto;

public interface SampleStockSingleCellRelative extends SampleSingleCellRelative {

  public Integer getTargetCellRecovery();

  public void setTargetCellRecovery(Integer targetCellRecovery);

  public String getCellViability();

  public void setCellViability(String cellViability);

  public String getLoadingCellConcentration();

  public void setLoadingCellConcentration(String loadingCellConcentration);

}
