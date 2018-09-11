package uk.ac.bbsrc.tgac.miso.dto;

public interface SampleStockSingleCellRelative extends SampleSingleCellRelative {

  public String getTargetCellRecovery();

  public void setTargetCellRecovery(String targetCellRecovery);

  public String getCellViability();

  public void setCellViability(String cellViability);

  public String getLoadingCellConcentration();

  public void setLoadingCellConcentration(String loadingCellConcentration);

}
