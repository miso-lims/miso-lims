package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstituteDto {
  
  private Long id;
  private String alias;
  private Boolean archived;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  
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
  
  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
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

  @Override
  public String toString() {
    return "InstituteDto [id=" + id + ", alias=" + alias + ", createdById=" + createdById + ", creationDate=" + creationDate
        + ", updatedById=" + updatedById + ", lastUpdated=" + lastUpdated + "]";
  }
  
}
