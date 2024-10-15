package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleSlide;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockRna;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStockSingleCell;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissuePiece;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferSample;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = SampleAliquotDto.class, name = SampleAliquot.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleAliquotRnaDto.class, name = SampleAliquotRna.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleAliquotSingleCellDto.class, name = SampleAliquotSingleCell.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleIdentityDto.class, name = SampleIdentity.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleStockDto.class, name = SampleStock.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleStockRnaDto.class, name = SampleStockRna.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleStockSingleCellDto.class, name = SampleStockSingleCell.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueDto.class, name = SampleTissue.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissueProcessingDto.class, name = SampleTissueProcessing.CATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleSlideDto.class, name = SampleSlide.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleTissuePieceDto.class, name = SampleTissuePiece.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleSingleCellDto.class, name = SampleSingleCell.SUBCATEGORY_NAME),
    @JsonSubTypes.Type(value = SampleDto.class, name = "Plain")})
@JsonTypeName(value = "Plain")
public class SampleDto extends AbstractBoxableDto implements ReceivableDto<Sample, TransferSample> {

  private Long id;
  private String accession;
  private String name;
  private String description;
  // Skipped security profile
  private String identificationBarcode;
  private String locationBarcode;
  private String locationLabel;
  private String sampleType;
  private String receivedTime;
  private Long senderLabId;
  private Long recipientGroupId;
  private Boolean received;
  private Boolean receiptQcPassed;
  private String receiptQcNote;
  private Long detailedQcStatusId;
  private String detailedQcStatusNote;
  private String qcUserName;
  private String qcDate;
  private String alias;
  private Long projectId;
  private String projectName;
  private String projectTitle;
  private String projectCode;
  private Long scientificNameId;
  private String taxonIdentifier;
  private Long rootSampleClassId;
  private String initialVolume;
  private String volume;
  @Enumerated(EnumType.STRING)
  private VolumeUnit volumeUnits;
  private String concentration;
  @Enumerated(EnumType.STRING)
  private ConcentrationUnit concentrationUnits;
  private Long updatedById;
  private String lastModified;
  private String qcDv200;
  private String qcRin;
  private List<QcDto> qcs;
  private Long requisitionId;
  private String requisitionAlias;
  private List<Long> requisitionAssayIds;
  private Long sequencingControlTypeId;
  private int libraryCount = 0;
  private Long sopId;
  private String worksetAddedTime;
  private Boolean requisitionStopped;
  private Boolean requisitionPaused;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getLocationBarcode() {
    return locationBarcode;
  }

  public void setLocationBarcode(String locationBarcode) {
    this.locationBarcode = locationBarcode;
  }

  public String getLocationLabel() {
    return locationLabel;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public void setLocationLabel(String locationLabel) {
    this.locationLabel = locationLabel;
  }

  public String getSampleType() {
    return sampleType;
  }

  public void setSampleType(String sampleType) {
    this.sampleType = sampleType;
  }

  @Override
  public String getReceivedTime() {
    return receivedTime;
  }

  @Override
  public void setReceivedTime(String receivedTime) {
    this.receivedTime = receivedTime;
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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public String getProjectTitle() {
    return projectTitle;
  }

  public void setProjectTitle(String projectTitle) {
    this.projectTitle = projectTitle;
  }

  public String getProjectCode() {
    return projectCode;
  }

  public void setProjectCode(String projectCode) {
    this.projectCode = projectCode;
  }

  public Long getScientificNameId() {
    return scientificNameId;
  }

  public void setScientificNameId(Long scientificNameId) {
    this.scientificNameId = scientificNameId;
  }

  public String getTaxonIdentifier() {
    return taxonIdentifier;
  }

  public void setTaxonIdentifier(String taxonIdentifier) {
    this.taxonIdentifier = taxonIdentifier;
  }

  public Long getRootSampleClassId() {
    return rootSampleClassId;
  }

  public void setRootSampleClassId(Long rootSampleClassId) {
    this.rootSampleClassId = rootSampleClassId;
  }

  public String getInitialVolume() {
    return initialVolume;
  }

  public void setInitialVolume(String initialVolume) {
    this.initialVolume = initialVolume;
  }

  public String getVolume() {
    return volume;
  }

  public void setVolume(String volume) {
    this.volume = volume;
  }

  public String getConcentration() {
    return concentration;
  }

  public void setConcentration(String concentration) {
    this.concentration = concentration;
  }

  public Long getUpdatedById() {
    return updatedById;
  }

  public void setUpdatedById(Long updatedById) {
    this.updatedById = updatedById;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public String getQcDv200() {
    return qcDv200;
  }

  public void setQcDv200(String qcDv200) {
    this.qcDv200 = qcDv200;
  }

  public String getQcRin() {
    return qcRin;
  }

  public void setQcRin(String qcRin) {
    this.qcRin = qcRin;
  }

  public List<QcDto> getQcs() {
    return qcs;
  }

  public void setQcs(List<QcDto> qcs) {
    this.qcs = qcs;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public VolumeUnit getVolumeUnits() {
    return volumeUnits;
  }

  public void setVolumeUnits(VolumeUnit volumeUnits) {
    this.volumeUnits = volumeUnits;
  }

  @JsonInclude(JsonInclude.Include.ALWAYS)
  public ConcentrationUnit getConcentrationUnits() {
    return concentrationUnits;
  }

  public void setConcentrationUnits(ConcentrationUnit concentrationUnits) {
    this.concentrationUnits = concentrationUnits;
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

  public List<Long> getRequisitionAssayIds() {
    return requisitionAssayIds;
  }

  public void setRequisitionAssayIds(List<Long> requisitionAssayIds) {
    this.requisitionAssayIds = requisitionAssayIds;
  }

  public int getLibraryCount() {
    return libraryCount;
  }

  public void setLibraryCount(int libraryCount) {
    this.libraryCount = libraryCount;
  }

  @Override
  public TransferSample makeTransferItem() {
    return new TransferSample();
  }

  @JsonIgnore
  @Override
  public Function<Transfer, Set<TransferSample>> getTransferItemsFunction() {
    return Transfer::getSampleTransfers;
  }

  public Long getSequencingControlTypeId() {
    return sequencingControlTypeId;
  }

  public void setSequencingControlTypeId(Long sequencingControlTypeId) {
    this.sequencingControlTypeId = sequencingControlTypeId;
  }

  public Long getSopId() {
    return sopId;
  }

  public void setSopId(Long sopId) {
    this.sopId = sopId;
  }

  public String getWorksetAddedTime() {
    return worksetAddedTime;
  }

  public void setWorksetAddedTime(String worksetAddedTime) {
    this.worksetAddedTime = worksetAddedTime;
  }

  public boolean getRequsitionStopped() { return requisitionStopped; }

  public void setRequisitionStopped(Boolean requisitionStopped) {
    this.requisitionStopped = requisitionStopped;
  }

  public boolean getRequisitionPaused() { return requisitionPaused; }

  public void setRequisitionPaused(Boolean requisitionPaused) {
    this.requisitionPaused = requisitionPaused;
  }
}
