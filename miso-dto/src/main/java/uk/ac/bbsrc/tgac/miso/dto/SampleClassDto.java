package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleClassDto {

  private Long id;
  private String alias;
  private String sampleCategory;
  private String sampleSubcategory;
  private String suffix;
  private boolean archived;
  private boolean directCreationAllowed;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  private Boolean dnaseTreatable;

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

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
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

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public boolean isDirectCreationAllowed() {
    return directCreationAllowed;
  }

  public void setDirectCreationAllowed(boolean directCreationAllowed) {
    this.directCreationAllowed = directCreationAllowed;
  }

  public Boolean getDNAseTreatable() {
    return dnaseTreatable;
  }

  public void setDNAseTreatable(Boolean dnaseTreatable) {
    this.dnaseTreatable = dnaseTreatable;
  }

  public String getSampleSubcategory() {
    return sampleSubcategory;
  }

  public void setSampleSubcategory(String sampleSubcategory) {
    this.sampleSubcategory = sampleSubcategory;
  }

  @Override
  public String toString() {
    return "SampleClassDto [id=" + id + ", alias=" + alias + ", sampleCategory=" + sampleCategory + ", sampleSubcategory="
        + sampleSubcategory + ", suffix=" + suffix
        + ", createdById=" + createdById + ", creationDate=" + creationDate + ", updatedById=" + updatedById + ", lastUpdated="
        + lastUpdated + ", dnaseTreatable=" + dnaseTreatable + "]";
  }

}
