package uk.ac.bbsrc.tgac.miso.dto;

public class SequencerDto {
  private String dateCommissioned;
  
  private String dateDecommissioned;

  private long id;

  private String ip;
  
  private String name;
  
  private PlatformDto platform;
  
  private String serialNumber;

  public String getDateCommissioned() {
    return dateCommissioned;
  }

  public String getDateDecommissioned() {
    return dateDecommissioned;
  }

  public long getId() {
    return id;
  }

  public String getIp() {
    return ip;
  }

  public String getName() {
    return name;
  }

  public PlatformDto getPlatform() {
    return platform;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setDateCommissioned(String dateCommissioned) {
    this.dateCommissioned = dateCommissioned;
  }

  public void setDateDecommissioned(String dateDecommissioned) {
    this.dateDecommissioned = dateDecommissioned;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPlatform(PlatformDto platform) {
    this.platform = platform;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

}
