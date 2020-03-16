package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = ContainerDto.class, name = "Container"),
    @JsonSubTypes.Type(value = OxfordNanoporeContainerDto.class, name = "OxfordNanoporeContainer") })
@JsonTypeName(value = "Container")
public class ContainerDto {
  private Long id;
  // identificationBarcode is displayed as "serial number" to the user
  private String identificationBarcode;
  private SequencingContainerModelDto model;
  private String description;
  private String lastRunAlias;
  private Long lastRunId;
  private Long lastRunInstrumentModelId;
  private String lastSequencerName;
  private Long lastSequencerId;
  private String lastModified;
  private Long clusteringKitId;
  private String clusteringKitLot;
  private Long multiplexingKitId;
  private String multiplexingKitLot;
  private List<PartitionDto> partitions;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public SequencingContainerModelDto getModel() {
    return model;
  }

  public void setModel(SequencingContainerModelDto model) {
    this.model = model;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLastRunAlias() {
    return lastRunAlias;
  }

  public void setLastRunAlias(String lastRunAlias) {
    this.lastRunAlias = lastRunAlias;
  }

  public Long getLastRunId() {
    return lastRunId;
  }

  public void setLastRunId(Long lastRunId) {
    this.lastRunId = lastRunId;
  }

  public Long getLastRunInstrumentModelId() {
    return lastRunInstrumentModelId;
  }

  public void setLastRunInstrumentModelId(Long lastRunInstrumentModelId) {
    this.lastRunInstrumentModelId = lastRunInstrumentModelId;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public String getLastSequencerName() {
    return lastSequencerName;
  }

  public void setLastSequencerName(String lastSequencerName) {
    this.lastSequencerName = lastSequencerName;
  }

  public Long getLastSequencerId() {
    return lastSequencerId;
  }

  public void setLastSequencerId(Long lastSequencerId) {
    this.lastSequencerId = lastSequencerId;
  }

  public Long getClusteringKitId() {
    return clusteringKitId;
  }

  public void setClusteringKitId(Long clusteringKitId) {
    this.clusteringKitId = clusteringKitId;
  }

  public String getClusteringKitLot() {
    return clusteringKitLot;
  }

  public void setClusteringKitLot(String clusteringKitLot) {
    this.clusteringKitLot = clusteringKitLot;
  }

  public Long getMultiplexingKitId() {
    return multiplexingKitId;
  }

  public void setMultiplexingKitId(Long multiplexingKitId) {
    this.multiplexingKitId = multiplexingKitId;
  }

  public String getMultiplexingKitLot() {
    return multiplexingKitLot;
  }

  public void setMultiplexingKitLot(String multiplexingKitLot) {
    this.multiplexingKitLot = multiplexingKitLot;
  }

  public List<PartitionDto> getPartitions() {
    return partitions;
  }

  public void setPartitions(List<PartitionDto> partitions) {
    this.partitions = partitions;
  }
}
