package uk.ac.bbsrc.tgac.miso.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class PoolDto extends AbstractBoxableDto {

  private Long id;
  private String name;
  private String alias;
  private String concentration;
  private Integer dnaSize;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private String identificationBarcode;
  private String locationLabel;
  private Boolean qcPassed;
  private boolean duplicateIndices;
  private Set<String> duplicateIndicesSequences;
  private boolean nearDuplicateIndices;
  private Set<String> nearDuplicateIndicesSequences;
  private Set<LibraryAliquotDto> pooledElements;
  private String creationDate;
  private String lastModified;
  private String description;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String platformType;
  private String longestIndex;
  private boolean hasLowQualityLibraries;
  private int libraryAliquotCount;
  private boolean hasEmptySequence;
  private Set<String> prioritySubprojectAliases;
  private boolean mergeChild;
  private String worksetAddedTime;

  public String getAlias() {
    return alias;
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

  public Set<LibraryAliquotDto> getPooledElements() {
    return pooledElements;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public String getWorksetAddedTime() { return worksetAddedTime; }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public void setWorksetAddedTime(String worksetAddedTime) {
        this.worksetAddedTime = worksetAddedTime;
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

  public void setPooledElements(Set<LibraryAliquotDto> pooledElements) {
    this.pooledElements = pooledElements;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
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

  public int getLibraryAliquotCount() {
    return libraryAliquotCount;
  }

  public void setLibraryAliquotCount(int libraryAliquotCount) {
    this.libraryAliquotCount = libraryAliquotCount;
  }

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  public boolean getHasEmptySequence() {
    return hasEmptySequence;
  }

  public void setHasEmptySequence(boolean hasEmptySequence) {
    this.hasEmptySequence = hasEmptySequence;
  }

  public Set<String> getPrioritySubprojectAliases() {
    return prioritySubprojectAliases;
  }

  public void setPrioritySubprojectAliases(Set<String> prioritySubprojectAliases) {
    this.prioritySubprojectAliases = prioritySubprojectAliases;
  }

  public boolean isMergeChild() {
    return mergeChild;
  }

  public void setMergeChild(boolean mergeChild) {
    this.mergeChild = mergeChild;
  }
}
