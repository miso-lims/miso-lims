package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleTissueDto {

  private String bioBankId;
  private Integer cellularity;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long sampleId;
  private String instituteTissueName;
  private String lastUpdated;
  private Long updatedById;
  private String updatedByUrl;
  private String url;
  private String sampleUrl;

  public String getBioBankId() {
    return bioBankId;
  }

  public Integer getCellularity() {
    return cellularity;
  }

  public Long getCreatedById() {
    return createdById;
  }

  public String getCreatedByUrl() {
    return createdByUrl;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public Long getSampleId() {
    return sampleId;
  }

  public String getInstituteTissueName() {
    return instituteTissueName;
  }

  public String getLastUpdated() {
    return lastUpdated;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public String getUpdatedByUrl() {
    return updatedByUrl;
  }

  public String getUrl() {
    return url;
  }

  public void setBioBankId(String bioBankId) {
    this.bioBankId = bioBankId;
  }

  public void setCellularity(Integer cellularity) {
    this.cellularity = cellularity;
  }

  public void setCreatedById(Long createdById) {
    this.createdById = createdById;
  }

  public void setCreatedByUrl(String createdByUrl) {
    this.createdByUrl = createdByUrl;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public void setInstituteTissueName(String instituteTissueName) {
    this.instituteTissueName = instituteTissueName;
  }

  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public void setUpdatedByUrl(String updatedByUrl) {
    this.updatedByUrl = updatedByUrl;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getSampleUrl() {
    return sampleUrl;
  }

  public void setSampleUrl(String sampleUrl) {
    this.sampleUrl = sampleUrl;
  }

  @Override
  public String toString() {
    return "SampleTissueDto [bioBankId=" + bioBankId + ", cellularity="
        + cellularity + ", createdById=" + createdById + ", createdByUrl="
        + createdByUrl + ", creationDate=" + creationDate + ", sampleId="
        + sampleId + ", instituteTissueName=" + instituteTissueName
        + ", lastUpdated=" + lastUpdated + ", updatedById=" + updatedById
        + ", updatedByUrl=" + updatedByUrl + ", url=" + url + ", sampleUrl="
        + sampleUrl + "]";
  }

}
