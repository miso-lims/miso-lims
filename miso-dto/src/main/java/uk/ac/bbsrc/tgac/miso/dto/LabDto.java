package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabDto {
  
  private Long id;
  private Long instituteId;
  private String instituteAlias;
  private Boolean instituteArchived;
  private String alias;
  private String label;
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
  
  public Long getInstituteId() {
    return instituteId;
  }
  
  public void setInstituteId(Long instituteId) {
    this.instituteId = instituteId;
  }
  
  public String getInstituteAlias() {
    return instituteAlias;
  }

  public void setInstituteAlias(String instituteAlias) {
    this.instituteAlias = instituteAlias;
  }

  public Boolean getInstituteArchived() {
    return instituteArchived;
  }

  public void setInstituteArchived(Boolean instituteArchived) {
    this.instituteArchived = instituteArchived;
  }

  public String getAlias() {
    return alias;
  }
  
  public void setAlias(String alias) {
    this.alias = alias;
  }
  
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
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
    return "LabDto [id=" + id + ", instituteId=" + instituteId + ", alias=" + alias + ", createdById=" + createdById + ", creationDate="
        + creationDate + ", updatedById=" + updatedById + ", lastUpdated=" + lastUpdated + "]";
  }

}
