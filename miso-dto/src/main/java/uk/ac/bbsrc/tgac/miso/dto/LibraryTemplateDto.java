package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryTemplateDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryTemplateDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryTemplateDto {

  private long libraryTemplateId;

  private String alias;

  private long projectId;

  private Double defaultVolume;

  private String platformType;

  private long libraryTypeId;

  private long selectionTypeId;

  private long strategyTypeId;

  private long kitDescriptorId;

  private long indexFamilyId;

  private Map<String, Long> indexOneIds;

  private Map<String, Long> indexTwoIds;

  public long getId() {
    return libraryTemplateId;
  }

  public void setId(long libraryTemplateId) {
    this.libraryTemplateId = libraryTemplateId;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public long getProjectId() {
    return projectId;
  }

  public void setProjectId(long projectId) {
    this.projectId = projectId;
  }

  public Double getDefaultVolume() {
    return defaultVolume;
  }

  public void setDefaultVolume(Double defaultVolume) {
    this.defaultVolume = defaultVolume;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public long getLibraryTypeId() {
    return libraryTypeId;
  }

  public void setLibraryTypeId(long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }

  public long getSelectionTypeId() {
    return selectionTypeId;
  }

  public void setSelectionTypeId(long selectionTypeId) {
    this.selectionTypeId = selectionTypeId;
  }

  public long getStrategyTypeId() {
    return strategyTypeId;
  }

  public void setStrategyTypeId(long strategyTypeId) {
    this.strategyTypeId = strategyTypeId;
  }

  public long getKitDescriptorId() {
    return kitDescriptorId;
  }

  public void setKitDescriptorId(long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  public long getIndexFamilyId() {
    return indexFamilyId;
  }

  public void setIndexFamilyId(long indexFamilyId) {
    this.indexFamilyId = indexFamilyId;
  }

  public Map<String, Long> getIndexOneIds() {
    return indexOneIds;
  }

  public void setIndexOneIds(Map<String, Long> indexOneIds) {
    this.indexOneIds = indexOneIds;
  }

  public Map<String, Long> getIndexTwoIds() {
    return indexTwoIds;
  }

  public void setIndexTwoIds(Map<String, Long> indexTwoIds) {
    this.indexTwoIds = indexTwoIds;
  }

}
