package uk.ac.bbsrc.tgac.miso.dto;

public class PoolOrderDto {
  private Long id;
  private SequencingParametersDto parameterId;
  private Integer partitions;
  private Long poolId;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private String url;

  public Long getId() {
    return id;
  }

  public SequencingParametersDto getParameters() {
    return parameterId;
  }

  public Integer getPartitions() {
    return partitions;
  }

  public Long getPoolId() {
    return poolId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setParameters(SequencingParametersDto parameterId) {
    this.parameterId = parameterId;
  }

  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  public void setPoolId(Long poolId) {
    this.poolId = poolId;
  }

  public Long getCreatedById() {
    return createdById;
  }

  public void setCreatedById(Long createdById) {
    this.createdById = createdById;
  }

  public String getCreatedByUrl() {
    return createdByUrl;
  }

  public void setCreatedByUrl(String createdByUrl) {
    this.createdByUrl = createdByUrl;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public String getUpdatedByUrl() {
    return updatedByUrl;
  }

  public void setUpdatedByUrl(String updatedByUrl) {
    this.updatedByUrl = updatedByUrl;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
