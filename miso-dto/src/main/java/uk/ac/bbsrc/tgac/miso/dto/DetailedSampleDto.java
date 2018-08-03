package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailedSampleDto extends SampleDto {

  private Long parentId;
  private String parentUrl;
  private String parentAlias;
  private Long parentTissueSampleClassId;
  private Long sampleClassId;
  private String sampleClassUrl;
  private Long detailedQcStatusId;
  private String detailedQcStatusUrl;
  private String detailedQcStatusNote;
  private Long subprojectId;
  private String subprojectUrl;
  private String prepKitUrl;
  private String groupId;
  private String groupDescription;
  private Boolean isSynthetic;
  private boolean nonStandardAlias;
  private Long identityId;
  private String identityConsentLevel;
  private String externalNames;
  private String effectiveGroupId;
  private String effectiveGroupIdSample;
  private String creationDate;

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

  public Long getParentTissueSampleClassId() {
    return parentTissueSampleClassId;
  }

  public void setParentTissueSampleClassId(Long parentSampleClassId) {
    this.parentTissueSampleClassId = parentSampleClassId;
  }

  public Long getDetailedQcStatusId() {
    return detailedQcStatusId;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setDetailedQcStatusId(Long detailedQcStatusId) {
    this.detailedQcStatusId = detailedQcStatusId;
  }

  public String getDetailedQcStatusUrl() {
    return detailedQcStatusUrl;
  }

  public void setDetailedQcStatusUrl(String detailedQcStatusUrl) {
    this.detailedQcStatusUrl = detailedQcStatusUrl;
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

  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  public void setDetailedQcStatusNote(String detailedQcStatusNote) {
    this.detailedQcStatusNote = detailedQcStatusNote;
  }

  public Long getIdentityId() {
    return identityId;
  }

  public void setIdentityId(Long identityId) {
    this.identityId = identityId;
  }

  @Override
  public void writeUrls(URI baseUri) {
    super.writeUrls(baseUri);
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/sample/{id}", getId()));
    if (getSampleClassId() != null) {
      setSampleClassUrl(WritableUrls.buildUriPath(baseUri, "/rest/sampleclass/{id}", getSampleClassId()));
    }
    if (getDetailedQcStatusId() != null) {
      setDetailedQcStatusUrl(WritableUrls.buildUriPath(baseUri, "/rest/detailedqcstatus/{id}", getDetailedQcStatusId()));
    }
    if (getSubprojectId() != null) {
      setSubprojectUrl(WritableUrls.buildUriPath(baseUri, "/rest/subproject/{id}", getSubprojectId()));
    }
    if (getParentId() != null) {
      setParentUrl(WritableUrls.buildUriPath(baseUri, "/rest/sample/{id}", getParentId()));
    }
  }

  public String getIdentityConsentLevel() {
    return identityConsentLevel;
  }

  public void setIdentityConsentLevel(String identityConsentLevel) {
    this.identityConsentLevel = identityConsentLevel;
  }

  public String getEffectiveGroupId() {
    return effectiveGroupId;
  }

  public void setEffectiveGroupId(String effectiveGroupId) {
    this.effectiveGroupId = effectiveGroupId;
  }

  public String getEffectiveGroupIdSample() {
    return effectiveGroupIdSample;
  }

  public void setEffectiveGroupIdSample(String effectiveGroupIdSample) {
    this.effectiveGroupIdSample = effectiveGroupIdSample;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

 public String getExternalNames() {
    return externalNames;
  }

  public void setExternalNames(String externalNames) {
    this.externalNames = externalNames;
  }

}
