package uk.ac.bbsrc.tgac.miso.dto;

public class RunPartitionAliquotDto {

  // This ID is only for the sake of DataTables selection and doesn't relate to anything in the DB
  public Long id;
  public Long runId;
  public String platformType;
  public Long containerId;
  public String containerIdentificationBarcode;
  public Long partitionId;
  public Integer partitionNumber;
  public Long aliquotId;
  public String aliquotName;
  public String aliquotAlias;
  public Long runPurposeId;
  public Boolean qcPassed;
  public String qcNote;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRunId() {
    return runId;
  }

  public void setRunId(Long runId) {
    this.runId = runId;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public Long getContainerId() {
    return containerId;
  }

  public void setContainerId(Long containerId) {
    this.containerId = containerId;
  }

  public String getContainerIdentificationBarcode() {
    return containerIdentificationBarcode;
  }

  public void setContainerIdentificationBarcode(String containerIdentificationBarcode) {
    this.containerIdentificationBarcode = containerIdentificationBarcode;
  }

  public Long getPartitionId() {
    return partitionId;
  }

  public void setPartitionId(Long partitionId) {
    this.partitionId = partitionId;
  }

  public Integer getPartitionNumber() {
    return partitionNumber;
  }

  public void setPartitionNumber(Integer partitionNumber) {
    this.partitionNumber = partitionNumber;
  }

  public Long getAliquotId() {
    return aliquotId;
  }

  public void setAliquotId(Long aliquotId) {
    this.aliquotId = aliquotId;
  }

  public String getAliquotName() {
    return aliquotName;
  }

  public void setAliquotName(String aliquotName) {
    this.aliquotName = aliquotName;
  }

  public String getAliquotAlias() {
    return aliquotAlias;
  }

  public void setAliquotAlias(String aliquotAlias) {
    this.aliquotAlias = aliquotAlias;
  }

  public Long getRunPurposeId() {
    return runPurposeId;
  }

  public void setRunPurposeId(Long runPurposeId) {
    this.runPurposeId = runPurposeId;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

}
