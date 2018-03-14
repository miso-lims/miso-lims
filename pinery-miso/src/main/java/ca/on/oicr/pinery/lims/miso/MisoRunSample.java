package ca.on.oicr.pinery.lims.miso;

import ca.on.oicr.pinery.lims.DefaultRunSample;

public class MisoRunSample extends DefaultRunSample {

  private Integer partitionId;

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
    result = prime * result + ((partitionId == null) ? 0 : partitionId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MisoRunSample other = (MisoRunSample) obj;
    if (partitionId == null) {
      if (other.partitionId != null) return false;
    } else if (!partitionId.equals(other.partitionId)) return false;
    return true;
  }

}
