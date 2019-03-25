package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = ContainerDto.class, name = "Container"),
    @JsonSubTypes.Type(value = OxfordNanoporeContainerDto.class, name = "OxfordNanoporeContainer") })
@JsonTypeName(value = "Container")
public class ContainerDto implements WritableUrls {
  private Long id;
  // identificationBarcode is displayed as "serial number" to the user
  private String identificationBarcode;
  private String url;
  private ContainerModelDto model;
  private String lastRunAlias;
  private Long lastRunId;
  private String lastSequencerName;
  private Long lastSequencerId;
  private String lastModified;
  private Long clusterKitId;
  private Long multiplexingKitId;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ContainerModelDto getModel() {
    return model;
  }

  public void setModel(ContainerModelDto model) {
    this.model = model;
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

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  @Override
  public void writeUrls(URI baseUri) {
    setUrl(
        WritableUrls.buildUriPath(baseUri, "/rest/run/container/{barcode}", getIdentificationBarcode()));
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

  public Long getClusterKitId() {
    return clusterKitId;
  }

  public void setClusterKitId(Long clusterKitId) {
    this.clusterKitId = clusterKitId;
  }

  public Long getMultiplexingKitId() {
    return multiplexingKitId;
  }

  public void setMultiplexingKitId(Long multiplexingKitId) {
    this.multiplexingKitId = multiplexingKitId;
  }

  public List<PartitionDto> getPartitions() {
    return partitions;
  }

  public void setPartitions(List<PartitionDto> partitions) {
    this.partitions = partitions;
  }
}
