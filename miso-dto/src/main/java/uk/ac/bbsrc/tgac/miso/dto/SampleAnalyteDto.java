package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleAnalyteDto {

  private Long sampleId;
  private String url;
  private String sampleUrl;
  private Long samplePurposeId;
  private String samplePurposeUrl;
  private Long sampleGroupId;
  private String sampleGroupUrl;
  private Long tissueMaterialId;
  private String tissueMaterialUrl;

  private String region;
  private String tubeId;
  private String strStatus;

  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
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

  public String getSampleUrl() {
    return sampleUrl;
  }

  public void setSampleUrl(String sampleUrl) {
    this.sampleUrl = sampleUrl;
  }

  public Long getSamplePurposeId() {
    return samplePurposeId;
  }

  public void setSamplePurposeId(Long samplePurposeId) {
    this.samplePurposeId = samplePurposeId;
  }

  public String getSamplePurposeUrl() {
    return samplePurposeUrl;
  }

  public void setSamplePurposeUrl(String samplePurposeUrl) {
    this.samplePurposeUrl = samplePurposeUrl;
  }

  public Long getSampleGroupId() {
    return sampleGroupId;
  }

  public void setSampleGroupId(Long sampleGroupId) {
    this.sampleGroupId = sampleGroupId;
  }

  public String getSampleGroupUrl() {
    return sampleGroupUrl;
  }

  public void setSampleGroupUrl(String sampleGroupUrl) {
    this.sampleGroupUrl = sampleGroupUrl;
  }

  public String getTissueMaterialUrl() {
    return tissueMaterialUrl;
  }

  public void setTissueMaterialUrl(String tissueMaterialUrl) {
    this.tissueMaterialUrl = tissueMaterialUrl;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getTubeId() {
    return tubeId;
  }

  public void setTubeId(String tubeId) {
    this.tubeId = tubeId;
  }

  public Long getTissueMaterialId() {
    return tissueMaterialId;
  }

  public void setTissueMaterialId(Long tissueMaterialId) {
    this.tissueMaterialId = tissueMaterialId;
  }

  public String getStrStatus() {
    return strStatus;
  }

  public void setStrStatus(String strStatus) {
    this.strStatus = strStatus;
  }

}
