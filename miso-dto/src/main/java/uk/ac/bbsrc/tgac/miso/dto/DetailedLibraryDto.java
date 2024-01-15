package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = "Detailed")
public class DetailedLibraryDto extends LibraryDto {

  private Boolean archived;
  private Long libraryDesignId;
  private Long libraryDesignCodeId;
  private boolean nonStandardAlias;
  private Long preMigrationId;
  private String groupId;
  private String groupDescription;
  private String identityConsentLevel;
  private String effectiveExternalNames;
  private String effectiveGroupId;
  private String effectiveGroupIdSample;
  private String effectiveTissueOriginAlias;
  private String effectiveTissueOriginDescription;
  private String effectiveTissueTypeAlias;
  private String effectiveTissueTypeDescription;
  private Boolean subprojectPriority;
  private String subprojectAlias;

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public Long getLibraryDesignId() {
    return libraryDesignId;
  }

  public void setLibraryDesignId(Long libraryDesignId) {
    this.libraryDesignId = libraryDesignId;
  }

  public Long getLibraryDesignCodeId() {
    return libraryDesignCodeId;
  }

  public void setLibraryDesignCodeId(Long libraryDesignCodeId) {
    this.libraryDesignCodeId = libraryDesignCodeId;
  }

  public boolean getNonStandardAlias() {
    return nonStandardAlias;
  }

  public void setNonStandardAlias(boolean nonStandardAlias) {
    this.nonStandardAlias = nonStandardAlias;
  }

  public Long getPreMigrationId() {
    return preMigrationId;
  }

  public void setPreMigrationId(Long preMigrationId) {
    this.preMigrationId = preMigrationId;
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

  public Boolean getSubprojectPriority() {
    return subprojectPriority;
  }

  public void setSubprojectPriority(Boolean subprojectPriority) {
    this.subprojectPriority = subprojectPriority;
  }

  public String getSubprojectAlias() {
    return subprojectAlias;
  }

  public void setSubprojectAlias(String subprojectAlias) {
    this.subprojectAlias = subprojectAlias;
  }

}
