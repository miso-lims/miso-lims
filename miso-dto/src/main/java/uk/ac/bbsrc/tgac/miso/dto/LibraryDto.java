package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.*;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DetailedLibraryDto.class, name = "Detailed"),
    @JsonSubTypes.Type(value = LibraryDto.class, name = "Plain") })
@JsonTypeName(value = "Plain")
public class LibraryDto extends AbstractBoxableDto implements ReceivableDto<Library, TransferLibrary>, UpstreamQcFailableDto {

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
  private String parentSampleName;
  private Long parentSampleClassId;
  private Long projectId;
  private String projectName;
  private String projectShortName;
  private String platformType;
  private Long detailedQcStatusId;
  private String detailedQcStatusNote;
  private String qcUserName;
  private String qcDate;
  private Long index1Id;
  private Long index2Id;
  private String index1Label;
  private String index2Label;
  private Long indexFamilyId;
  private String indexFamilyName;
  private String initialVolume;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String volumeUsed;
  private String ngUsed;
  private List<QcDto> qcs;
  private Integer dnaSize;
  private Long kitDescriptorId;
  private String kitLot;
  private SampleDto sample;
  private String receivedTime;
  private Long senderLabId;
  private Long recipientGroupId;
  private Boolean received;
  private Boolean receiptQcPassed;
  private String receiptQcNote;
  private String sampleBoxPosition;
  private String sampleBoxPositionLabel;
  private Long spikeInId;
  private String spikeInVolume;
  private String spikeInDilutionFactor;
  private boolean umis;
  private Long workstationId;
  private Long thermalCyclerId;
  private Long sopId;
  private String batchId;
  private String worksetAddedTime;
  private Long effectiveQcFailureId;
  private String effectiveQcFailureLevel;
  private Long requisitionId;
  private String requisitionAlias;
  private Long requisitionAssayId;

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

  public Long getIndexFamilyId() {
    return indexFamilyId;
  }

