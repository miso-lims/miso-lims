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
  private Double defaultVolume;
  private String platformType;
  private Long libraryTypeId;
  private Long selectionTypeId;
  private Long strategyTypeId;
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

  public Long getLibraryTypeId() {
    return libraryTypeId;
  }

  public void setLibraryTypeId(Long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }

  public Long getSelectionTypeId() {
    return selectionTypeId;
  }

  public void setSelectionTypeId(Long selectionTypeId) {
    this.selectionTypeId = selectionTypeId;
  }

  public Long getStrategyTypeId() {
    return strategyTypeId;
  }

  public void setStrategyTypeId(Long strategyTypeId) {
    this.strategyTypeId = strategyTypeId;
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
