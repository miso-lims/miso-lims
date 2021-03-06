package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KitDescriptorDto {
  
  private Long id;
  private String name;
  private String description;
  private Integer version;
  private String manufacturer;
  private String partNumber;
  private Integer stockLevel;
  private String kitType;
  private String platformType;
  private boolean archived = false;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getPartNumber() {
    return partNumber;
  }

  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }

  public Integer getStockLevel() {
    return stockLevel;
  }

  public void setStockLevel(Integer stockLevel) {
    this.stockLevel = stockLevel;
  }

  public String getKitType() {
    return kitType;
  }

  public void setKitType(String kitType) {
    this.kitType = kitType;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  @Override
  public String toString() {
    return "KitDescriptorDto [id=" + id + ", name=" + name + ", version=" + version + ", manufacturer=" + manufacturer
        + ", partNumber=" + partNumber + ", stockLevel=" + stockLevel + ", kitType=" + kitType + ", platformType=" + platformType + "]";
  }

}
