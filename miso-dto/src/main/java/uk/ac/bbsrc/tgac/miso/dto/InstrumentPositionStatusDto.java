package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.dto.run.RunDto;

public class InstrumentPositionStatusDto {

  private String position;
  private RunDto run;
  private List<PoolDto> pools;
  private boolean outOfService;
  private String outOfServiceTime;

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public RunDto getRun() {
    return run;
  }

  public void setRun(RunDto run) {
    this.run = run;
  }

  public List<PoolDto> getPools() {
    return pools;
  }

  public void setPools(List<PoolDto> pools) {
    this.pools = pools;
  }

  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

  public String getOutOfServiceTime() {
    return outOfServiceTime;
  }

  public void setOutOfServiceTime(String outOfServiceTime) {
    this.outOfServiceTime = outOfServiceTime;
  }

}
