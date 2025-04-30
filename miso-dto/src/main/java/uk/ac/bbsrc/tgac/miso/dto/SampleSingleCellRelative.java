package uk.ac.bbsrc.tgac.miso.dto;

public interface SampleSingleCellRelative {

  public String getInitialCellConcentration();

  public void setInitialCellConcentration(String initialCellConcentration);

  public Integer getTargetCellRecovery();

  public void setTargetCellRecovery(Integer targetCellRecovery);

  public String getLoadingCellConcentration();

  public void setLoadingCellConcentration(String loadingCellConcentration);

  public String getDigestion();

  public void setDigestion(String digestion);

}
