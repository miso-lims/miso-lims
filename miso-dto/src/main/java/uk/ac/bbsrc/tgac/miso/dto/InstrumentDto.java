package uk.ac.bbsrc.tgac.miso.dto;

public class InstrumentDto {
  private String dateCommissioned;
  
  private String dateDecommissioned;

  private long id;

  private String ip;
  
  private String name;
  
  private InstrumentModelDto instrumentModel;
  
  private String serialNumber;

  private String status;

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

  public InstrumentModelDto getInstrumentModel() {
    return instrumentModel;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public String getStatus() {
    return status;
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

  public void setInstrumentModel(InstrumentModelDto instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
