package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LibraryDto {
  
  private String alias;
  private Double concentration;
  private String creationDate;
  private String description;
  private Long libraryId;
  private LibraryAdditionalInfoDto libraryAdditionalInfo;
  private Long librarySelectionTypeId;
  private Long libraryStrategyTypeId;
  private Long libraryTypeId;
  private Boolean lowQuality;
  private String name;
  private String parentSampleAlias;
  private Long parentSampleId;
  private String platformName;
  private Long tagBarcodeIndex1Id;
  private Long tagBarcodeIndex2Id;
  private String tagBarcodeStrategyName;
  private Double volume;
  
  public String getAlias() {
    return alias;
  }
  public Double getConcentration() {
    return concentration;
  }
  public String getCreationDate() {
    return creationDate;
  }
  public String getDescription() {
    return description;
  }
  public Long getId() {
    return libraryId;
  }
  public LibraryAdditionalInfoDto getLibraryAdditionalInfo() {
    return libraryAdditionalInfo;
  }
  public Long getLibrarySelectionTypeId() {
    return librarySelectionTypeId;
  }
  public Long getLibraryStrategyTypeId() {
    return libraryStrategyTypeId;
  }
  public Long getLibraryTypeId() {
    return libraryTypeId;
  }
  public Boolean getLowQuality() {
    return lowQuality;
  }
  public String getName() {
    return name;
  }
  public String getParentSampleAlias() {
    return parentSampleAlias;
  }
  public Long getParentSampleId() {
    return parentSampleId;
  }
  public String getPlatformName() {
    return platformName;
  }
  public Long getTagBarcodeIndex1Id() {
    return tagBarcodeIndex1Id;
  }
  public Long getTagBarcodeIndex2Id() {
    return tagBarcodeIndex2Id;
  }
  public String getTagBarcodeStrategyName() {
    return tagBarcodeStrategyName;
  }
  public Double getVolume() {
    return volume;
  }
  public void setAlias(String alias) {
    this.alias = alias;
  }
  public void setConcentration(Double concentration) {
    this.concentration = concentration;
  }
  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public void setId(Long libraryId) {
    this.libraryId = libraryId;
  }
  public void setLibraryAdditionalInfo(LibraryAdditionalInfoDto libraryAdditionalInfo) {
    this.libraryAdditionalInfo = libraryAdditionalInfo;
  }
  public void setLibrarySelectionTypeId(Long librarySelectionTypeId) {
    this.librarySelectionTypeId = librarySelectionTypeId;
  }
  public void setLibraryStrategyTypeId(Long libraryStrategyTypeId) {
    this.libraryStrategyTypeId = libraryStrategyTypeId;
  }
  public void setLibraryTypeId(Long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }
  public void setLowQuality(Boolean lowQuality) {
    this.lowQuality = lowQuality;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setParentSampleAlias(String parentSampleAlias) {
    this.parentSampleAlias = parentSampleAlias;
  }
  public void setParentSampleId(Long parentSampleId) {
    this.parentSampleId = parentSampleId;
  }
  public void setPlatformName(String platformName) {
    this.platformName = platformName;
  }
  public void setTagBarcodeIndex1Id(Long tagBarcodeIndex1Id) {
    this.tagBarcodeIndex1Id = tagBarcodeIndex1Id;
  }
  public void setTagBarcodeIndex2Id(Long tagBarcodeIndex2Id) {
    this.tagBarcodeIndex2Id = tagBarcodeIndex2Id;
  }
  public void setTagBarcodeStrategyName(String tagBarcodeStrategyName) {
    this.tagBarcodeStrategyName = tagBarcodeStrategyName;
  }
  public void setVolume(Double volume) {
    this.volume = volume;
  }

}
