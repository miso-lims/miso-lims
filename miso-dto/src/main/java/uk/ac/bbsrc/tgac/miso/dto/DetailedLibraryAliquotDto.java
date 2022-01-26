package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "Detailed")
public class DetailedLibraryAliquotDto extends LibraryAliquotDto {

  private boolean nonStandardAlias;
  private Long libraryDesignCodeId;
  private String identityConsentLevel;
  private String effectiveExternalNames;
  private String effectiveGroupId;
  private String effectiveGroupIdSample;
  private String effectiveTissueOriginAlias;
  private String effectiveTissueOriginDescription;
  private String effectiveTissueTypeAlias;
  private String effectiveTissueTypeDescription;
  private String groupId;
  private String groupDescription;

  public boolean isNonStandardAlias() {
    return nonStandardAlias;
  }

  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  public Long getLibraryDesignCodeId() {
    return libraryDesignCodeId;
  }

  public void setLibraryDesignCodeId(Long libraryDesignCodeId) {
    this.libraryDesignCodeId = libraryDesignCodeId;
  }

  public String getIdentityConsentLevel() {
    return identityConsentLevel;
  }

  public void setIdentityConsentLevel(String identityConsentLevel) {
    this.identityConsentLevel = identityConsentLevel;
  }

  public String getEffectiveExternalNames() {
    return effectiveExternalNames;
  }

  public void setEffectiveExternalNames(String effectiveExternalNames) {
    this.effectiveExternalNames = effectiveExternalNames;
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

}
