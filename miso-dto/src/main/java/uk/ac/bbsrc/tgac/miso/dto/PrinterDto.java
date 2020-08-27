package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrinterDto {
  private boolean available;
  private String backend;
  private ObjectNode configuration;
  private String driver;
  private double height;
  private long id;
  private ArrayNode layout;
  private String name;
  private double width;

  public String getBackend() {
    return backend;
  }

  public ObjectNode getConfiguration() {
    return configuration;
  }

  public String getDriver() {
    return driver;
  }

  public double getHeight() {
    return height;
  }

  public long getId() {
    return id;
  }

  public ArrayNode getLayout() {
    return layout;
  }

  public String getName() {
    return name;
  }

  public double getWidth() {
    return width;
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

  public void setHeight(double height) {
    this.height = height;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setLayout(ArrayNode layout) {
    this.layout = layout;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWidth(double width) {
    this.width = width;
  }
}
