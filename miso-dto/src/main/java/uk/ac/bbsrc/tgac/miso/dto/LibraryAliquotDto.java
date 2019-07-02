package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

public class LibraryAliquotDto extends AbstractBoxableDto {

  private Long id;
  private String name;
  private String identificationBarcode;
  private String locationLabel;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String creationDate;
  private String creatorName;
  private Long targetedSequencingId;
  private Long libraryId;
  private LibraryDto library;
  private String lastModified;
  private List<Long> indexIds;
  private String identityConsentLevel;
  private String ngUsed;
  private String volumeUsed;
  private Integer proportion;
  private String subprojectAlias;
  private Boolean subprojectPriority;
  private boolean distributed;
  private String distributionDate;
  private String distributionRecipient;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public String getConcentration() {
    return concentration;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getCreatorName() {
    return creatorName;
  }

  public void setCreatorName(String userName) {
    this.creatorName = userName;
  }

  public Long getTargetedSequencingId() {
    return targetedSequencingId;
  }

  public void setTargetedSequencingId(Long targetedSequencingId) {
    this.targetedSequencingId = targetedSequencingId;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public LibraryDto getLibrary() {
    return library;
  }

  public void setLibrary(LibraryDto library) {
    this.library = library;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public List<Long> getIndexIds() {
    return indexIds;
  }

  public void setIndexIds(List<Long> indexIds) {
    this.indexIds = indexIds;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnit) {
    this.volumeUnits = volumeUnit;
  }

  public String getIdentityConsentLevel() {
    return identityConsentLevel;
  }

  public void setIdentityConsentLevel(String identityConsentLevel) {
    this.identityConsentLevel = identityConsentLevel;
  }

  public String getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(String ngUsed) {
    this.ngUsed = ngUsed;
  }

  public String getVolumeUsed() {
    return volumeUsed;
  }

  public void setVolumeUsed(String volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  public Integer getProportion() {
    return proportion;
  }

  public void setProportion(Integer proportion) {
    this.proportion = proportion;
  }

  public String getSubprojectAlias() {
    return subprojectAlias;
  }

  public void setSubprojectAlias(String subprojectAlias) {
    this.subprojectAlias = subprojectAlias;
  }

  public Boolean getSubprojectPriority() {
    return subprojectPriority;
  }

  public void setSubprojectPriority(Boolean subprojectPriority) {
    this.subprojectPriority = subprojectPriority;
  }

  public boolean isDistributed() {
    return distributed;
  }

  public void setDistributed(boolean distributed) {
    this.distributed = distributed;
  }

  public String getDistributionDate() {
    return distributionDate;
  }

  public void setDistributionDate(String distributionDate) {
    this.distributionDate = distributionDate;
  }

  public String getDistributionRecipient() {
    return distributionRecipient;
  }

  public void setDistributionRecipient(String distributionRecipient) {
    this.distributionRecipient = distributionRecipient;
  }

}
