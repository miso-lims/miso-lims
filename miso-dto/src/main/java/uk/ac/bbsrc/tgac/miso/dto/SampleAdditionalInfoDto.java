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
  private Long qcPassedDetailId;
  private String qcPassedDetailUrl;
  private Long subprojectId;
  private String subprojectUrl;
  private Long prepKitId;
  private String prepKitUrl;
  private String groupId;
  private String groupDescription;
  private Boolean isSynthetic;
  private boolean nonStandardAlias;

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

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getGroupDescription() {
    return groupDescription;
  }

  public void setGroupDescription(String groupDescription) {
    this.groupDescription = groupDescription;
  }

  public Boolean getSynthetic() {
    return isSynthetic;
  }

  public void setSynthetic(Boolean isSynthetic) {
    this.isSynthetic = isSynthetic;
  }

  public boolean getNonStandardAlias() {
    return nonStandardAlias;
  }

  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/sample/{id}").buildAndExpand(getId()).toUriString());
    if (getSampleClassId() != null) {
      setSampleClassUrl(
          UriComponentsBuilder.fromUri(baseUri).path("/rest/sampleclass/{id}").buildAndExpand(getSampleClassId()).toUriString());
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

}