  public void setIndexFamilyId(Long indexFamilyId) {
    this.indexFamilyId = indexFamilyId;
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

  public Long getProjectId() {
    return projectId;
  }

  public Long getParentSampleId() {
    return parentSampleId;
  }

  public String getPlatformType() {
    return platformType;
  }

  public List<QcDto> getQcs() {
    return qcs;
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

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public void setParentSampleId(Long parentSampleId) {
    this.parentSampleId = parentSampleId;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public void setQcs(List<QcDto> qcs) {
    this.qcs = qcs;
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

  public String getKitLot() {
    return kitLot;
  }

  public void setKitLot(String kitLot) {
    this.kitLot = kitLot;
  }

  public SampleDto getSample() {
    return sample;
  }

  public void setSample(SampleDto sample) {
    this.sample = sample;
  }

  @Override
  public String getReceivedTime() {
    return receivedTime;
  }

  @Override
  public void setReceivedTime(String receivedDate) {
    this.receivedTime = receivedDate;
  }

  @Override
  public Long getSenderLabId() {
    return senderLabId;
  }

  @Override
  public void setSenderLabId(Long senderLabId) {
    this.senderLabId = senderLabId;
  }

  @Override
  public Long getRecipientGroupId() {
    return recipientGroupId;
  }

  @Override
  public void setRecipientGroupId(Long recipientGroupId) {
    this.recipientGroupId = recipientGroupId;
  }

  @Override
  public Boolean isReceived() {
    return received;
  }

  @Override
  public void setReceived(Boolean received) {
    this.received = received;
  }

  @Override
  public Boolean isReceiptQcPassed() {
    return receiptQcPassed;
  }

  @Override
  public void setReceiptQcPassed(Boolean receiptQcPassed) {
    this.receiptQcPassed = receiptQcPassed;
  }

  @Override
  public String getReceiptQcNote() {
    return receiptQcNote;
  }

  @Override
  public void setReceiptQcNote(String receiptQcNote) {
    this.receiptQcNote = receiptQcNote;
  }

  public String getSampleBoxPosition() {
    return sampleBoxPosition;
  }

  public void setSampleBoxPosition(String sampleBoxPosition) {
    this.sampleBoxPosition = sampleBoxPosition;
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

  public String getInitialVolume() {
    return initialVolume;
  }

  public void setInitialVolume(String initialVolume) {
    this.initialVolume = initialVolume;
  }

  public String getVolumeUsed() {
    return volumeUsed;
  }

  public void setVolumeUsed(String volumeUsed) {
    this.volumeUsed = volumeUsed;
  }

  public String getNgUsed() {
    return ngUsed;
  }

  public void setNgUsed(String ngUsed) {
    this.ngUsed = ngUsed;
  }

  public boolean getUmis() {
    return umis;
  }

  public void setUmis(boolean umis) {
    this.umis = umis;
  }

  public Long getWorkstationId() {
    return workstationId;
  }

  public void setWorkstationId(Long workstationId) {
    this.workstationId = workstationId;
  }

  public Long getThermalCyclerId() {
    return thermalCyclerId;
  }

  public void setThermalCyclerId(Long thermalCyclerId) {
    this.thermalCyclerId = thermalCyclerId;
  }

  @Override
  public TransferLibrary makeTransferItem() {
    return new TransferLibrary();
  }

  @JsonIgnore
  @Override
  public Function<Transfer, Set<TransferLibrary>> getTransferItemsFunction() {
    return Transfer::getLibraryTransfers;
  }

  public Long getSopId() {
    return sopId;
  }

  public void setSopId(Long sopId) {
    this.sopId = sopId;
  }

  public String getBatchId() {
    return batchId;
  }

  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }

  public String getWorksetAddedTime() {
    return worksetAddedTime;
  }

  public void setWorksetAddedTime(String worksetAddedTime) {
    this.worksetAddedTime = worksetAddedTime;
  }

  public Long getDetailedQcStatusId() {
    return detailedQcStatusId;
  }

  public void setDetailedQcStatusId(Long detailedQcStatusId) {
    this.detailedQcStatusId = detailedQcStatusId;
  }

  public String getDetailedQcStatusNote() {
    return detailedQcStatusNote;
  }

  public void setDetailedQcStatusNote(String detailedQcStatusNote) {
    this.detailedQcStatusNote = detailedQcStatusNote;
  }

  public String getQcUserName() {
    return qcUserName;
  }

  public void setQcUserName(String qcUserName) {
    this.qcUserName = qcUserName;
  }

  public String getQcDate() {
    return qcDate;
  }

  public void setQcDate(String qcDate) {
    this.qcDate = qcDate;
  }

  @Override
  public Long getEffectiveQcFailureId() {
    return effectiveQcFailureId;
  }

  @Override
  public void setEffectiveQcFailureId(Long effectiveQcFailureId) {
    this.effectiveQcFailureId = effectiveQcFailureId;
  }

  @Override
  public String getEffectiveQcFailureLevel() {
    return effectiveQcFailureLevel;
  }

  @Override
  public void setEffectiveQcFailureLevel(String effectiveQcFailureLevel) {
    this.effectiveQcFailureLevel = effectiveQcFailureLevel;
  }

  public String getParentSampleName() {
    return parentSampleName;
  }

  public void setParentSampleName(String parentSampleName) {
    this.parentSampleName = parentSampleName;
  }

  public Long getRequisitionId() {
    return requisitionId;
  }

  public void setRequisitionId(Long requisitionId) {
    this.requisitionId = requisitionId;
  }

  public String getRequisitionAlias() {
    return requisitionAlias;
  }

  public void setRequisitionAlias(String requisitionAlias) {
    this.requisitionAlias = requisitionAlias;
  }

  public Long getRequisitionAssayId() {
    return requisitionAssayId;
  }

  public void setRequisitionAssayId(Long requisitionAssayId) {
    this.requisitionAssayId = requisitionAssayId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectShortName() {
    return projectShortName;
  }

  public void setProjectShortName(String projectShortName) {
    this.projectShortName = projectShortName;
  }
}
