package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultRunSample;

public class MisoRunSample extends DefaultRunSample {

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((runId == null) ? 0 : runId.hashCode());
    result = prime * result + ((partitionId == null) ? 0 : partitionId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MisoRunSample other = (MisoRunSample) obj;
    if (runId == null) {
      if (other.runId != null) return false;
    } else if (!runId.equals(other.runId)) return false;
    if (partitionId == null) {
      if (other.partitionId != null) return false;
    } else if (!partitionId.equals(other.partitionId)) return false;
    return true;
  }

}
