package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class InstrumentModelDto {

  private long id;
  private String alias;
  private String description;
  private Integer numContainers;
  private String platformType;
  private String instrumentType;
  private String dataManglingPolicy;
  private List<InstrumentPositionDto> positions;
  private List<SequencingContainerModelDto> containerModels;
  private boolean active;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getNumContainers() {
    return numContainers;
  }

  public void setNumContainers(Integer numContainers) {
    this.numContainers = numContainers;
  }

  public String getPlatformType() {
    return platformType;
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

  public String getDataManglingPolicy() {
    return dataManglingPolicy;
  }

  public void setDataManglingPolicy(String dataManglingPolicy) {
    this.dataManglingPolicy = dataManglingPolicy;
  }

  public List<InstrumentPositionDto> getPositions() {
    return positions;
  }

  public void setPositions(List<InstrumentPositionDto> positions) {
    this.positions = positions;
  }

  public List<SequencingContainerModelDto> getContainerModels() {
    return containerModels;
  }

  public void setContainerModels(List<SequencingContainerModelDto> containerModels) {
    this.containerModels = containerModels;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
