package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.Set;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class PoolDto extends AbstractBoxableDto implements WritableUrls {

  private Long id;
  private String url;
  private String name;
  private String alias;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private String identificationBarcode;
  private String locationLabel;
  private Boolean qcPassed;
  private boolean duplicateIndices;
  private Set<String> duplicateIndicesSequences;
  private boolean nearDuplicateIndices;
  private Set<String> nearDuplicateIndicesSequences;
  private Integer avgInsertSize;
  private Set<DilutionDto> pooledElements;
  private String creationDate;
  private String lastModified;
  private String description;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String platformType;
  private String longestIndex;
  private boolean hasLowQualityLibraries;
  private int dilutionCount;
  private Double insertSize;
  private boolean hasEmptySequence;

  public String getAlias() {
    return alias;
  }

  public Integer getAvgInsertSize() {
    return avgInsertSize;
  }

  public String getConcentration() {
    return concentration;
  }

  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
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

  public boolean getNearDuplicateIndices() {
    return nearDuplicateIndices;
  }

  public Set<String> getNearDuplicateIndicesSequences() {
    return nearDuplicateIndicesSequences;
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

  public String getUrl() {
    return url;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setAvgInsertSize(Integer avgInsertSize) {
    this.avgInsertSize = avgInsertSize;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
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

  public void setNearDuplicateIndices(Boolean nearDuplicateIndices) {
    this.nearDuplicateIndices = nearDuplicateIndices;
  }

  public void setNearDuplicateIndicesSequences(Set<String> nearDuplicateIndicesSequences) {
    this.nearDuplicateIndicesSequences = nearDuplicateIndicesSequences;
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

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public String getLongestIndex() {
    return longestIndex;
  }

  public void setLongestIndex(String longestIndex) {
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

  public boolean getHasEmptySequence() {
    return hasEmptySequence;
  }

  public void setHasEmptySequence(boolean hasEmptySequence) {
    this.hasEmptySequence = hasEmptySequence;
  }
}
