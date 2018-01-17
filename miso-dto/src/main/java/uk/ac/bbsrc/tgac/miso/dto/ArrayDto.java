package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class ArrayDto {

  private Long id;
  private String alias;
  private String arrayModelAlias;
  private Long arrayModelId;
  private int rows;
  private int columns;
  private String serialNumber;
  private String description;
  private List<ArraySampleDto> samples;
  private String lastModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getArrayModelAlias() {
    return arrayModelAlias;
  }

  public void setArrayModelAlias(String arrayModelAlias) {
    this.arrayModelAlias = arrayModelAlias;
  }

  public Long getArrayModelId() {
    return arrayModelId;
  }

  public void setArrayModelId(Long arrayModelId) {
    this.arrayModelId = arrayModelId;
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public int getColumns() {
    return columns;
  }

  public void setColumns(int columns) {
    this.columns = columns;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ArraySampleDto> getSamples() {
    return samples;
  }

  public void setSamples(List<ArraySampleDto> samples) {
    this.samples = samples;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

}
