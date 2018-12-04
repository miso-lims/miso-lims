package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class InstrumentModelDto {
  private boolean active;
  private String description;

  private long id;

  private String alias;

  private int numContainers;

  private String platformType;

  private String instrumentType;

  private List<String> positions;

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getAlias() {
    return alias;
  }


  public int getNumContainers() {
    return numContainers;
  }

  public String getPlatformType() {
    return platformType;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setNumContainers(int numContainers) {
    this.numContainers = numContainers;
  }

  public void setPlatformType(String platformType) {
    this.platformType = platformType;
  }

  public String getInstrumentType() {
    return instrumentType;
  }

  public void setInstrumentType(String instrumentType) {
    this.instrumentType = instrumentType;
  }

  public List<String> getPositions() {
    return positions;
  }

  public void setPositions(List<String> positions) {
    this.positions = positions;
  }

}
