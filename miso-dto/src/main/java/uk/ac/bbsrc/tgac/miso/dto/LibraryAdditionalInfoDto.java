package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LibraryAdditionalInfoDto {

  private Long id;
  private String url;
  private Long libraryId;
  private String libraryUrl;
  private TissueOriginDto tissueOrigin;
  private TissueTypeDto tissueType;
  private KitDescriptorDto prepKit;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private Boolean archived;
  private Long libraryDesignId;

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

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public String getLibraryUrl() {
    return libraryUrl;
  }

  public void setLibraryUrl(String libraryUrl) {
    this.libraryUrl = libraryUrl;
  }

  public TissueOriginDto getTissueOrigin() {
    return tissueOrigin;
  }

  public void setTissueOrigin(TissueOriginDto tissueOrigin) {
    this.tissueOrigin = tissueOrigin;
  }

  public TissueTypeDto getTissueType() {
    return tissueType;
  }

  public void setTissueType(TissueTypeDto tissueType) {
    this.tissueType = tissueType;
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

  public KitDescriptorDto getPrepKit() {
    return prepKit;
  }

  public void setPrepKit(KitDescriptorDto prepKit) {
    this.prepKit = prepKit;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Long getLibraryDesignId() {
    return libraryDesignId;
  }

  public void setLibraryDesignId(Long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  @Override
  public String toString() {
    return "LibraryAdditionalInfoDto [id=" + id + ", url=" + url + ", libraryId=" + libraryId + ", libraryUrl=" + libraryUrl
        + ", tissueOrigin=" + tissueOrigin + ", tissueType=" + tissueType + ", prepKit=" + prepKit + ", createdById=" + createdById
        + ", createdByUrl=" + createdByUrl + ", creationDate=" + creationDate + ", updatedById=" + updatedById + ", updatedByUrl="
        + updatedByUrl + ", lastUpdated=" + lastUpdated + ", archived=" + archived + ", libraryDesignId=" + libraryDesignId + "]";
  }

}
