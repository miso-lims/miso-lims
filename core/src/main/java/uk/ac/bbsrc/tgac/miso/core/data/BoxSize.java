package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;

@Entity
@Table(name = "BoxSize")
public class BoxSize implements Deletable, Identifiable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "boxSizeId")
  private long id = UNSAVED_ID;
  private Integer rows;
  private Integer columns;
  private boolean scannable;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
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

  /**
   * Creates a String of number of rows x number of columns.
   * 
   * @return String getRowsByColumns
   */
  public String getRowsByColumns() {
    return Integer.toString(rows) + " × " + Integer.toString(columns);
  }

  public String getRowsByColumnsWithScan() {
    return getRowsByColumns() + (getScannable() ? " scannable" : "");
  }

  /**
   * Returns whether the box is able to be scanned by the bulk scanner
   */
  public boolean getScannable() {
    return scannable;
  }

  /**
   * Sets whether the box is able to be scanned by the bulk scanner
   */
  public void setScannable(boolean scannable) {
    this.scannable = scannable;
  }

  public Stream<String> positionStream() {
    return IntStream.range(0, rows * columns).mapToObj(x -> BoxUtils.getPositionString(x / columns, x % columns));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + columns;
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + rows;
    result = prime * result + (scannable ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BoxSize other = (BoxSize) obj;
    if (columns != other.columns) return false;
    if (id != other.id) return false;
    if (rows != other.rows) return false;
    if (scannable != other.scannable) return false;
    return true;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Box Size";
  }

  @Override
  public String getDeleteDescription() {
    return getRowsByColumnsWithScan();
  }

}
