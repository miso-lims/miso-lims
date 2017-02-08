package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriComponentsBuilder;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryDto {

  private String alias;
  private Double concentration;
  private String creationDate;
  private String description;
  private String identificationBarcode;
  private String lastModified;
  private Long id;
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
  private Long parentSampleClassId;
  private String platformType;
  private Boolean qcPassed;
  private Long index1Id;
  private Long index2Id;
  private String index1Label;
  private String index2Label;
  private String indexFamilyName;
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
    return id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public String getLastModified() {
    return lastModified;
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

  public String getPlatformType() {
    return platformType;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public Long getIndex1Id() {
    return index1Id;
  }

  public Long getIndex2Id() {
    return index2Id;
  }

  public String getIndex1Label() {
    return index1Label;
  }

  public String getIndex2Label() {
    return index2Label;
  }

  public String getIndexFamilyName() {
    return indexFamilyName;
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
    this.id = libraryId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
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

  public Long getParentSampleClassId() {
    return parentSampleClassId;
  }

  public void setParentSampleClassId(Long parentSampleClassId) {
    this.parentSampleClassId = parentSampleClassId;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIndex1Id(Long index1Id) {
    this.index1Id = index1Id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIndex2Id(Long index2Id) {
    this.index2Id = index2Id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIndex1Label(String index1Label) {
    this.index1Label = index1Label;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIndex2Label(String index2Label) {
    this.index2Label = index2Label;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
  public void setIndexFamilyName(String indexFamilyName) {
    this.indexFamilyName = indexFamilyName;
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
