package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class InstrumentStatusDto {
  private InstrumentDto instrument;
  private List<PoolDto> pools;
  private RunDto run;
  private boolean outOfService;

  public InstrumentDto getInstrument() {
    return instrument;
  }

  public List<PoolDto> getPools() {
    return pools;
  }

  public RunDto getRun() {
    return run;
  }

  public void setInstrument(InstrumentDto instrument) {
    this.instrument = instrument;
  }

  public void setPools(List<PoolDto> pools) {
    this.pools = pools;
  }

  public void setRun(RunDto run) {
    this.run = run;
  }

  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

}
