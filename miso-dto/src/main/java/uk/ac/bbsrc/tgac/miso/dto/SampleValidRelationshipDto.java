package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SampleValidRelationshipDto implements WritableUrls {

  private Long id;
  private String url;
  private Long parentId;
  private String parentUrl;
  private Long childId;
  private String childUrl;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private Boolean archived;

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

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getParentUrl() {
    return parentUrl;
  }

  public void setParentUrl(String parentUrl) {
    this.parentUrl = parentUrl;
  }

  public Long getChildId() {
    return childId;
  }

  public void setChildId(Long childId) {
    this.childId = childId;
  }

  public String getChildUrl() {
    return childUrl;
  }

  public void setChildUrl(String childUrl) {
    this.childUrl = childUrl;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/samplevalidrelationship/{id}", getId()));
    setCreatedByUrl(WritableUrls.buildUriPath(baseUri, "/rest/user/{id}", getCreatedById()));
    setUpdatedByUrl(WritableUrls.buildUriPath(baseUri, "/rest/user/{id}", getUpdatedById()));
    setParentUrl(WritableUrls.buildUriPath(baseUri, "/rest/sampleclass/{id}", getParentId()));
    setChildUrl(WritableUrls.buildUriPath(baseUri, "/rest/sampleclass/{id}", getChildId()));
  }

}
