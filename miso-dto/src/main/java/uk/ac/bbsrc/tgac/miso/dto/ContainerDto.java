package uk.ac.bbsrc.tgac.miso.dto;

import java.net.URI;
import java.util.List;

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
  private KitDescriptorDto clusteringKit;
  private KitDescriptorDto multiplexingKit;
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

  public KitDescriptorDto getClusteringKit() {
    return clusteringKit;
  }

  public void setClusteringKit(KitDescriptorDto clusteringKit) {
    this.clusteringKit = clusteringKit;
  }

  public KitDescriptorDto getMultiplexingKit() {
    return multiplexingKit;
  }

  public void setMultiplexingKit(KitDescriptorDto multiplexingKit) {
    this.multiplexingKit = multiplexingKit;
  }

  public List<PartitionDto> getPartitions() {
    return partitions;
  }

  public void setPartitions(List<PartitionDto> partitions) {
    this.partitions = partitions;
  }
}
