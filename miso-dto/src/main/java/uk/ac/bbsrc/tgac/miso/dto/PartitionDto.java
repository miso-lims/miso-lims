package uk.ac.bbsrc.tgac.miso.dto;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;

public class PartitionDto {
  private Long containerId;
  private String containerName;
  private String runPositionAlias;
  private Long id;
  private String loadingConcentration;
  private ConcentrationUnit loadingConcentrationUnits;
  private int partitionNumber;
  private PoolDto pool;
  private String qcNotes;
  private long qcType;
  private Long runPurposeId;

  public Long getContainerId() {
    return containerId;
  }

  public String getContainerName() {
    return containerName;
  }

  public String getRunPositionAlias() {
    return runPositionAlias;
  }

  public void setRunPositionAlias(String runPositionAlias) {
    this.runPositionAlias = runPositionAlias;
  }

  public Long getId() {
    return id;
  }

  public String getLoadingConcentration() {
    return loadingConcentration;
  }

  public ConcentrationUnit getLoadingConcentrationUnits() {
    return loadingConcentrationUnits;
  }

  public int getPartitionNumber() {
    return partitionNumber;
  }

  public PoolDto getPool() {
    return pool;
  }

  public String getQcNotes() {
    return qcNotes;
  }

  public long getQcType() {
    return qcType;
  }

  public void setContainerId(Long containerId) {
    this.containerId = containerId;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLoadingConcentration(String loadingConcentration) {
    this.loadingConcentration = loadingConcentration;
  }

  public void setLoadingConcentrationUnits(ConcentrationUnit loadingConcentrationUnits) {
    this.loadingConcentrationUnits = loadingConcentrationUnits;
  }

  public void setPartitionNumber(int partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  public void setPool(PoolDto pool) {
    this.pool = pool;
  }

  public void setQcNotes(String qcNotes) {
    this.qcNotes = qcNotes;
  }

  public void setQcType(long qcType) {
    this.qcType = qcType;
  }

  public Long getRunPurposeId() {
    return runPurposeId;
  }

  public void setRunPurposeId(Long runPurposeId) {
    this.runPurposeId = runPurposeId;
  }
}
