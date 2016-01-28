package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleTissueDto {

  private String bioBankId;

  private Integer cellularity;

  private Long createdById;

  private String createdByUrl;

  private String creationDate;

  private Long id;

  private InstituteDto institute;

  private String instituteTissueName;

  private String lastUpdated;

  private Long updatedById;

  private String updatedByUrl;

  private String url;

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

  public Long getId() {
    return id;
  }

  public InstituteDto getInstitute() {
    return institute;
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

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitute(InstituteDto institute) {
    this.institute = institute;
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

  @Override
  public String toString() {
    return "SampleTissueDto [id=" + id + ", cellularity=" + cellularity + ", institute=" + institute + ", instituteTissueName="
        + instituteTissueName + ", createdById=" + createdById + ", creationDate=" + creationDate + ", lastUpdatedById=" + updatedById
        + ", lastUpdated=" + lastUpdated + "]";
  }
}
