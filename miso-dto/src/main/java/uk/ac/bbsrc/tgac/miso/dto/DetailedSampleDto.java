package uk.ac.bbsrc.tgac.miso.dto;

public class DetailedSampleDto extends SampleDto implements UpstreamQcFailableDto {

  private Long parentId;
  private String parentName;
  private String parentAlias;
  private String parentBoxPosition;
  private String parentBoxPositionLabel;
  private Long parentSampleClassId;
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
  private String effectiveTissueOriginAlias;
  private String effectiveTissueOriginDescription;
  private String effectiveTissueTypeAlias;
  private String effectiveTissueTypeDescription;
  private String effectiveTimepoint;
  private Long effectiveQcFailureId;
  private String effectiveQcFailureLevel;
  private String creationDate;
  private String volumeUsed;
  private String ngUsed;

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public String getParentAlias() {
    return parentAlias;
  }

  public void setParentAlias(String parentAlias) {
    this.parentAlias = parentAlias;
  }

  public String getParentBoxPosition() {
    return parentBoxPosition;
  }

  public void setParentBoxPosition(String parentBoxPosition) {
    this.parentBoxPosition = parentBoxPosition;
  }

  public String getParentBoxPositionLabel() {
    return parentBoxPositionLabel;
  }

  public void setParentBoxPositionLabel(String parentBoxPositionLabel) {
    this.parentBoxPositionLabel = parentBoxPositionLabel;
  }

  public Long getParentSampleClassId() {
    return parentSampleClassId;
  }

  public void setParentSampleClassId(Long parentSampleClassId) {
    this.parentSampleClassId = parentSampleClassId;
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

  public String getEffectiveTissueOriginAlias() {
    return effectiveTissueOriginAlias;
  }

  public void setEffectiveTissueOriginAlias(String effectiveTissueOriginAlias) {
    this.effectiveTissueOriginAlias = effectiveTissueOriginAlias;
  }

  public String getEffectiveTissueOriginDescription() {
    return effectiveTissueOriginDescription;
  }

  public void setEffectiveTissueOriginDescription(String effectiveTissueOriginDescription) {
    this.effectiveTissueOriginDescription = effectiveTissueOriginDescription;
  }

  public String getEffectiveTissueTypeAlias() {
    return effectiveTissueTypeAlias;
  }

  public void setEffectiveTissueTypeAlias(String effectiveTissueTypeAlias) {
    this.effectiveTissueTypeAlias = effectiveTissueTypeAlias;
  }

  public String getEffectiveTissueTypeDescription() {
    return effectiveTissueTypeDescription;
  }

  public void setEffectiveTissueTypeDescription(String effectiveTissueTypeDescription) {
    this.effectiveTissueTypeDescription = effectiveTissueTypeDescription;
  }

  public String getEffectiveTimepoint() {
    return effectiveTimepoint;
  }

  public void setEffectiveTimepoint(String effectiveTimepoint) {
    this.effectiveTimepoint = effectiveTimepoint;
  }

  @Override
  public Long getEffectiveQcFailureId() {
    return effectiveQcFailureId;
  }

  @Override
  public void setEffectiveQcFailureId(Long effectiveQcFailureId) {
    this.effectiveQcFailureId = effectiveQcFailureId;
  }

  @Override
  public String getEffectiveQcFailureLevel() {
    return effectiveQcFailureLevel;
  }

  @Override
  public void setEffectiveQcFailureLevel(String effectiveQcFailureLevel) {
    this.effectiveQcFailureLevel = effectiveQcFailureLevel;
  }

}
