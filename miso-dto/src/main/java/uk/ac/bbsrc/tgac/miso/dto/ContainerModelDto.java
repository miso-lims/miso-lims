package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class ContainerModelDto {

  private Long id;
  private String alias;
  private String identificationBarcode;
  private String platformType;
  private Integer partitionCount;
  private Boolean archived;
  private List<Long> platformIds;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getIdentificationBarcode() {
    return identificationBarcode;
  }

  public void setIdentificationBarcode(String identificationBarcode) {
    this.identificationBarcode = identificationBarcode;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public Integer getPartitionCount() {
    return partitionCount;
  }

  public void setPartitionCount(Integer partitionCount) {
    this.partitionCount = partitionCount;
  }

  public Boolean getArchived() {
    return archived;
  }

  public void setArchived(Boolean archived) {
    this.archived = archived;
  }

  public List<Long> getPlatformIds() {
    return platformIds;
  }

  public void setPlatformIds(List<Long> platformIds) {
    this.platformIds = platformIds;
  }

}
