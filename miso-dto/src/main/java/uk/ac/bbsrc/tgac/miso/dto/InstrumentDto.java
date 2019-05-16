package uk.ac.bbsrc.tgac.miso.dto;

public class InstrumentDto {
  
  private long id;
  private String name;
  private String instrumentType;
  private Long instrumentModelId;
  private String instrumentModelAlias;
  private String platformType;
  private String serialNumber;
  private String status;
  private boolean outOfService;
  private String dateCommissioned;
  private String dateDecommissioned;
  private Long preUpgradeInstrumentId;
  private String preUpgradeInstrumentName;
  private Long upgradedInstrumentId;

  public String getDateCommissioned() {
    return dateCommissioned;
  }

  public String getDateDecommissioned() {
    return dateDecommissioned;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
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

  public void setName(String name) {
    this.name = name;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getInstrumentType() {
    return instrumentType;
  }

  public void setInstrumentType(String instrumentType) {
    this.instrumentType = instrumentType;
  }

  public Long getInstrumentModelId() {
    return instrumentModelId;
  }

  public void setInstrumentModelId(Long instrumentModelId) {
    this.instrumentModelId = instrumentModelId;
  }

  public String getInstrumentModelAlias() {
    return instrumentModelAlias;
  }

  public void setInstrumentModelAlias(String instrumentModelAlias) {
    this.instrumentModelAlias = instrumentModelAlias;
  }

  public String getPlatformType() {
    return platformType;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public boolean isOutOfService() {
    return outOfService;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

  public Long getPreUpgradeInstrumentId() {
    return preUpgradeInstrumentId;
  }

  public void setPreUpgradeInstrumentId(Long preUpgradeInstrumentId) {
    this.preUpgradeInstrumentId = preUpgradeInstrumentId;
  }

  public String getPreUpgradeInstrumentName() {
    return preUpgradeInstrumentName;
  }

  public void setPreUpgradeInstrumentName(String preUpgradeInstrumentName) {
    this.preUpgradeInstrumentName = preUpgradeInstrumentName;
  }

  public Long getUpgradedInstrumentId() {
    return upgradedInstrumentId;
  }

  public void setUpgradedInstrumentId(Long upgradedInstrumentId) {
    this.upgradedInstrumentId = upgradedInstrumentId;
  }

}
