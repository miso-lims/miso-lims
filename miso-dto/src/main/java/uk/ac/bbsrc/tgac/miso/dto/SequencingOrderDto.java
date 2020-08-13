package uk.ac.bbsrc.tgac.miso.dto;

public class SequencingOrderDto {

  private Long id;
  private SequencingParametersDto parameters;
  private Integer partitions;
  private PoolDto pool;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  private String description;
  private Long purposeId;
  private String purposeAlias;
  private Long containerModelId;


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

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getDescription(){
    return description;
  }

  public void setDescription(String description){
    this.description = description;
  }

  public Long getPurposeId() {
    return purposeId;
  }

  public void setPurposeId(Long purposeId) {
    this.purposeId = purposeId;
  }

  public String getPurposeAlias() {
    return purposeAlias;
  }

  public void setPurposeAlias(String purposeAlias) {
    this.purposeAlias = purposeAlias;
  }

  public Long getContainerModelId() {
    return containerModelId;
  }

  public void setContainerModelId(Long containerModelId) {
    this.containerModelId = containerModelId;
  }

}
