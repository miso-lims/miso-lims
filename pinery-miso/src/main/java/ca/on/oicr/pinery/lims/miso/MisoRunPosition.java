package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultRunPosition;

public class MisoRunPosition extends DefaultRunPosition {

  private Integer runId;
  private Integer partitionId;

  public Integer getRunId() {
    return runId;
  }

  public void setRunId(Integer runId) {
    this.runId = runId;
  }

  public Integer getPartitionId() {
    return partitionId;
  }

  public void setPartitionId(Integer partitionId) {
    this.partitionId = partitionId;
  }

}
