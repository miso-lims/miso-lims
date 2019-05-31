package uk.ac.bbsrc.tgac.miso.dto;

public class ArrayModelDto {

  private Long id;
  private String alias;
  private Integer rows;
  private Integer Columns;

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

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getColumns() {
    return Columns;
  }

  public void setColumns(Integer columns) {
    Columns = columns;
  }

}
