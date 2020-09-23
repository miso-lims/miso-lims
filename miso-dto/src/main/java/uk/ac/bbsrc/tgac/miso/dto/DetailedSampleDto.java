package uk.ac.bbsrc.tgac.miso.dto;

public class DetailedSampleDto extends SampleDto {

  private Long parentId;
  private String parentAlias;
  private String parentBoxPositionLabel;
  private Long parentTissueSampleClassId;
  private Long sampleClassId;
  private String sampleClassAlias;
  private String sampleCategory;
  private String sampleSubcategory;
  private Long subprojectId;
  private String subprojectAlias;
  private Boolean subprojectPriority;
  private String groupId;
  private String groupDescription;
  private Boolean isSynthetic;
  private boolean nonStandardAlias;
  private Long identityId;
  private String identityConsentLevel;
  private String effectiveExternalNames;
  private String effectiveGroupId;
  private String effectiveGroupIdSample;
  private String effectiveTissueOriginLabel;
  private String effectiveTissueTypeLabel;
  private String creationDate;
  private String volumeUsed;
  private String ngUsed;

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getParentAlias() {
    return parentAlias;
  }

  public void setParentAlias(String parentAlias) {
    this.parentAlias = parentAlias;
  }

  public String getParentBoxPositionLabel() {
    return parentBoxPositionLabel;
  }

  public void setParentBoxPositionLabel(String parentBoxPositionLabel) {
    this.parentBoxPositionLabel = parentBoxPositionLabel;
  }

  public Long getParentTissueSampleClassId() {
    return parentTissueSampleClassId;
  }

  public void setParentTissueSampleClassId(Long parentSampleClassId) {
    this.parentTissueSampleClassId = parentSampleClassId;
  }

  public Long getSubprojectId() {
    return subprojectId;
  }

  public void setSubprojectId(Long subprojectId) {
    this.subprojectId = subprojectId;
  }

  public String getSubprojectAlias() {
    return subprojectAlias;
  }

  public void setSubprojectAlias(String subprojectAlias) {
    this.subprojectAlias = subprojectAlias;
  }

  public Boolean getSubprojectPriority() {
    return subprojectPriority;
  }

  public void setSubprojectPriority(Boolean subprojectPriority) {
    this.subprojectPriority = subprojectPriority;
  }

  public Long getSampleClassId() {
    return sampleClassId;
  }

  public void setSampleClassId(Long sampleClassId) {
    this.sampleClassId = sampleClassId;
  }

  public String getSampleClassAlias() {
    return sampleClassAlias;
  }

  public void setSampleClassAlias(String sampleClassAlias) {
    this.sampleClassAlias = sampleClassAlias;
  }

  public String getSampleCategory() {
    return sampleCategory;
  }

  public void setSampleCategory(String sampleCategory) {
    this.sampleCategory = sampleCategory;
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

  public Long getIdentityId() {
    return identityId;
  }

  public void setIdentityId(Long identityId) {
    this.identityId = identityId;
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

  public String getEffectiveExternalNames() {
    return effectiveExternalNames;
  }

  public void setEffectiveExternalNames(String effectiveExternalNames) {
    this.effectiveExternalNames = effectiveExternalNames;
  }

  public String getSampleSubcategory() {
    return sampleSubcategory;
  }

  public void setSampleSubcategory(String sampleSubcategory) {
    this.sampleSubcategory = sampleSubcategory;
  }

  public String getVolumeUsed() {
    return volumeUsed;
  }

  public void setVolumeUsed(String volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  public String getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(String ngUsed) {
    this.ngUsed = ngUsed;
  }

  public String getEffectiveTissueOriginLabel() {
    return effectiveTissueOriginLabel;
  }

  public void setEffectiveTissueOriginLabel(String effectiveTissueOriginLabel) {
    this.effectiveTissueOriginLabel = effectiveTissueOriginLabel;
  }

  public String getEffectiveTissueTypeLabel() {
    return effectiveTissueTypeLabel;
  }

  public void setEffectiveTissueTypeLabel(String effectiveTissueTypeLabel) {
    this.effectiveTissueTypeLabel = effectiveTissueTypeLabel;
  }

}
