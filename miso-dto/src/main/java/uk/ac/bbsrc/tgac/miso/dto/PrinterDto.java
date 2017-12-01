package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrinterDto {
  private boolean available;
  private String backend;
  private ObjectNode configuration;
  private String driver;
  private long id;
  private String name;

  public String getBackend() {
    return backend;
  }

  public ObjectNode getConfiguration() {
    return configuration;
  }

  public String getDriver() {
    return driver;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void setBackend(String backend) {
    this.backend = backend;
  }

  public void setConfiguration(ObjectNode configuration) {
    this.configuration = configuration;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
