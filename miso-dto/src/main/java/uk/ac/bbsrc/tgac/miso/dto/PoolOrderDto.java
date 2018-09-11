package uk.ac.bbsrc.tgac.miso.dto;

public class PoolOrderDto {
  private Long id;
  private SequencingParametersDto parameters;
  private Integer partitions;
  private PoolDto pool;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private String url;
  private String description;


  public Long getId() {
    return id;
  }

  public SequencingParametersDto getParameters() {
    return parameters;
  }

  public Integer getPartitions() {
    return partitions;
  }

  public PoolDto getPool() {
    return pool;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setParameters(SequencingParametersDto parameters) {
    this.parameters = parameters;
  }

  public void setPartitions(Integer partitions) {
    this.partitions = partitions;
  }

  public void setPool(PoolDto pool) {
    this.pool = pool;
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

  public String getDescription(){
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

}
