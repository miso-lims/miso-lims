package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public interface SampleSingleCellRelative {

  String getInitialCellConcentration();

  void setInitialCellConcentration(String initialCellConcentration);

  Integer getTargetCellRecovery();

  void setTargetCellRecovery(Integer targetCellRecovery);

  String getLoadingCellConcentration();

  void setLoadingCellConcentration(String loadingCellConcentration);

  String getDigestion();

  void setDigestion(String digestion);

  List<ProbeDto> getProbes();

  void setProbes(List<ProbeDto> probes);

}
