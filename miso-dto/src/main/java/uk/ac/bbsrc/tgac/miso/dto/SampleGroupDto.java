package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleGroupDto {

  private Long id;
  private String url;
  private Long projectId;
  private String projectUrl;
  private Integer groupId;
  private String description;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;

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

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getProjectUrl() {
    return projectUrl;
  }

  public void setProjectUrl(String projectUrl) {
    this.projectUrl = projectUrl;
  }

  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }

  @Override
  public String toString() {
    return "SampleGroupDto [id=" + id + ", url=" + url  +", projectId=" + projectId + ", projectUrl=" + projectUrl + ", groupId=" + groupId
        + ", description=" + description + ", createdById=" + createdById + ", createdByUrl=" + createdByUrl + ", creationDate=" 
        + creationDate + ", updatedById=" + updatedById + ", updatedByUrl=" + updatedByUrl + ", lastUpdated=" + lastUpdated + "]";
  }
}
