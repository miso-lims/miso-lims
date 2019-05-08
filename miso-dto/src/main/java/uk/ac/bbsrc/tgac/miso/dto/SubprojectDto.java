package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubprojectDto {

  private Long id;
  private String alias;
  private String description;
  private Long parentProjectId;
  private Boolean priority;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  private Long referenceGenomeId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Long getCreatedById() {
    return createdById;
  }

  public void setCreatedById(Long createdById) {
    this.createdById = createdById;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public Long getParentProjectId() {
    return parentProjectId;
  }

  public void setParentProjectId(Long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public Boolean getPriority() {
    return priority;
  }

  public void setPriority(Boolean priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "SubprojectDto [id=" + id + ", alias=" + alias + ", description=" + description + ", parentProjectId=" + parentProjectId
        + ", priority=" + priority + ", createdById=" + createdById + ", creationDate=" + creationDate + ", updatedById=" + updatedById
        + ", lastUpdated=" + lastUpdated + "]";
  }

  public Long getReferenceGenomeId() {
    return referenceGenomeId;
  }

  public void setReferenceGenomeId(Long referenceGenomeId) {
    this.referenceGenomeId = referenceGenomeId;
  }

}
