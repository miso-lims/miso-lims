package uk.ac.bbsrc.tgac.miso.dto;

public class RunPartitionAliquotDto {

  // This ID is only for the sake of DataTables selection and doesn't relate to anything in the DB
  private Long id;
  private Long runId;
  private String runAlias;
  private String platformType;
  private Long containerId;
  private String containerIdentificationBarcode;
  private Long partitionId;
  private Integer partitionNumber;
  private Long aliquotId;
  private String aliquotName;
  private String aliquotAlias;
  private String tissueOriginAlias;
  private String tissueOriginDescription;
  private String tissueTypeAlias;
  private String tissueTypeDescription;
  private String libraryDesignCode;
  private Long runPurposeId;
  private Long qcStatusId;
  private String qcNote;
  private String qcUserName;
  private String qcDate;
  private Boolean dataReview;
  private String dataReviewer;
  private String dataReviewDate;

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

  public String getRunAlias() {
    return runAlias;
  }

  public void setRunAlias(String runAlias) {
    this.runAlias = runAlias;
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

  public String getTissueOriginAlias() {
    return tissueOriginAlias;
  }

  public void setTissueOriginAlias(String tissueOriginAlias) {
    this.tissueOriginAlias = tissueOriginAlias;
  }

  public String getTissueOriginDescription() {
    return tissueOriginDescription;
  }

  public void setTissueOriginDescription(String tissueOriginDescription) {
    this.tissueOriginDescription = tissueOriginDescription;
  }

  public String getTissueTypeAlias() {
    return tissueTypeAlias;
  }

  public void setTissueTypeAlias(String tissueTypeAlias) {
    this.tissueTypeAlias = tissueTypeAlias;
  }

  public String getTissueTypeDescription() {
    return tissueTypeDescription;
  }

  public void setTissueTypeDescription(String tissueTypeDescription) {
    this.tissueTypeDescription = tissueTypeDescription;
  }

  public String getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(String libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

  public Long getRunPurposeId() {
    return runPurposeId;
  }

  public void setRunPurposeId(Long runPurposeId) {
    this.runPurposeId = runPurposeId;
  }

  public Long getQcStatusId() {
    return qcStatusId;
  }

  public void setQcStatusId(Long qcStatusId) {
    this.qcStatusId = qcStatusId;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

  public String getQcUserName() {
    return qcUserName;
  }

  public void setQcUserName(String qcUserName) {
    this.qcUserName = qcUserName;
  }

  public String getQcDate() {
    return qcDate;
  }

  public void setQcDate(String qcDate) {
    this.qcDate = qcDate;
  }

  public Boolean getDataReview() {
    return dataReview;
  }

  public void setDataReview(Boolean dataReview) {
    this.dataReview = dataReview;
  }

  public String getDataReviewer() {
    return dataReviewer;
  }

  public void setDataReviewer(String dataReviewer) {
    this.dataReviewer = dataReviewer;
  }

  public String getDataReviewDate() {
    return dataReviewDate;
  }

  public void setDataReviewDate(String dataReviewDate) {
    this.dataReviewDate = dataReviewDate;
  }

}
