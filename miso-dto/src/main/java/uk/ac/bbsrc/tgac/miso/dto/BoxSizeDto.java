package uk.ac.bbsrc.tgac.miso.dto;

public class BoxSizeDto {

  private Long id;
  private Integer rows;
  private Integer columns;
  private boolean scannable;
  private String rowsByColumns;
  private String rowsByColumnsWithScan;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getColumns() {
    return columns;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }

  public boolean isScannable() {
    return scannable;
  }

  public void setScannable(boolean scannable) {
    this.scannable = scannable;
  }

  public String getRowsByColumns() {
    return rowsByColumns;
  }

  public void setRowsByColumns(String rowsByColumns) {
    this.rowsByColumns = rowsByColumns;
  }

  public String getRowsByColumnsWithScan() {
    return rowsByColumnsWithScan;
  }

  public void setRowsByColumnsWithScan(String rowsByColumnsWithScan) {
    this.rowsByColumnsWithScan = rowsByColumnsWithScan;
  }

}
