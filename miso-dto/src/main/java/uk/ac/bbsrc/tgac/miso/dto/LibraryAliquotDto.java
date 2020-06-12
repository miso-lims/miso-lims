package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryAliquotDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryAliquotDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryAliquotDto extends AbstractBoxableDto {

  private Long id;
  private String name;
  private String alias;
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
  private Long sampleId;
  private String sampleName;
  private String sampleAlias;
  private Long libraryId;
  private String libraryName;
  private String libraryAlias;
  private Long libraryKitDescriptorId;
  private boolean libraryLowQuality;
  private Boolean libraryQcPassed;
  private String libraryPlatformType;
  private Long parentAliquotId;
  private List<Long> parentAliquotIds;
  private String parentAliquotAlias;
  private String parentName;
  private String parentVolume;
  private String lastModified;
  private List<Long> indexIds;
  private List<String> indexLabels;
  private String identityConsentLevel;
  private String effectiveTissueOriginLabel;
  private String effectiveTissueTypeLabel;
  private String ngUsed;
  private String volumeUsed;
  private Integer dnaSize;
  private Integer proportion;
  private String subprojectAlias;
  private Boolean subprojectPriority;
  private String sequencingControlTypeAlias;
  private Boolean qcPassed;

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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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

  public Long getSampleId() {
    return sampleId;
  }

  public void setSampleId(Long sampleId) {
    this.sampleId = sampleId;
  }

  public String getSampleName() {
    return sampleName;
  }

  public void setSampleName(String sampleName) {
    this.sampleName = sampleName;
  }

  public String getSampleAlias() {
    return sampleAlias;
  }

  public void setSampleAlias(String sampleAlias) {
    this.sampleAlias = sampleAlias;
  }

  public Long getLibraryId() {
    return libraryId;
  }

  public void setLibraryId(Long libraryId) {
    this.libraryId = libraryId;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  public String getLibraryAlias() {
    return libraryAlias;
  }

  public void setLibraryAlias(String libraryAlias) {
    this.libraryAlias = libraryAlias;
  }

  public Long getLibraryKitDescriptorId() {
    return libraryKitDescriptorId;
  }

  public void setLibraryKitDescriptorId(Long libraryKitDescriptorId) {
    this.libraryKitDescriptorId = libraryKitDescriptorId;
  }

  public boolean isLibraryLowQuality() {
    return libraryLowQuality;
  }

  public void setLibraryLowQuality(boolean libraryLowQuality) {
    this.libraryLowQuality = libraryLowQuality;
  }

  public Boolean getLibraryQcPassed() {
    return libraryQcPassed;
  }

  public void setLibraryQcPassed(Boolean libraryQcPassed) {
    this.libraryQcPassed = libraryQcPassed;
  }

  public String getLibraryPlatformType() {
    return libraryPlatformType;
  }

  public void setLibraryPlatformType(String libraryPlatformType) {
    this.libraryPlatformType = libraryPlatformType;
  }

  public Long getParentAliquotId() {
    return parentAliquotId;
  }

  public void setParentAliquotId(Long parentAliquotId) {
    this.parentAliquotId = parentAliquotId;
  }

  public List<Long> getParentAliquotIds() {
    return parentAliquotIds;
  }

  public void setParentAliquotIds(List<Long> parentAliquotIds) {
    this.parentAliquotIds = parentAliquotIds;
  }

  public String getParentAliquotAlias() {
    return parentAliquotAlias;
  }

  public void setParentAliquotAlias(String parentAliquotAlias) {
    this.parentAliquotAlias = parentAliquotAlias;
  }

  public String getParentName() {
    return parentName;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public String getParentVolume() {
    return parentVolume;
  }

  public void setParentVolume(String parentVolume) {
    this.parentVolume = parentVolume;
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

  public String getEffectiveTissueOriginLabel() {
    return effectiveTissueOriginLabel;
  }

  public void setEffectiveTissueOriginLabel(String effectiveTissueOriginLabel) {
    this.effectiveTissueOriginLabel = effectiveTissueOriginLabel;
  }

  public String getEffectiveTissueTypeLabel() {
    return effectiveTissueTypeLabel;
  }

  public void setEffectiveTissueTypeLabel(String effectiveTissueTypeLabel) {
    this.effectiveTissueTypeLabel = effectiveTissueTypeLabel;
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

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
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

  public List<String> getIndexLabels() {
    return indexLabels;
  }

  public void setIndexLabels(List<String> indexLabels) {
    this.indexLabels = indexLabels;
  }

  public String getSequencingControlTypeAlias() {
    return sequencingControlTypeAlias;
  }

  public void setSequencingControlTypeAlias(String sequencingControlTypeAlias) {
    this.sequencingControlTypeAlias = sequencingControlTypeAlias;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

}
