package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SampleAdditionalInfoDto extends SampleDto {

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
  private String externalInstituteIdentifier;
  private Long labId;
  private String labUrl;
  private Long groupId;
  private String groupDescription;

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

  public Long getGroupId() {
    return groupId;
  }

  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  public String getGroupDescription() {
    return groupDescription;
  }

  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
  }

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    if (getSampleClassId() != null) {
      setSampleClassUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleclass/{id}").buildAndExpand(getSampleClassId()).toUriString());
    }
    if (getTissueOriginId() != null) {
      setTissueOriginUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/tissueorigin/{id}").buildAndExpand(getTissueOriginId()).toUriString());
    }
    if (getTissueTypeId() != null) {
      setTissueTypeUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/tissuetype/{id}").buildAndExpand(getTissueTypeId()).toUriString());
    }
    if (getQcPassedDetailId() != null) {
      setQcPassedDetailUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/qcpasseddetail/{id}").buildAndExpand(getQcPassedDetailId()).toUriString());
    }
    if (getSubprojectId() != null) {
      setSubprojectUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/subproject/{id}").buildAndExpand(getSubprojectId()).toUriString());
    }
    if (getPrepKitId() != null) {
      setPrepKitUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/kitdescriptor/{id}").buildAndExpand(getPrepKitId()).toUriString());
    }
    if (getParentId() != null) {
      setParentUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/{id}").buildAndExpand(getParentId()).toUriString());
    }
  }

  @Override
  public String toString() {
    return "SampleAdditionalInfoDto [parentId=" + parentId + ", parentUrl=" + parentUrl + ", parentAlias=" + parentAlias
        + ", parentSampleClassId=" + parentSampleClassId + ", sampleClassId=" + sampleClassId + ", sampleClassUrl=" + sampleClassUrl
        + ", tissueOriginId=" + tissueOriginId + ", tissueOriginUrl=" + tissueOriginUrl + ", tissueTypeId=" + tissueTypeId
        + ", tissueTypeUrl=" + tissueTypeUrl + ", qcPassedDetailId=" + qcPassedDetailId + ", qcPassedDetailUrl=" + qcPassedDetailUrl
        + ", subprojectId=" + subprojectId + ", subprojectUrl=" + subprojectUrl + ", prepKitId=" + prepKitId + ", prepKitUrl=" + prepKitUrl
        + ", passageNumber=" + passageNumber + ", timesReceived=" + timesReceived + ", tubeNumber=" + tubeNumber + ", concentration="
        + concentration + ", externalInstituteIdentifier=" + externalInstituteIdentifier + ", labId=" + labId + ", labUrl=" + labUrl
        + ", groupId=" + groupId + ", groupDescription=" + groupDescription + "]";
  }

}
