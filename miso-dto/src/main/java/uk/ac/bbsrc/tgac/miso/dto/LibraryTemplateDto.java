package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryTemplateDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryTemplateDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryTemplateDto {

  private Long libraryTemplateId;
  private String alias;
  private List<Long> projectIds;
  private String defaultVolume;
  private String volumeUnits;
  private String platformType;
  private Long libraryTypeId;
  private Long selectionId;
  private Long strategyId;
  private Long kitDescriptorId;
  private Long indexFamilyId;

  private Map<String, Long> indexOneIds;

  private Map<String, Long> indexTwoIds;

  public Long getId() {
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

  public List<Long> getProjectIds() {
    return projectIds;
  }

  public void setProjectIds(List<Long> projectIds) {
    this.projectIds = projectIds;
  }

  public String getDefaultVolume() {
    return defaultVolume;
  }

  public void setDefaultVolume(String defaultVolume) {
    this.defaultVolume = defaultVolume;
  }

  public String getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(String volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public Long getLibraryTypeId() {
    return libraryTypeId;
  }

  public void setLibraryTypeId(Long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }

  public Long getSelectionId() {
    return selectionId;
  }

  public void setSelectionId(Long selectionId) {
    this.selectionId = selectionId;
  }

  public Long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(Long strategyId) {
    this.strategyId = strategyId;
  }

  public Long getKitDescriptorId() {
    return kitDescriptorId;
  }

  public void setKitDescriptorId(Long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  public Long getIndexFamilyId() {
    return indexFamilyId;
  }

  public void setIndexFamilyId(Long indexFamilyId) {
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
