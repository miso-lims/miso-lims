package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryDto extends AbstractBoxableDto implements WritableUrls {

  private String alias;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private String creationDate;
  private String description;
  private String identificationBarcode;
  private String lastModified;
  private Long id;
  private Long librarySelectionTypeId;
  private Long libraryStrategyTypeId;
  private Long libraryTypeId;
  private String libraryTypeAlias;
  private String locationBarcode;
  private String locationLabel;
  private boolean lowQuality;
  private String name;
  private Boolean paired;
  private String parentSampleAlias;
  private Long parentSampleId;
  private Long parentSampleClassId;
  private Long parentSampleProjectId;
  private String platformType;
  private Boolean qcPassed;
  private Long index1Id;
  private Long index2Id;
  private String index1Label;
  private String index2Label;
  private String indexFamilyName;
  private String url;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private List<QcDto> qcs;
  private Integer dnaSize;
  private Long kitDescriptorId;
  private SampleDto sample;
  private String receivedDate;
  private String sampleBoxPositionLabel;
  private Long spikeInId;
  private String spikeInVolume;
  private String spikeInDilutionFactor;

  public String getAlias() {
    return alias;
  }

  public String getConcentration() {
    return concentration;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
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

  public Long getIndex1Id() {
    return index1Id;
  }

  public String getIndex1Label() {
    return index1Label;
  }

  public Long getIndex2Id() {
    return index2Id;
  }

  public String getIndex2Label() {
    return index2Label;
  }

  public String getIndexFamilyName() {
    return indexFamilyName;
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

  public String getLibraryTypeAlias() {
    return libraryTypeAlias;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public Long getLibraryTypeId() {
    return libraryTypeId;
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

  public Long getParentSampleClassId() {
    return parentSampleClassId;
  }

  public Long getParentSampleProjectId() {
    return parentSampleProjectId;
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

  public List<QcDto> getQcs() {
    return qcs;
  }

  public String getUrl() {
    return url;
  }

  public String getVolume() {
    return volume;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long libraryId) {
    this.id = libraryId;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIndex1Id(Long index1Id) {
    this.index1Id = index1Id;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIndex1Label(String index1Label) {
    this.index1Label = index1Label;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIndex2Id(Long index2Id) {
    this.index2Id = index2Id;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIndex2Label(String index2Label) {
    this.index2Label = index2Label;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIndexFamilyName(String indexFamilyName) {
    this.indexFamilyName = indexFamilyName;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
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

  @JsonInclude(JsonInclude.Include.ALWAYS)
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

  public void setParentSampleClassId(Long parentSampleClassId) {
    this.parentSampleClassId = parentSampleClassId;
  }

  public void setParentSampleProjectId(Long parentSampleProjectId) {
    this.parentSampleProjectId = parentSampleProjectId;
  }

  public void setParentSampleId(Long parentSampleId) {
    this.parentSampleId = parentSampleId;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public void setQcs(List<QcDto> qcs) {
    this.qcs = qcs;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  public Integer getDnaSize() {
    return dnaSize;
  }

  public void setDnaSize(Integer dnaSize) {
    this.dnaSize = dnaSize;
  }

  public Long getKitDescriptorId() {
    return kitDescriptorId;
  }

  public void setKitDescriptorId(Long kitDescriptorId) {
    this.kitDescriptorId = kitDescriptorId;
  }

  public SampleDto getSample() {
    return sample;
  }

  public void setSample(SampleDto sample) {
    this.sample = sample;
  }

  public String getReceivedDate() {
    return receivedDate;
  }

  public void setReceivedDate(String receivedDate) {
    this.receivedDate = receivedDate;
  }

  public String getSampleBoxPositionLabel() {
    return sampleBoxPositionLabel;
  }

  public void setSampleBoxPositionLabel(String boxPositionLabel) {
    this.sampleBoxPositionLabel = boxPositionLabel;
  }

  public Long getSpikeInId() {
    return spikeInId;
  }

  public void setSpikeInId(Long spikeInId) {
    this.spikeInId = spikeInId;
  }

  public String getSpikeInVolume() {
    return spikeInVolume;
  }

  public void setSpikeInVolume(String spikeInVolume) {
    this.spikeInVolume = spikeInVolume;
  }

  public String getSpikeInDilutionFactor() {
    return spikeInDilutionFactor;
  }

  public void setSpikeInDilutionFactor(String spikeInDilutionFactor) {
    this.spikeInDilutionFactor = spikeInDilutionFactor;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(WritableUrls.buildUriPath(baseUri, "/rest/library/{id}", getId()));
  }

}
