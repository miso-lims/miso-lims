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
  private String effectiveTissueOriginLabel;
  private String effectiveTissueTypeLabel;
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
