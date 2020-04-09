package uk.ac.bbsrc.tgac.miso.dto;

public class ListContainerViewDto {

  private Long id;
  private String identificationBarcode;
  private String platform;
  private Long lastRunId;
  private String lastRunName;
  private String lastRunAlias;
  private Long lastSequencerId;
  private String lastSequencerName;
  private String lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public Long getLastRunId() {
    return lastRunId;
  }

  public void setLastRunId(Long lastRunId) {
    this.lastRunId = lastRunId;
  }

  public String getLastRunName() {
    return lastRunName;
  }

  public void setLastRunName(String lastRunName) {
    this.lastRunName = lastRunName;
  }

  public String getLastRunAlias() {
    return lastRunAlias;
  }

  public void setLastRunAlias(String lastRunAlias) {
    this.lastRunAlias = lastRunAlias;
  }

  public Long getLastSequencerId() {
    return lastSequencerId;
  }

  public void setLastSequencerId(Long lastSequencerId) {
    this.lastSequencerId = lastSequencerId;
  }

  public String getLastSequencerName() {
    return lastSequencerName;
  }

  public void setLastSequencerName(String lastSequencerName) {
    this.lastSequencerName = lastSequencerName;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
