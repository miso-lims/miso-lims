package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "BoxSize")
public class BoxSize implements Deletable, Serializable {

  public enum BoxType {
    STORAGE("Storage"), PLATE("Plate");

    private final String label;

    private BoxType(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "boxSizeId")
  private long id = UNSAVED_ID;
  @Column(name = "boxSizeRows")
  private Integer boxSizeRows;
  @Column(name = "boxSizeColumns")
  private Integer boxSizeColumns;
  private boolean scannable;
  @Enumerated(EnumType.STRING)
  private BoxType boxType;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  public Integer getRows() {
    return boxSizeRows;
  }

  public void setRows(Integer rows) {
    this.boxSizeRows = rows;
  }

  public Integer getColumns() {
    return boxSizeColumns;
  }

  public void setColumns(Integer columns) {
    this.boxSizeColumns = columns;
  }

  public String getLabel() {
    return Integer.toString(boxSizeRows) + "×" + Integer.toString(boxSizeColumns)
        + " " + getBoxType().getLabel() + (getScannable() ? " (scannable)" : "");
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

  public BoxType getBoxType() {
    return boxType;
  }

  public void setBoxType(BoxType boxType) {
    this.boxType = boxType;
  }

  public Stream<String> positionStream() {
    return IntStream.range(0, boxSizeRows * boxSizeColumns)
        .mapToObj(x -> BoxUtils.getPositionString(x / boxSizeColumns, x % boxSizeColumns));
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, boxSizeColumns, boxSizeRows, scannable, boxType);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        BoxSize::getId,
        BoxSize::getColumns,
        BoxSize::getRows,
        BoxSize::getScannable,
        BoxSize::getBoxType);
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
    return getLabel();
  }

}
