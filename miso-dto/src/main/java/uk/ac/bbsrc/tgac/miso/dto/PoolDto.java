package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class PoolDto implements WritableUrls {

  private Long id;
  private String url;
  private String name;
  private String alias;
  private String concentration;
  private String identificationBarcode;
  private String locationLabel;
  private Boolean readyToRun;
  private Boolean qcPassed;
  private boolean duplicateIndices;
  private Set<String> duplicateIndicesSequences;
  private Integer avgInsertSize;
  private Set<DilutionDto> pooledElements;
  private String creationDate;
  private String lastModified;
  private String description;
  private Long boxId;
  private boolean discarded;
  private String volume;
  private String platformType;
  private int longestIndex;
  private boolean hasLowQualityLibraries;
  private int dilutionCount;
  private Double insertSize;

  public String getAlias() {
    return alias;
  }

  public Integer getAvgInsertSize() {
    return avgInsertSize;
  }

  public Long getBoxId() {
    return boxId;
  }

  public String getConcentration() {
    return concentration;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public String getDescription() {
    return description;
  }

  public boolean getDuplicateIndices() {
    return duplicateIndices;
  }

  public Set<String> getDuplicateIndicesSequences() {
    return duplicateIndicesSequences;
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

  public String getLocationLabel() {
    return locationLabel;
  }

  public String getName() {
    return name;
  }

  public Set<DilutionDto> getPooledElements() {
    return pooledElements;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public Boolean getReadyToRun() {
    return readyToRun;
  }

  public String getUrl() {
    return url;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setAvgInsertSize(Integer avgInsertSize) {
    this.avgInsertSize = avgInsertSize;
  }

  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDuplicateIndices(Boolean duplicateIndices) {
    this.duplicateIndices = duplicateIndices;
  }

  public void setDuplicateIndicesSequences(Set<String> duplicateIndicesSequences) {
    this.duplicateIndicesSequences = duplicateIndicesSequences;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPooledElements(Set<DilutionDto> pooledElements) {
    this.pooledElements = pooledElements;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public void setReadyToRun(Boolean readyToRun) {
    this.readyToRun = readyToRun;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/pool/{id}", getId()));
    for (DilutionDto ldto : getPooledElements()) {
      ldto.setLibraryUrl(
          WritableUrls.buildUriPath(baseUri, "/rest/library/{id}", ldto.getLibrary().getId()));
    }
  }

  public boolean isDiscarded() {
    return discarded;
  }

  public void setDiscarded(boolean isDiscarded) {
    this.discarded = isDiscarded;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public int getLongestIndex() {
    return longestIndex;
  }

  public void setLongestIndex(int longestIndex) {
    this.longestIndex = longestIndex;
  }

  public boolean getHasLowQualityLibraries() {
    return hasLowQualityLibraries;
  }

  public void setHasLowQualityLibraries(boolean hasLowQualityLibraries) {
    this.hasLowQualityLibraries = hasLowQualityLibraries;
  }

  public int getDilutionCount() {
    return dilutionCount;
  }

  public void setDilutionCount(int dilutionCount) {
    this.dilutionCount = dilutionCount;
  }

  public Double getInsertSize() {
    return insertSize;
  }

  public void setInsertSize(Double insertSize) {
    this.insertSize = insertSize;
  }
}
