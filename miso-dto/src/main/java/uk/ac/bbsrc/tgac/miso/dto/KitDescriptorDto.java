package uk.ac.bbsrc.tgac.miso.dto;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class KitDescriptorDto {
  
  private Long id;
  private String url;
  private String name;
  private Double version;
  private String manufacturer;
  private String partNumber;
  private Integer stockLevel;
  private String kitType;
  private String platformType;
  
  public KitDescriptorDto() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getVersion() {
    return version;
  }

  public void setVersion(Double version) {
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

  @Override
  public String toString() {
    return "KitDescriptorDto [id=" + id + ", url=" + url + ", name=" + name
        + ", version=" + version + ", manufacturer=" + manufacturer
        + ", partNumber=" + partNumber + ", stockLevel=" + stockLevel
        + ", kitType=" + kitType + ", platformType=" + platformType + "]";
  }

}
