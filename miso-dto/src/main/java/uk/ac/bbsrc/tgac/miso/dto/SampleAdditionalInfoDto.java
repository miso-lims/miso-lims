package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleAdditionalInfoDto {

  private Long sampleId;
  private String url;
  private String sampleUrl;
  private Long parentId;
  private String parentUrl;
  private String parentAlias;
  private Long parentSampleClassId;
  private Long sampleClassId;
  private String sampleClassUrl;
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
  private Double concentration;
  private Long createdById;
  private String createdByUrl;
  private String creationDate;
  private Long updatedById;
  private String updatedByUrl;
  private String lastUpdated;
  private String externalInstituteIdentifier;
  private Long labId;
  private String labUrl;

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long id) {
    this.sampleId = id;
  }

  public String getUrl() {
    return url;
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
  
  public String getParentAlias() {
    return parentAlias;
  }
  
  public void setParentAlias(String parentAlias) {
    this.parentAlias = parentAlias;
  }
  
  public Long getParentSampleClassId() {
    return parentSampleClassId;
  }
  
  public void setParentSampleClassId(Long parentSampleClassId) {
    this.parentSampleClassId = parentSampleClassId;
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

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  public String getSampleClassUrl() {
    return sampleClassUrl;
  }

  public void setSampleClassUrl(String sampleClassUrl) {
    this.sampleClassUrl = sampleClassUrl;
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
  
  public String getExternalInstituteIdentifier() {
    return externalInstituteIdentifier;
  }
  
  public void setExternalInstituteIdentifier(String externalInstituteIdentifier) {
    this.externalInstituteIdentifier = externalInstituteIdentifier;
  }

  public Long getLabId() {
    return labId;
  }

  public void setLabId(Long labId) {
    this.labId = labId;
  }

  public String getLabUrl() {
    return labUrl;
  }

  public void setLabUrl(String labUrl) {
    this.labUrl = labUrl;
  }

  @Override
  public String toString() {
    return "SampleAdditionalInfoDto [sampleId=" + sampleId + ", url=" + url + ", sampleUrl=" + sampleUrl + ", parentId=" + parentId 
        + ", parentUrl=" + parentUrl + ", parentAlias=" + parentAlias + ", parentSampleClassId=" + parentSampleClassId + ", sampleClassId="
        + sampleClassId + ", sampleClassUrl=" + sampleClassUrl + ", tissueOriginId=" + tissueOriginId + ", tissueOriginUrl=" 
        + tissueOriginUrl + ", tissueTypeId=" + tissueTypeId + ", tissueTypeUrl=" + tissueTypeUrl + ", qcPassedDetailId=" 
        + qcPassedDetailId + ", qcPassedDetailUrl=" + qcPassedDetailUrl + ", subprojectId=" + subprojectId + ", subprojectUrl="
        + subprojectUrl + ", prepKitId=" + prepKitId + ", prepKitUrl=" + prepKitUrl + ", passageNumber=" + passageNumber 
        + ", timesReceived=" + timesReceived + ", tubeNumber=" + tubeNumber + ", concentration=" + concentration + ", createdById="
        + createdById + ", createdByUrl=" + createdByUrl + ", creationDate=" + creationDate + ", updatedById=" + updatedById
        + ", updatedByUrl=" + updatedByUrl + ", lastUpdated=" + lastUpdated + ", externalInstituteIdentifier=" + externalInstituteIdentifier
        + ", labId=" + labId + ", labUrl=" + labUrl + "]";
  }
}
