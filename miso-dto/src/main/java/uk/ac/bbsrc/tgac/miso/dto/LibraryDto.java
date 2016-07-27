package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LibraryDto {

  private String alias;
  private Double concentration;
  private String creationDate;
  private String description;
  private String identificationBarcode;
  private String lastModified;
  private Long libraryId;
  private LibraryAdditionalInfoDto libraryAdditionalInfo;
  private Long librarySelectionTypeId;
  private Long libraryStrategyTypeId;
  private Long libraryTypeId;
  private String libraryTypeAlias;
  private String locationLabel;
  private Boolean lowQuality;
  private String name;
  private Boolean paired;
  private String parentSampleAlias;
  private Long parentSampleId;
  private String platformName;
  private Boolean qcPassed;
  private Long tagBarcodeIndex1Id;
  private Long tagBarcodeIndex2Id;
  private String tagBarcodeIndex1Label;
  private String tagBarcodeIndex2Label;
  private String tagBarcodeStrategyName;
  private String url;
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

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public String getLastModified() {
    return lastModified;
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

  public String getLibraryTypeAlias() {
    return libraryTypeAlias;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  public Boolean getLowQuality() {
    return lowQuality;
  }

  public String getName() {
    return name;
  }

  public Boolean getPaired() {
    return paired;
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

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public Long getTagBarcodeIndex1Id() {
    return tagBarcodeIndex1Id;
  }

  public Long getTagBarcodeIndex2Id() {
    return tagBarcodeIndex2Id;
  }

  public String getTagBarcodeIndex1Label() {
    return tagBarcodeIndex1Label;
  }

  public String getTagBarcodeIndex2Label() {
    return tagBarcodeIndex2Label;
  }

  public String getTagBarcodeStrategyName() {
    return tagBarcodeStrategyName;
  }

  public String getUrl() {
    return url;
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

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
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

  public void setLibraryTypeAlias(String libraryTypeAlias) {
    this.libraryTypeAlias = libraryTypeAlias;
  }

  public void setLibraryTypeId(Long libraryTypeId) {
    this.libraryTypeId = libraryTypeId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public void setLowQuality(Boolean lowQuality) {
    this.lowQuality = lowQuality;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPaired(Boolean paired) {
    this.paired = paired;
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

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setTagBarcodeIndex1Id(Long tagBarcodeIndex1Id) {
    this.tagBarcodeIndex1Id = tagBarcodeIndex1Id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setTagBarcodeIndex2Id(Long tagBarcodeIndex2Id) {
    this.tagBarcodeIndex2Id = tagBarcodeIndex2Id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setTagBarcodeIndex1Label(String tagBarcodeIndex1Label) {
    this.tagBarcodeIndex1Label = tagBarcodeIndex1Label;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setTagBarcodeIndex2Label(String tagBarcodeIndex2Label) {
    this.tagBarcodeIndex2Label = tagBarcodeIndex2Label;
  }

  public void setTagBarcodeStrategyName(String tagBarcodeStrategyName) {
    this.tagBarcodeStrategyName = tagBarcodeStrategyName;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVolume(Double volume) {
    this.volume = volume;
  }

  public void writeUrls(UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    writeUrls(baseUri);
  }

  public void writeUrls(URI baseUri) {
    setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/library/{id}").buildAndExpand(getId()).toUriString());
  }
}
