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
  private String sampleBoxPositionLabel;
  private String groupId;
  private String groupDescription;

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

  public String getSampleBoxPositionLabel() {
    return sampleBoxPositionLabel;
  }

  public void setSampleBoxPositionLabel(String boxPositionLabel) {
    this.sampleBoxPositionLabel = boxPositionLabel;
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
