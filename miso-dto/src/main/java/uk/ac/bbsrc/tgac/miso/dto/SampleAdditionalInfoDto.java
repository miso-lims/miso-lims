package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleAdditionalInfoDto {

  private Long id;
  private String url;
  private Long sampleId;
  private String sampleUrl;
  private Long tissueOriginId;
  private String tissueOriginUrl;
  private Long tissueTypeId;
  private String tissueTypeUrl;
  private Long qcPassedDetailId;
  private String qcPassedDetailUrl;
  private Long subprojectId;
  private String subprojectUrl;
  private Long prepKitId;
  private String prepKitUrl;
  private Integer passageNumber;
  private Integer timesReceived;
  private Integer tubeNumber;
  private Double volume;
  private Double concentration;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;

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

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public String getSampleUrl() {
    return sampleUrl;
  }

  public void setSampleUrl(String sampleUrl) {
    this.sampleUrl = sampleUrl;
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public String getTissueOriginUrl() {
    return tissueOriginUrl;
  }

  public void setTissueOriginUrl(String tissueOriginUrl) {
    this.tissueOriginUrl = tissueOriginUrl;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public String getTissueTypeUrl() {
    return tissueTypeUrl;
  }

  public void setTissueTypeUrl(String tissueTypeUrl) {
    this.tissueTypeUrl = tissueTypeUrl;
  }

  public Long getQcPassedDetailId() {
    return qcPassedDetailId;
  }

  public void setQcPassedDetailId(Long qcPassedDetailId) {
    this.qcPassedDetailId = qcPassedDetailId;
  }

  public String getQcPassedDetailUrl() {
    return qcPassedDetailUrl;
  }

  public void setQcPassedDetailUrl(String qcPassedDetailUrl) {
    this.qcPassedDetailUrl = qcPassedDetailUrl;
  }

  public Long getSubprojectId() {
    return subprojectId;
  }

  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  public String getSubprojectUrl() {
    return subprojectUrl;
  }

  public void setSubprojectUrl(String subprojectUrl) {
    this.subprojectUrl = subprojectUrl;
  }

  public Long getPrepKitId() {
    return prepKitId;
  }

  public void setPrepKitId(Long prepKitId) {
    this.prepKitId = prepKitId;
  }

  public String getPrepKitUrl() {
    return prepKitUrl;
  }

  public void setPrepKitUrl(String prepKitUrl) {
    this.prepKitUrl = prepKitUrl;
  }

  public Integer getPassageNumber() {
    return passageNumber;
  }

  public void setPassageNumber(Integer passageNumber) {
    this.passageNumber = passageNumber;
  }

  public Integer getTimesReceived() {
    return timesReceived;
  }

  public void setTimesReceived(Integer timesReceived) {
    this.timesReceived = timesReceived;
  }

  public Integer getTubeNumber() {
    return tubeNumber;
  }

  public void setTubeNumber(Integer tubeNumber) {
    this.tubeNumber = tubeNumber;
  }

  public Double getVolume() {
    return volume;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public Double getConcentration() {
    return concentration;
  }

  public void setConcentration(Double concentration) {
    this.concentration = concentration;
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

}
