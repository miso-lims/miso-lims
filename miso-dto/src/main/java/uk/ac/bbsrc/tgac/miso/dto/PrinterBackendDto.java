package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class PrinterBackendDto {
  private List<String> configurationKeys;
  private int id;
  private String name;

  public List<String> getConfigurationKeys() {
    return configurationKeys;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setConfigurationKeys(List<String> configurationKeys) {
    this.configurationKeys = configurationKeys;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }
}
