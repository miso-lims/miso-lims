package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleClassDto {

  private Long id;
  private String alias;
  private String sampleCategory;
  private String sampleSubcategory;
  private String suffix;
  private String v2NamingCode;
  private boolean archived;
  private boolean directCreationAllowed;
  private Long createdById;
  private String creationDate;
  private Long updatedById;
  private String lastUpdated;
  private boolean dnaseTreatable;
  private String defaultSampleType;
  private List<SampleValidRelationshipDto> parentRelationships;
  private List<SampleValidRelationshipDto> childRelationships;

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

  public String getV2NamingCode() {
    return v2NamingCode;
  }

  public void setV2NamingCode(String v2NamingCode) {
    this.v2NamingCode = v2NamingCode;
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

  public boolean getDNAseTreatable() {
    return dnaseTreatable;
  }

  public void setDNAseTreatable(boolean dnaseTreatable) {
    this.dnaseTreatable = dnaseTreatable;
  }

  public String getDefaultSampleType() {
    return defaultSampleType;
  }

  public void setDefaultSampleType(String defaultSampleType) {
    this.defaultSampleType = defaultSampleType;
  }

  public String getSampleSubcategory() {
    return sampleSubcategory;
  }

  public void setSampleSubcategory(String sampleSubcategory) {
    this.sampleSubcategory = sampleSubcategory;
  }

  public List<SampleValidRelationshipDto> getParentRelationships() {
    return parentRelationships;
  }

  public void setParentRelationships(List<SampleValidRelationshipDto> parentRelationships) {
    this.parentRelationships = parentRelationships;
  }

  public List<SampleValidRelationshipDto> getChildRelationships() {
    return childRelationships;
  }

  public void setChildRelationships(List<SampleValidRelationshipDto> childRelationships) {
    this.childRelationships = childRelationships;
  }

  @Override
  public String toString() {
    return "SampleClassDto [id=" + id + ", alias=" + alias + ", sampleCategory=" + sampleCategory + ", sampleSubcategory="
        + sampleSubcategory + ", suffix=" + suffix
        + ", createdById=" + createdById + ", creationDate=" + creationDate + ", updatedById=" + updatedById + ", lastUpdated="
        + lastUpdated + ", dnaseTreatable=" + dnaseTreatable + "]";
  }

}
