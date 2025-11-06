package ca.on.oicr.pinery.lims.miso;

import java.util.HashSet;

import ca.on.oicr.pinery.lims.DefaultRunPosition;

public class MisoRunPosition extends DefaultRunPosition {

  private Integer runId;
  private String barcode;
  private Integer containerId;
  private Integer partitionId;
  private String instrumentPosition;
  private String containerModel;
  private String sequencingParameters;

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public Integer getRunId() {
    return runId;
  }

  public void setRunId(Integer runId) {
    this.runId = runId;
  }

  public Integer getContainerId() {
    return containerId;
  }

  public void setContainerId(Integer containerId) {
    this.containerId = containerId;
  }

  public Integer getPartitionId() {
    return partitionId;
  }

  public void setPartitionId(Integer partitionId) {
    this.partitionId = partitionId;
  }

  public String getInstrumentPosition() {
    return instrumentPosition;
  }

  public void setInstrumentPosition(String instrumentPosition) {
    this.instrumentPosition = instrumentPosition;
  }

  public String getContainerModel() {
    return containerModel;
  }

  public void setContainerModel(String containerModel) {
    this.containerModel = containerModel;
  }

  public String getSequencingParameters() {
    return sequencingParameters;
  }

  public void setSequencingParameters(String sequencingParameters) {
    this.sequencingParameters = sequencingParameters;
  }

  public MisoRunContainer makeRunContainer() {
    MisoRunContainer container = new MisoRunContainer();
    container.setContainerId(containerId);
    container.setBarcode(barcode);
    container.setContainerModel(containerModel);
    container.setInstrumentPosition(instrumentPosition);
    container.setSequencingParameters(sequencingParameters);
    container.setPositions(new HashSet<>());
    return container;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((runId == null) ? 0 : runId.hashCode());
    result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
    result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
    result = prime * result + ((partitionId == null) ? 0 : partitionId.hashCode());
    result = prime * result + ((instrumentPosition == null) ? 0 : instrumentPosition.hashCode());
    result = prime * result + ((containerModel == null) ? 0 : containerModel.hashCode());
    result = prime * result + ((sequencingParameters == null) ? 0 : sequencingParameters.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    MisoRunPosition other = (MisoRunPosition) obj;
    if (runId == null) {
      if (other.runId != null)
        return false;
    } else if (!runId.equals(other.runId))
      return false;
    if (containerId == null) {
      if (other.containerId != null)
        return false;
    } else if (!containerId.equals(other.containerId))
      return false;
    if (barcode == null) {
      if (other.barcode != null)
        return false;
    } else if (!barcode.equals(other.barcode))
      return false;
    if (partitionId == null) {
      if (other.partitionId != null)
        return false;
    } else if (!partitionId.equals(other.partitionId))
      return false;
    if (instrumentPosition == null) {
      if (other.instrumentPosition != null)
        return false;
    } else if (!instrumentPosition.equals(other.instrumentPosition))
      return false;
    if (containerModel == null) {
      if (other.containerModel != null)
        return false;
    } else if (!containerModel.equals(other.containerModel))
      return false;
    if (sequencingParameters == null) {
      if (other.sequencingParameters != null)
        return false;
    } else if (!sequencingParameters.equals(other.sequencingParameters))
      return false;
    return true;
  }

}
