package uk.ac.bbsrc.tgac.miso.dto;

public class DetailedQcStatusDto {

  private Long id;
  private Boolean status;
  private String description;
  private boolean noteRequired;
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

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public boolean isNoteRequired() {
    return noteRequired;
  }

  public void setNoteRequired(boolean noteRequired) {
    this.noteRequired = noteRequired;
  }

}
