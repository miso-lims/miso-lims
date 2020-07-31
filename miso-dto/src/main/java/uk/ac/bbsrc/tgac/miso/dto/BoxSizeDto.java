package uk.ac.bbsrc.tgac.miso.dto;

public class BoxSizeDto {

  private Long id;
  private Integer rows;
  private Integer columns;
  private boolean scannable;
  private String boxType;
  private String boxTypeLabel;
  private String label;

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

  public String getBoxType() {
    return boxType;
  }

  public void setBoxType(String boxType) {
    this.boxType = boxType;
  }

  public String getBoxTypeLabel() {
    return boxTypeLabel;
  }

  public void setBoxTypeLabel(String boxTypeLabel) {
    this.boxTypeLabel = boxTypeLabel;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

}
