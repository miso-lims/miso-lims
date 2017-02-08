package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubprojectDto {

  private Long id;
  private String url;
  private String alias;
  private String description;
  private Long parentProjectId;
  private String parentProjectUrl;
  private Boolean priority;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private Long referenceGenomeId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  public String getParentProjectUrl() {
    return parentProjectUrl;
  }

  public void setParentProjectUrl(String parentProjectUrl) {
    this.parentProjectUrl = parentProjectUrl;
  }

  public Boolean getPriority() {
    return priority;
  }

  public void setPriority(Boolean priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "SubprojectDto [id=" + id + ", url=" + url + ", alias=" + alias + ", description=" + description + ", parentProjectId="
        + parentProjectId + ", parentProjectUrl=" + parentProjectUrl + ", priority=" + priority + ", createdById=" + createdById
        + ", createdByUrl=" + createdByUrl + ", creationDate=" + creationDate + ", updatedById=" + updatedById + ", updatedByUrl="
        + updatedByUrl + ", lastUpdated=" + lastUpdated + "]";
  }

  public Long getReferenceGenomeId() {
    return referenceGenomeId;
  }

  public void setReferenceGenomeId(Long referenceGenomeId) {
    this.referenceGenomeId = referenceGenomeId;
  }
}
